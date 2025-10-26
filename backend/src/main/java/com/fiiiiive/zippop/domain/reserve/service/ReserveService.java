package com.fiiiiive.zippop.domain.reserve.service;


import com.fiiiiive.zippop.global.base.BaseMessage;
import com.fiiiiive.zippop.global.base.BaseStatus;
import com.fiiiiive.zippop.global.base.BaseException;
import com.fiiiiive.zippop.global.security.normal.CustomUserDetails;
import com.fiiiiive.zippop.domain.reserve.dto.ReserveDto;
import com.fiiiiive.zippop.domain.reserve.entity.Reserve;
import com.fiiiiive.zippop.domain.reserve.repository.ReserveRepository;
import com.fiiiiive.zippop.domain.store.repository.StoreRepository;
import com.fiiiiive.zippop.domain.store.entity.Store;
import com.fiiiiive.zippop.global.service.JwtService;
import com.fiiiiive.zippop.global.service.RedisService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReserveService {

    private final ReserveRepository reserveRepository;
    private final StoreRepository storeRepository;
    private final JwtService jwtService;
    private final RedisService redisService;
    private final SimpMessagingTemplate messagingTemplate;

    // 예약 생성
    @Transactional
    public ReserveDto.CreateReserveRes registerReserve(CustomUserDetails customUserDetails, ReserveDto.CreateReserveReq dto) throws BaseException {

        // 스토어 조회(storeIdx)
        Store store = storeRepository.findById(dto.getStoreIdx()).orElseThrow(
                () -> new BaseException(BaseMessage.RESERVE_REGISTER_FAIL_NOT_FOUND_STORE)
        );

        // 스토어 상태 확인(종료 상태면 예약 생성 불가)
        if(store.getStatus() == BaseStatus.STORE_END) {
            throw new BaseException(BaseMessage.RESERVE_REGISTER_FAIL_STORE_ENDED);
        }

        // 스토어 소유 확인
        if(!Objects.equals(store.getCompanyEmail(), customUserDetails.getEmail())) {
            throw new BaseException(BaseMessage.RESERVE_REGISTER_FAIL_INVALID_MEMBER);
        }

        // 스토어 최대 예약자 수를 넘었는지 확인
        if(store.getTotalPeople() <= 0) {
            throw new BaseException(BaseMessage.RESERVE_REGISTER_FAIL_LIMIT_EXCEEDED);
        }

        // 예약 저장
        String workingUUID = UUID.randomUUID().toString();
        String waitingUUID = UUID.randomUUID().toString();
        Reserve reserve = dto.toEntity(store, workingUUID, waitingUUID);
        reserveRepository.save(reserve);

        // Redis 큐 초기화 (예약 종료 시간까지 유효)
        long expirationMinutes = java.time.Duration.between(java.time.LocalDateTime.now(), dto.getReserveEndTime()).toMinutes();
        if (expirationMinutes <= 0) {
            throw new BaseException(BaseMessage.RESERVE_REGISTER_FAIL_TIME_CLOSED);
        }

        redisService.createQueue(workingUUID, expirationMinutes);
        redisService.createQueue(waitingUUID, expirationMinutes);
        log.info("Redis 큐 생성 완료 - workingUUID: {}, waitingUUID: {}, 만료시간: {}분", workingUUID, waitingUUID, expirationMinutes);

        // 스토어 저장(총 예약자 수 - 남은 예약자 수)
        store.setTotalPeople(store.getTotalPeople() - dto.getReservePeople());
        storeRepository.save(store);

        // DTO 반환
        return ReserveDto.CreateReserveRes.builder().reserveIdx(reserve.getIdx()).build();

    }

    // 예약삭제
    @Transactional
    public void deleteReserve(CustomUserDetails customUserDetails, Long storeIdx, Long reserveIdx) throws BaseException {

        // 스토어 조회(storeIdx)
        Store store = storeRepository.findById(storeIdx).orElseThrow(
                () -> new BaseException(BaseMessage.RESERVE_DELETE_FAIL_NOT_FOUND_STORE)
        );

        // 스토어 소유 확인
        if(!Objects.equals(store.getCompanyEmail(), customUserDetails.getEmail())) {
            throw new BaseException(BaseMessage.RESERVE_DELETE_FAIL_INVALID_MEMBER);
        }

        // 예약 조회
        Reserve reserve = reserveRepository.findById(reserveIdx).orElseThrow(
                () -> new BaseException(BaseMessage.RESERVE_DELETE_FAIL_NOT_FOUND)
        );

        // 예약 종료시간이 지나서 취소할때
        if(reserve.getEndTime().isBefore(LocalDateTime.now())) {
            throw new  BaseException(BaseMessage.RESERVE_DELETE_FAIL_END_TIME);
        }

        // 인원수 복구
        store.setTotalPeople(store.getTotalPeople() + reserve.getTotalPeople());
        storeRepository.save(store);

        // 예약 삭제
        reserveRepository.deleteById(reserveIdx);
        redisService.deleteQueue(reserve.getWorkingUUID(), reserve.getWaitingUUID());

    }

    // 예약 등록
    public ReserveDto.EnrollReserveRes enrollReserve(HttpServletResponse res, CustomUserDetails customUserDetails, Long reserveIdx) throws BaseException {
        // 예약 조회(reserveIdx)
        Reserve reserve = reserveRepository.findById(reserveIdx).orElseThrow(
                () -> new BaseException(BaseMessage.RESERVE_ENROLL_FAIL_NOT_FOUND)
        );

        String email = customUserDetails.getEmail();
        String response;

        // 예약 접속 큐에 사용자가 이미 있는지 확인 (재접속)
        Long currentWorkingOrder = redisService.getOrder(reserve.getWorkingUUID(), customUserDetails.getEmail());
        if (currentWorkingOrder != null) {
            String token = jwtService.createReserveToken(reserveIdx, customUserDetails.getEmail());
            res.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
            Cookie aToken = new Cookie("WTOKEN", token);
            aToken.setHttpOnly(true);
            aToken.setSecure(true);
            aToken.setPath("/");
            aToken.setMaxAge(60 * 10);
            res.addCookie(aToken);
            response = "예약 접속 재접속 -> 접속 번호: " + currentWorkingOrder + 1;
            log.info("예약 재접속: {}, 순번: {}", customUserDetails.getEmail(), currentWorkingOrder + 1);
            return ReserveDto.EnrollReserveRes.builder().response(response).build();
        }

        // 대기 큐에 사용자가 이미 있는지 확인 (재접속)
        Long currentWaitingOrder = redisService.getOrder(reserve.getWaitingUUID(), email);
        if (currentWaitingOrder != null) {
            response = "예약 대기 재접속 -> 대기 순번: " + currentWaitingOrder + 1;
            return ReserveDto.EnrollReserveRes.builder().response(response).build();
        }

        // 신규 사용자 - Redisson 분산 락으로 원자적 등록 (Race Condition 완벽 해결)
        long timestamp = System.currentTimeMillis();
        boolean enrolledToWorking = redisService.enrollQueueWithLock(reserve.getWorkingUUID(),email,timestamp,reserve.getTotalPeople());
        
        if (enrolledToWorking) {
            // 예약큐에 등록 성공시 토큰 발급
            String token = jwtService.createReserveToken(reserveIdx, email);
            res.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
            Cookie aToken = new Cookie("WTOKEN", token);
            aToken.setHttpOnly(true);
            aToken.setSecure(true);
            aToken.setPath("/");
            aToken.setMaxAge(60 * 10);
            res.addCookie(aToken);
            response = "예약 접속 성공 -> 접속 번호: " + redisService.getOrder(reserve.getWorkingUUID(), email) + 1;
            log.info("[Redisson Lock] 예약큐 등록 성공: {}, 순번: {}", email, redisService.getOrder(reserve.getWorkingUUID(), email) + 1);
        } else {
            // 예약큐가 가득 참 -> 대기큐에 등록
            redisService.enrollQueue(reserve.getWaitingUUID(), email, timestamp);
            response = "예약 대기 등록 -> 대기 순번: " + redisService.getOrder(reserve.getWaitingUUID(), email) + 1;
            log.info("[Redisson Lock] 대기큐 등록: {}, 순번: {}", email, redisService.getOrder(reserve.getWaitingUUID(), email) + 1);
        }

        return ReserveDto.EnrollReserveRes.builder().response(response).build();
    }

    // 예약 취소 (개선: Lua Script + 토큰 발급)
    public String cancelReserve(HttpServletRequest req, HttpServletResponse res, CustomUserDetails customUserDetails, Long reserveIdx, Long storeIdx) throws BaseException {

        // 예약 조회(reserveIdx)
        Reserve reserve = reserveRepository.findById(reserveIdx).orElseThrow(
                () -> new BaseException(BaseMessage.RESERVE_CANCEL_FAIL)
        );


        // 현재 사용자가 예약 큐에 있는 경우
        if (redisService.getOrder(reserve.getWorkingUUID(), customUserDetails.getEmail()) != null) {
            // 현재 사용자를 예약 큐에서 삭제
            redisService.remove(reserve.getWorkingUUID(), customUserDetails.getEmail());
            
            // 쿠키에서 WTOKEN 추출 및 블랙리스트 추가
            Cookie[] cookies = req.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("WTOKEN".equals(cookie.getName())) {
                        String wtoken = cookie.getValue();
                        if (wtoken != null && !wtoken.isEmpty()) {
                            redisService.blacklistReserveToken(wtoken);
                            log.info("예약 토큰 블랙리스트 추가: {}", wtoken);
                        }
                        break;
                    }
                }
            }
            
            // 토큰 쿠키 삭제
            Cookie deleteCookie = new Cookie("WTOKEN", null);
            deleteCookie.setHttpOnly(true);
            deleteCookie.setSecure(true);
            deleteCookie.setPath("/");
            deleteCookie.setMaxAge(0);  // 쿠키 만료 처리
            res.addCookie(deleteCookie);
            
            log.info("예약 취소: {}, 예약큐에서 제거", customUserDetails.getEmail());
            
            // 대기자 → 예약자 이동
            String firstWaitingUser = redisService.firstWaitingUserToWorking(reserve.getWorkingUUID(), reserve.getWaitingUUID(), reserve.getTotalPeople());
            
            // 대기자가 승격된 경우 토큰 발급 및 알림
            if(firstWaitingUser != null) {

                // 토큰 생성
                String wtoken = jwtService.createReserveToken(reserveIdx, firstWaitingUser);

                // 현재 큐 상태 조회
                String workingTotal = redisService.getSize(reserve.getWorkingUUID());
                String waitingTotal = redisService.getSize(reserve.getWaitingUUID());
                Long newWorkingOrder = redisService.getOrder(reserve.getWorkingUUID(), firstWaitingUser);
                String statusMessage = "예약 승격! 예약접속자: " + workingTotal + " 예약대기자: " + waitingTotal + " 현재 순번: " + (newWorkingOrder + 1);

                // WebSocket으로 토큰과 함께 알림 전송
                messagingTemplate.convertAndSendToUser(
                    firstWaitingUser,
                    "/reserve/status",
                    ReserveDto.StatusReserveRes.toDataWithToken(workingTotal, waitingTotal, statusMessage, 1, wtoken)
                );

                log.info("승격 알림 전송 완료: {}, 토큰 포함", firstWaitingUser);
            }
        } else {
            // 현재 사용자가 대기 큐에 있는 경우
            Long waitingOrder = redisService.getOrder(reserve.getWaitingUUID(), customUserDetails.getEmail());
            if (waitingOrder != null) {
                redisService.remove(reserve.getWaitingUUID(), customUserDetails.getEmail());
                log.info("예약 취소: {}, 대기큐에서 제거", customUserDetails.getEmail());
            }
        }
        
        return "예약을 취소했습니다.";
    }

    // 소켓 방식
    public void status(Principal principal, ReserveDto.StatusReserveReq dto) throws BaseException {

        Reserve reserve = reserveRepository.findById(dto.getReserveIdx()).orElseThrow(
                () -> new BaseException(BaseMessage.RESERVE_SEARCH_STATUS_FAIL_NOT_FOUND)
        );

        // 접속자
        String workingTotal = redisService.getSize(reserve.getWorkingUUID());

        // 대기자
        String waitingTotal = redisService.getSize(reserve.getWaitingUUID());

        // 현재 위치
        Long currentWorkingOrder = redisService.getOrder(reserve.getWorkingUUID(), principal.getName());

        // 결제 페이지로 접근 가능한지 여부
        int access;

        // 클라이언트로 전송할 상태 메시지
        String statusMessage;
        if (currentWorkingOrder == null) {

            Long currentWaitingOrder = redisService.getOrder(reserve.getWaitingUUID(), principal.getName());
            statusMessage = "예약접속자: " + workingTotal + " 예약대기자: " + waitingTotal + " 현재 순번: " + (currentWaitingOrder + 1);
            access = 0; // 대기 큐에 있으면 access는 0
        } else {
            statusMessage = "예약접속자: " + workingTotal + " 예약대기자: " + waitingTotal + " 현재 순번: " + (currentWorkingOrder + 1);
            access = 1; // 예약 접속 큐에 있으면 access는 1
        }

        if(reserve.getEndTime().isBefore(LocalDateTime.now())) {
            statusMessage = "예약이 종료되었습니다.";
            access = 2; // 예약 종료 2
        }

        if(reserve.getTotalPeople() <= 0) {
            statusMessage = "예약이 마감되었습니다.";
            access = 3; // 예약 마감 3
        }

        // 특정 사용자에게만 상태 정보 전송
        messagingTemplate.convertAndSendToUser(
                principal.getName(), // 사용자 이름으로 특정 사용자에게 전송
                "/reserve/status",
                ReserveDto.StatusReserveRes.toData(workingTotal, waitingTotal, statusMessage, access)
        );
        log.info("Sending message to user: {}, {}", principal.getName(), access);
    }

    // 예약 목록 조회
    public Page<ReserveDto.SearchReserveRes> searchAllReserve(Long storeIdx, String keyword, int page, int size) throws BaseException {

        // 예약 조회(status, storeIdx, keyword)
        Page<Reserve> reservePage;
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        if(storeIdx == null){
            if(keyword == null) reservePage = reserveRepository.findAllByStatus(BaseStatus.valueOf("STORE_START"), pageable);
            else reservePage = reserveRepository.findAllByKeywordAndStatus(keyword, BaseStatus.valueOf("STORE_START"), pageable);
        } else {
            reservePage = reserveRepository.findAllByStoreIdx(storeIdx, BaseStatus.valueOf("STORE_START"), pageable);
        }

        return Reserve.toDtoPage(reservePage);
    }

    // 예약 목록 조회(기업용)
    public Page<ReserveDto.SearchReserveRes> searchAllReserveAsCompany(CustomUserDetails customUserDetails, Long storeIdx, int page, int size) throws BaseException {

        Page<Reserve> reservePage = reserveRepository.findAllByCompanyEmail(storeIdx, customUserDetails.getEmail(), PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"))).orElseThrow(
                () -> new BaseException(BaseMessage.RESERVE_SEARCH_ALL_FAIL_NOT_FOUND)
        );

        return Reserve.toDtoPage(reservePage);
    }


}

