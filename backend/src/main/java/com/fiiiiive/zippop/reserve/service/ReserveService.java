package com.fiiiiive.zippop.reserve.service;


import com.fiiiiive.zippop.global.base.BaseMessage;
import com.fiiiiive.zippop.global.base.BaseStatus;
import com.fiiiiive.zippop.global.base.BaseException;
import com.fiiiiive.zippop.global.security.normal.CustomUserDetails;
import com.fiiiiive.zippop.reserve.dto.ReserveDto;
import com.fiiiiive.zippop.reserve.entity.Reserve;
import com.fiiiiive.zippop.reserve.repository.ReserveRepository;
import com.fiiiiive.zippop.store.repository.StoreRepository;
import com.fiiiiive.zippop.store.entity.Store;
import com.fiiiiive.zippop.global.service.JwtService;
import com.fiiiiive.zippop.global.service.RedisService;
import jakarta.servlet.http.Cookie;
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

    // 토큰 발급
    private void issueWToken(HttpServletResponse res, Long reserveIdx, String userId) {
        String token = jwtService.createReserveToken(reserveIdx, userId);
        res.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        Cookie aToken = new Cookie("WTOKEN", token);
        aToken.setHttpOnly(true);
        aToken.setSecure(true);
        aToken.setPath("/");
        aToken.setMaxAge(60 * 1); // 1분간 유효
        res.addCookie(aToken);
    }

    // 토큰 삭제
    private void deleteWToken(HttpServletResponse response) {
        Cookie deleteCookie = new Cookie("WTOKEN", null);
        deleteCookie.setHttpOnly(true);
        deleteCookie.setSecure(true);
        deleteCookie.setPath("/");
        deleteCookie.setMaxAge(0);  // 쿠키 만료 처리
        response.addCookie(deleteCookie);
    }

    // 예약 생성
    @Transactional
    public ReserveDto.CreateReserveRes registerReserve(CustomUserDetails customUserDetails, ReserveDto.CreateReserveReq dto) throws BaseException {

        // 스토어 조회(storeIdx)
        Store store = storeRepository.findById(dto.getStoreIdx()).orElseThrow(
                () -> new BaseException(BaseMessage.RESERVE_REGISTER_FAIL_NOT_FOUND_STORE)
        );

        // 스토어 상태 확인(종료 상태면 예약 생성 불가)
        if(store.getStatus() == BaseStatus.STORE_END) throw new BaseException(BaseMessage.RESERVE_REGISTER_FAIL_STORE_ENDED);

        // 스토어 소유 확인
        if(!Objects.equals(store.getCompanyEmail(), customUserDetails.getEmail())) throw new BaseException(BaseMessage.RESERVE_REGISTER_FAIL_INVALID_MEMBER);

        // 스토어 최대 예약자 수를 넘었는지 확인
        if(store.getTotalPeople() <= 0) throw new BaseException(BaseMessage.RESERVE_REGISTER_FAIL_LIMIT_EXCEEDED);

        // 예약 저장
        Reserve reserve = dto.toEntity(store, UUID.randomUUID().toString(), UUID.randomUUID().toString());
        reserveRepository.save(reserve);

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
        if(!Objects.equals(store.getCompanyEmail(), customUserDetails.getEmail())) throw new BaseException(BaseMessage.RESERVE_DELETE_FAIL_INVALID_MEMBER);

        // 예약 조회
        Reserve reserve = reserveRepository.findById(reserveIdx).orElseThrow(
                () -> new BaseException(BaseMessage.RESERVE_DELETE_FAIL_NOT_FOUND)
        );

        // 예약 종료시간이 지나서 취소할때
        if(reserve.getEndTime().isBefore(LocalDateTime.now())) throw new  BaseException(BaseMessage.RESERVE_DELETE_FAIL_END_TIME);

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

        // 예약 접속 큐의 현재 크기 및 사용자의 순서 조회
        Long currentOrder = redisService.getOrder(reserve.getWorkingUUID(), customUserDetails.getEmail());
        Long workingQueueSize = redisService.getSize(reserve.getWorkingUUID());
        long rank;
        String response = "";

        // 예약 접속 큐에 사용자가 이미 있는 경우 -> 사이트 재접속인 경우
        if (currentOrder != null) {
            issueWToken(res, reserveIdx, customUserDetails.getEmail()); // 이미 등록되어 있으므로 토큰을 발급
            rank = currentOrder + 1; // 0부터 시작하므로 +1 해서 순위를 반환

            response = "예약 접속 -> 접속 번호: " + rank;
        }

        // 예약 접속 큐에 자리가 있는 경우 (현재 예약자 수보다 큐의 크기가 작은 경우)
        if (workingQueueSize < reserve.getTotalPeople()) {
            // 새로운 사용자 등록
            redisService.enrollQueue(reserve.getWorkingUUID(), customUserDetails.getEmail(), System.currentTimeMillis());
            issueWToken(res, reserveIdx, customUserDetails.getEmail());
            rank = redisService.getOrder(reserve.getWorkingUUID(), customUserDetails.getEmail()) + 1;
            response = "예약 접속 -> 접속 번호: " + rank;
        }

        // 예약 접속 큐가 꽉 찬 경우 -> 대기 큐에 추가
        if (workingQueueSize >= reserve.getTotalPeople()) {
            redisService.enrollQueue(reserve.getWaitingUUID(), customUserDetails.getEmail(), System.currentTimeMillis());
            rank = redisService.getOrder(reserve.getWaitingUUID(), customUserDetails.getEmail()) + 1;
            response = "예약 대기 -> 접속 번호: " + rank;
        }

        return ReserveDto.EnrollReserveRes.builder().response(response).build();

    }

    // 예약 취소
    public String cancelReserve(HttpServletResponse res,CustomUserDetails customUserDetails, Long reserveIdx) throws BaseException {

        // 예약 조회(reserveIdx)
        Reserve reserve = reserveRepository.findById(reserveIdx).orElseThrow(
                () -> new BaseException(BaseMessage.RESERVE_CANCEL_FAIL)
        );

        Long currentOrder = redisService.getOrder(reserve.getWorkingUUID(), customUserDetails.getEmail());
        // if: 현재 사용자가 예약 큐에 있는 경우
        // else: 현재 사용자가 대기큐에 있는 경우
        if (currentOrder != null) {
            // 현재 사용자 예약 접속 redis에서 삭제, 토큰 삭제
            redisService.remove(reserve.getWorkingUUID(), customUserDetails.getEmail());
            deleteWToken(res);
            String firstWaitingUser = redisService.firstWaitingUserToWorking(reserve.getWorkingUUID(), reserve.getWaitingUUID(), reserve.getTotalPeople());
            if(firstWaitingUser != null ) {
                // 대기자에서 예약자로 이동한 사용자에게 WebSocket 알림 전송
                String workingTotal = redisService.getAllValues(reserve.getWorkingUUID());
                String waitingTotal = redisService.getAllValues(reserve.getWaitingUUID());
                Long newWorkingOrder = redisService.getOrder(reserve.getWorkingUUID(), firstWaitingUser);
                String statusMessage = "예약접속자: " + workingTotal + " 예약대기자: " + waitingTotal + " 현재 순번: " + (newWorkingOrder + 1);
                
                messagingTemplate.convertAndSendToUser(
                        firstWaitingUser,
                        "/reserve/status",
                        ReserveDto.StatusReserveRes.toData(workingTotal, waitingTotal, statusMessage, 1)
                );
            }
        } else {
            Long waitingOrder = redisService.getOrder(reserve.getWaitingUUID(), customUserDetails.getEmail());
            if (waitingOrder != null) {
                // 대기 큐에서 현재 사용자 제거
                redisService.remove(reserve.getWaitingUUID(), customUserDetails.getEmail());
            }
        }
        return "예약을 취소했습니다.";

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

    // 소켓 방식
    public void status(Principal principal, ReserveDto.StatusReserveReq dto) throws BaseException {

        Reserve reserve = reserveRepository.findById(dto.getReserveIdx()).orElseThrow(
                () -> new BaseException(BaseMessage.RESERVE_SEARCH_STATUS_FAIL_NOT_FOUND)
        );
        // 접속자
        String workingTotal = redisService.getAllValues(reserve.getWorkingUUID());

        // 대기자
        String waitingTotal = redisService.getAllValues(reserve.getWaitingUUID());

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
        log.info("Sending message to user: {}", principal.getName(), access);
    }

//    // 폴링방식
//    public String status(CustomUserDetails customUserDetails, Long reserveIdx) throws BaseException {
//        Reserve reserve = reserveRepository.findById(reserveIdx).orElseThrow(
//                () -> new BaseException(BaseMessage.RESERVE_SEARCH_STATUS_FAIL_NOT_FOUND)
//        );
//        String waitingTotal = redisService.getAllValues(reserve.getWaitingUUID());
//        String workingTotal = redisService.getAllValues(reserve.getWorkingUUID());
//        Long currentWorkingOrder = redisService.getOrder(reserve.getWorkingUUID(), customUserDetails.getUserId());
//        if(currentWorkingOrder == null){
//            Long currentWaitingOrder = redisService.getOrder(reserve.getWaitingUUID(), customUserDetails.getUserId());
//            return " 예약접속자 " + workingTotal + " 예약대기자 " + waitingTotal + "현재 순번" + (currentWaitingOrder + 1);
//        }
//        return " 예약접속자 " + workingTotal + " 예약대기자 " + waitingTotal + "현재 순번" + (currentWorkingOrder + 1);
//    }

}

