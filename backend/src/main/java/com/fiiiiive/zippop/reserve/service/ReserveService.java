package com.fiiiiive.zippop.reserve.service;


import com.fiiiiive.zippop.global.base.ServiceErrorCode;
import com.fiiiiive.zippop.global.base.ServiceException;
import com.fiiiiive.zippop.global.enums.PopupStatus;
import com.fiiiiive.zippop.global.security.normal.CustomUserDetails;
import com.fiiiiive.zippop.popup.policy.PopupPolicy;
import com.fiiiiive.zippop.reserve.model.dto.*;
import com.fiiiiive.zippop.reserve.model.entity.Reserve;
import com.fiiiiive.zippop.reserve.repository.ReserveRepository;
import com.fiiiiive.zippop.popup.repository.PopupRepository;
import com.fiiiiive.zippop.popup.model.entity.Popup;
import com.fiiiiive.zippop.global.crypto.JwtService;
import com.fiiiiive.zippop.global.redis.RedisQueueService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReserveService {

    private final ReserveRepository reserveRepository;
    private final PopupRepository popupRepository;
    private final PopupPolicy popupPolicy;
    private final JwtService jwtService;
    private final RedisQueueService redisQueueService;
    private final SimpMessagingTemplate messagingTemplate;

    // 예약 생성
    @Transactional
    public CreateReserveRes createReserve(CustomUserDetails user, CreateReserveReq req) throws ServiceException {

        // 팝업 조회(popupIdx)
        Popup popup = popupRepository.findById(req.getPopupIdx()).orElseThrow(
                () -> new ServiceException(ServiceErrorCode.RESERVE_REGISTER_FAIL_NOT_FOUND_STORE)
        );

        // 팝업 상태 확인(종료 상태면 예약 생성 불가)
        popupPolicy.validateReserveStatus(popup, PopupStatus.POPUP_END);

        // 팝업 소유 확인
        popupPolicy.validateOwner(popup, user);

        // 팝업 최대 예약자 수를 넘었는지 확인
        popup.validateTotalPeople();

        // 예약 저장
        String workingUUID = UUID.randomUUID().toString();
        String waitingUUID = UUID.randomUUID().toString();
        Reserve reserve = Reserve.create(
                popup,
                workingUUID,
                waitingUUID,
                req.getReservePeople(),
                req.getReserveStartDate(),
                req.getReserveStartTime(),
                req.getReserveEndTime()
        );
        reserveRepository.save(reserve);

        // Redis 큐 초기화 (예약 종료 시간까지 유효)
        long expirationMinutes = java.time.Duration.between(java.time.LocalDateTime.now(), req.getReserveEndTime()).toMinutes();
        if (expirationMinutes <= 0) {
            throw new ServiceException(ServiceErrorCode.RESERVE_REGISTER_FAIL_TIME_CLOSED);
        }

        redisQueueService.createQueue(workingUUID, expirationMinutes);
        redisQueueService.createQueue(waitingUUID, expirationMinutes);
        log.info("Redis 큐 생성 완료 - workingUUID: {}, waitingUUID: {}, 만료시간: {}분", workingUUID, waitingUUID, expirationMinutes);

        // 팝업 저장(총 예약자 수 - 남은 예약자 수)
        popup.setTotalPeople(popup.getTotalPeople() - req.getReservePeople());

        // DTO 반환
        return CreateReserveRes.builder().reserveIdx(reserve.getIdx()).build();

    }

    // 예약삭제
    @Transactional
    public void deleteReserve(CustomUserDetails user, Long reserveIdx) throws ServiceException {

        // 예약 조회
        Reserve reserve = reserveRepository.findById(reserveIdx).orElseThrow(
                () -> new ServiceException(ServiceErrorCode.RESERVE_DELETE_FAIL_NOT_FOUND)
        );

        // 스토어 조회(storeIdx)
        Popup popup = popupRepository.findById(reserve.getPopup().getIdx()).orElseThrow(
                () -> new ServiceException(ServiceErrorCode.RESERVE_DELETE_FAIL_NOT_FOUND_STORE)
        );

        // 팝업 소유 확인
        popupPolicy.validateOwner(reserve.getPopup(), user);

        // 예약 종료시간이 지나서 취소할때
        if(reserve.getEndTime().isBefore(LocalDateTime.now())) {
            throw new  ServiceException(ServiceErrorCode.RESERVE_DELETE_FAIL_END_TIME);
        }

        // 인원수 복구
        popup.setTotalPeople(popup.getTotalPeople() + reserve.getTotalPeople());

        // 예약 삭제
        reserveRepository.delete(reserve);
        redisQueueService.deleteQueue(reserve.getWorkingUUID(), reserve.getWaitingUUID());

    }

    // 예약 등록
    public EnrollReserveRes enrollReserve(HttpServletResponse res, CustomUserDetails user, Long reserveIdx) throws ServiceException {
        // 예약 조회(reserveIdx)
        Reserve reserve = reserveRepository.findById(reserveIdx).orElseThrow(
                () -> new ServiceException(ServiceErrorCode.RESERVE_ENROLL_FAIL_NOT_FOUND)
        );

        String email = user.getEmail();
        String response;

        // 예약 접속 큐에 사용자가 이미 있는지 확인 (재접속)
        Long currentWorkingOrder = redisQueueService.getOrder(reserve.getWorkingUUID(), user.getEmail());
        if (currentWorkingOrder != null) {
            String token = jwtService.createReserveToken(reserveIdx, user.getEmail());
            res.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
            Cookie aToken = new Cookie("WTOKEN", token);
            aToken.setHttpOnly(true);
            aToken.setSecure(true);
            aToken.setPath("/");
            aToken.setMaxAge(60 * 10);
            res.addCookie(aToken);
            response = "예약 접속 재접속 -> 접속 번호: " + (currentWorkingOrder + 1);
            log.info("예약 재접속: {}, 순번: {}", user.getEmail(), currentWorkingOrder + 1);
            return EnrollReserveRes.builder().response(response).build();
        }

        // 대기 큐에 사용자가 이미 있는지 확인 (재접속)
        Long currentWaitingOrder = redisQueueService.getOrder(reserve.getWaitingUUID(), email);
        if (currentWaitingOrder != null) {
            response = "예약 대기 재접속 -> 대기 순번: " + (currentWaitingOrder + 1);
            return EnrollReserveRes.builder().response(response).build();
        }

        // 신규 사용자 - Redisson 분산 락으로 원자적 등록
        long timestamp = System.currentTimeMillis();
        boolean enrolledToWorking = redisQueueService.enrollQueueWithLock(reserve.getWorkingUUID(),email,timestamp,reserve.getTotalPeople());
        
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
            Long workingOrder = redisQueueService.getOrder(reserve.getWorkingUUID(), email);
            response = "예약 접속 성공 -> 접속 번호: " + (workingOrder != null ? workingOrder + 1 : 1);
            log.info("[Redisson Lock] 예약큐 등록 성공: {}, 순번: {}", email, workingOrder != null ? workingOrder + 1 : 1);
        } else {
            // 예약큐가 가득 참 -> 대기큐에 등록
            redisQueueService.enrollQueue(reserve.getWaitingUUID(), email, timestamp);
            Long waitingOrder = redisQueueService.getOrder(reserve.getWaitingUUID(), email);
            response = "예약 대기 등록 -> 대기 순번: " + (waitingOrder != null ? waitingOrder + 1 : 1);
            log.info("[Redisson Lock] 대기큐 등록: {}, 순번: {}", email, waitingOrder != null ? waitingOrder + 1 : 1);
        }

        return EnrollReserveRes.builder().response(response).build();
    }

    // 예약 취소 (개선: Lua Script + 토큰 발급)
    public String cancelReserve(HttpServletRequest req, HttpServletResponse res, CustomUserDetails user, Long reserveIdx) throws ServiceException {

        // 예약 조회(reserveIdx)
        Reserve reserve = reserveRepository.findById(reserveIdx).orElseThrow(
                () -> new ServiceException(ServiceErrorCode.RESERVE_CANCEL_FAIL)
        );

        // 현재 사용자가 예약 큐에 있는 경우
        if (redisQueueService.getOrder(reserve.getWorkingUUID(), user.getEmail()) != null) {
            // 현재 사용자를 예약 큐에서 삭제
            redisQueueService.remove(reserve.getWorkingUUID(), user.getEmail());
            
            // 쿠키에서 WTOKEN 추출 및 블랙리스트 추가
            Cookie[] cookies = req.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("WTOKEN".equals(cookie.getName())) {
                        String wtoken = cookie.getValue();
                        if (wtoken != null && !wtoken.isEmpty()) {
                            redisQueueService.blacklistReserveToken(wtoken);
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
            
            log.info("예약 취소: {}, 예약큐에서 제거", user.getEmail());
            
            // 대기자 → 예약자 이동
            String firstWaitingUser = redisQueueService.firstWaitingUserToWorking(reserve.getWorkingUUID(), reserve.getWaitingUUID(), reserve.getTotalPeople());
            
            // 대기자가 승격된 경우 토큰 발급 및 알림
            if(firstWaitingUser != null) {

                // 토큰 생성
                String wtoken = jwtService.createReserveToken(reserveIdx, firstWaitingUser);

                // 현재 큐 상태 조회
                String workingTotal = redisQueueService.getSize(reserve.getWorkingUUID());
                String waitingTotal = redisQueueService.getSize(reserve.getWaitingUUID());
                Long newWorkingOrder = redisQueueService.getOrder(reserve.getWorkingUUID(), firstWaitingUser);
                String statusMessage = "예약 승격! 예약접속자: " + workingTotal + " 예약대기자: " + waitingTotal + " 현재 순번: " + (newWorkingOrder + 1);

                // WebSocket으로 토큰과 함께 알림 전송
                messagingTemplate.convertAndSendToUser(
                    firstWaitingUser,
                    "/reserve/status",
                    GetReserveQueueRes.toDataWithToken(workingTotal, waitingTotal, statusMessage, 1, wtoken)
                );

                log.info("승격 알림 전송 완료: {}, 토큰 포함", firstWaitingUser);
            }
        } else {
            // 현재 사용자가 대기 큐에 있는 경우
            Long waitingOrder = redisQueueService.getOrder(reserve.getWaitingUUID(), user.getEmail());
            if (waitingOrder != null) {
                redisQueueService.remove(reserve.getWaitingUUID(), user.getEmail());
                log.info("예약 취소: {}, 대기큐에서 제거", user.getEmail());
            }
        }
        
        return "예약을 취소했습니다.";
    }

    // 소켓 방식
    public void status(Principal principal, GetReserveQueueReq req) throws ServiceException {

        Reserve reserve = reserveRepository.findById(req.getReserveIdx()).orElseThrow(
                () -> new ServiceException(ServiceErrorCode.RESERVE_SEARCH_STATUS_FAIL_NOT_FOUND)
        );

        // 접속자
        String workingTotal = redisQueueService.getSize(reserve.getWorkingUUID());

        // 대기자
        String waitingTotal = redisQueueService.getSize(reserve.getWaitingUUID());

        // 현재 위치
        Long currentWorkingOrder = redisQueueService.getOrder(reserve.getWorkingUUID(), principal.getName());

        // 결제 페이지로 접근 가능한지 여부
        int access;

        // 클라이언트로 전송할 상태 메시지
        String statusMessage;
        if (currentWorkingOrder == null) {
            Long currentWaitingOrder = redisQueueService.getOrder(reserve.getWaitingUUID(), principal.getName());
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
                GetReserveQueueRes.toData(workingTotal, waitingTotal, statusMessage, access)
        );
        log.info("Sending message to user: {}, {}", principal.getName(), access);
    }

    // Redis 큐 관리 (만료 큐 정리 + 복구)
    public void managementReserveQueue() {
        List<Reserve> reserveList = reserveRepository.findAllByStartDate(LocalDate.now());
        if (reserveList.isEmpty()) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();

        for (Reserve reserve : reserveList) {
            // 종료 시간이 지난 예약: Redis 큐 삭제 (정리)
            if (reserve.getEndTime().isBefore(now)) {
                redisQueueService.deleteQueue(reserve.getWorkingUUID(), reserve.getWaitingUUID());
                log.debug("만료 큐 삭제 - 예약 ID: {}, 스토어: {}", reserve.getIdx(), reserve.getPopup().getName());
                continue;
            }

            // 진행 중인 예약: 큐가 없으면 생성 (복구)
            // 현재 시간부터 종료 시간까지의 남은 시간 계산 (분 단위)
            long remainingMinutes = Duration.between(now, reserve.getEndTime()).toMinutes();

            if (remainingMinutes <= 0) {
                continue;
            }

            // Working Queue 확인 및 생성 (큐가 없으면 생성)
            if (redisQueueService.existQueue(reserve.getWorkingUUID())) {
                redisQueueService.createQueue(reserve.getWorkingUUID(), remainingMinutes);
                log.debug("Working 큐 생성: {} (예약 ID: {})", reserve.getWorkingUUID(), reserve.getIdx());
            }

            // Waiting Queue 확인 및 생성 (큐가 없으면 생성)
            if (redisQueueService.existQueue(reserve.getWaitingUUID())) {
                redisQueueService.createQueue(reserve.getWaitingUUID(), remainingMinutes);
                log.debug("Waiting 큐 생성: {} (예약 ID: {})", reserve.getWaitingUUID(), reserve.getIdx());
            }
        }
    }


}

