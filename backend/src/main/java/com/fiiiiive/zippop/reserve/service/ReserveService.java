package com.fiiiiive.zippop.reserve.service;


import com.fiiiiive.zippop.global.base.ServiceErrorCode;
import com.fiiiiive.zippop.global.base.ServiceException;
import com.fiiiiive.zippop.global.security.crypto.JwtService;
import com.fiiiiive.zippop.global.security.normal.CustomUserDetails;
import com.fiiiiive.zippop.popup.model.entity.Popup;
import com.fiiiiive.zippop.popup.model.enums.PopupStatus;
import com.fiiiiive.zippop.popup.repository.PopupRepository;
import com.fiiiiive.zippop.reserve.infrastructure.redis.ReserveRedisService;
import com.fiiiiive.zippop.reserve.model.dto.*;
import com.fiiiiive.zippop.reserve.model.entity.Reserve;
import com.fiiiiive.zippop.reserve.model.enums.EnrollResult;
import com.fiiiiive.zippop.reserve.repository.ReserveRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.security.Principal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReserveService {

    private final ReserveRepository reserveRepository;
    private final PopupRepository popupRepository;
    private final JwtService jwtService;
    private final ReserveRedisService reserveRedisService;
    private final SimpMessagingTemplate messagingTemplate;

    // 예약 생성
    @Transactional
    public CreateReserveRes createReserve(CustomUserDetails user, CreateReserveReq req) throws ServiceException {

        // 팝업 조회(popupIdx)
        Popup popup = popupRepository.findForUpdate(req.getPopupIdx()).orElseThrow(
                () -> new ServiceException(ServiceErrorCode.RESERVE_REGISTER_FAIL_NOT_FOUND_STORE)
        );

        // 팝업 소유 확인
        if (!Objects.equals(popup.getCompanyEmail(), user.getEmail())) {
            throw new ServiceException(ServiceErrorCode.STORE_OWN_FAIL_INVALID_MEMBER);
        }

        // 팝업 상태 확인(종료 상태면 예약 생성 불가)
        if(Objects.equals(popup.getStatus(), PopupStatus.POPUP_END)) {
            throw new ServiceException(ServiceErrorCode.RESERVE_REGISTER_FAIL_STORE_ENDED);
        }

        // 팝업 최대 예약자 수를 넘었는지 확인
        if (req.getReservePeople() > popup.getTotalPeople() || popup.getTotalPeople() <= 0) {
            throw new ServiceException(ServiceErrorCode.RESERVE_REGISTER_FAIL_LIMIT_EXCEEDED);
        }

        if (req.getReserveStartTime().toLocalDate().isBefore(popup.getStartDate()) ||
            req.getReserveEndTime().isAfter(popup.getEndDate().plusDays(1).atStartOfDay())) {
            throw new ServiceException(ServiceErrorCode.RESERVE_REGISTER_FAIL_TIME_CLOSED);
        }

        // 예약 저장
        String workingUUID = UUID.randomUUID().toString();
        String waitingUUID = UUID.randomUUID().toString();
        Reserve reserve = Reserve.create(
                popup,
                workingUUID,
                waitingUUID,
                req.getReservePeople(),
                req.getReserveStartTime(),
                req.getReserveEndTime()
        );
        reserveRepository.save(reserve);

        // Redis 큐 초기화 (예약 종료 시간까지 유효)
        long expirationMinutes = Duration.between(LocalDateTime.now(), req.getReserveEndTime()).toMinutes();
        LocalDateTime reserveEndTime = req.getReserveEndTime();

        afterCommit("예약 큐 생성", () -> {
            reserveRedisService.createQueue(workingUUID, reserveEndTime);
            reserveRedisService.createQueue(waitingUUID, reserveEndTime);
            log.info("Redis 큐 생성 완료 - workingUUID: {}, waitingUUID: {}, 만료시간: {}분", workingUUID, waitingUUID, expirationMinutes);
        });

        // 팝업 저장(총 예약자 수 - 남은 예약자 수)
        popup.setTotalPeople(popup.getTotalPeople() - req.getReservePeople());

        // DTO 반환
        return CreateReserveRes.builder().reserveIdx(reserve.getIdx()).build();

    }

    // 예약 목록 조회(기업용)
    public Page<GetReserveRes> getReserves(CustomUserDetails user, Long popupIdx, int page, int size) throws ServiceException {

        Page<Reserve> reservePage = reserveRepository.findAllByCompanyEmail(popupIdx, user.getEmail(), PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "idx")))
                .orElseThrow(() -> new ServiceException(ServiceErrorCode.RESERVE_SEARCH_ALL_FAIL_NOT_FOUND));

        return Reserve.toDtoPage(reservePage);
    }

    // 예약삭제
    @Transactional
    public void deleteReserve(CustomUserDetails user, Long reserveIdx) throws ServiceException {

        // 예약 조회
        Reserve reserve = reserveRepository.findById(reserveIdx)
                .orElseThrow(() -> new ServiceException(ServiceErrorCode.RESERVE_DELETE_FAIL_NOT_FOUND));

        // 스토어 조회(storeIdx)
        Popup popup = popupRepository.findForUpdate(reserve.getPopup().getIdx())
                .orElseThrow(() -> new ServiceException(ServiceErrorCode.RESERVE_DELETE_FAIL_NOT_FOUND_STORE));

        // 팝업 소유 확인
        if (!Objects.equals(popup.getCompanyEmail(), user.getEmail())) {
            throw new ServiceException(ServiceErrorCode.STORE_OWN_FAIL_INVALID_MEMBER);
        }

        // 예약 종료시간이 지나서 취소할때
        if(reserve.getEndTime().isBefore(LocalDateTime.now())) {
            throw new  ServiceException(ServiceErrorCode.RESERVE_DELETE_FAIL_END_TIME);
        }

        // 인원수 복구
        popup.setTotalPeople(popup.getTotalPeople() + reserve.getTotalPeople());

        // 예약 삭제
        reserveRepository.delete(reserve);
        String workingUUID = reserve.getWorkingUUID();
        String waitingUUID = reserve.getWaitingUUID();
        afterCommit("예약 큐 삭제", () -> reserveRedisService.deleteQueue(workingUUID, waitingUUID));

    }

    // 예약 등록
    public EnrollReserveRes enrollReserve(HttpServletResponse res, CustomUserDetails user, Long reserveIdx) throws ServiceException {

        // 예약 조회(reserveIdx)
        Reserve reserve = reserveRepository.findById(reserveIdx)
                .orElseThrow(() -> new ServiceException(ServiceErrorCode.RESERVE_ENROLL_FAIL_NOT_FOUND));

        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(reserve.getStartTime())) {
            throw new ServiceException(ServiceErrorCode.RESERVE_ENROLL_FAIL_NOT_OPEN);
        }

        if (!now.isBefore(reserve.getEndTime()) || reserve.getTotalPeople() <= 0 || reserve.getPopup().getStatus() != PopupStatus.POPUP_START) {
            throw new ServiceException(ServiceErrorCode.RESERVE_ENROLL_FAIL_CLOSED);
        }

        String email = user.getEmail();
        String response;

        // 락) 기존 멤버 확인, 만료자 정리, 대기자 우선 승격, 신규 등록
        EnrollResult enrollment = reserveRedisService.enrollWithLock(
                reserve.getWorkingUUID(), reserve.getWaitingUUID(),
                email, System.currentTimeMillis(), reserve.getTotalPeople()
        );

        if (enrollment == EnrollResult.WORKING) {
            Long workingOrder = reserveRedisService.getOrder(reserve.getWorkingUUID(), email);
            if (workingOrder == null) throw new ServiceException(ServiceErrorCode.RESERVE_ACCESS_FAIL);

            String token = jwtService.createReserveToken(reserveIdx, email);
            res.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
            Cookie aToken = new Cookie("WTOKEN", token);
            aToken.setHttpOnly(true);
            aToken.setSecure(true);
            aToken.setPath("/");
            aToken.setMaxAge(60 * 10);
            res.addCookie(aToken);

            response = "예약 접속 성공 -> 접속 번호: " + (workingOrder + 1);
            log.info("[Redisson Lock] 예약큐 등록 성공: {}, 순번: {}", email, workingOrder + 1);

        } else if (enrollment == EnrollResult.WAITING) {
            Long waitingOrder = reserveRedisService.getOrder(reserve.getWaitingUUID(), email);
            if (waitingOrder == null) throw new ServiceException(ServiceErrorCode.RESERVE_ACCESS_FAIL);

            response = "예약 대기 등록 -> 대기 순번: " + (waitingOrder + 1);
            log.info("[Redisson Lock] 대기큐 등록: {}, 순번: {}", email, waitingOrder + 1);

        } else {
            throw new ServiceException(ServiceErrorCode.RESERVE_ACCESS_FAIL);
        }

        return EnrollReserveRes.builder().response(response).build();
    }

    // 예약 취소
    public String cancelReserve(HttpServletResponse res, CustomUserDetails user, Long reserveIdx) throws ServiceException {
        Reserve reserve = reserveRepository.findById(reserveIdx)
                .orElseThrow(() -> new ServiceException(ServiceErrorCode.RESERVE_CANCEL_FAIL));

        if (leaveQueue(reserve, user.getEmail())) {
            Cookie deleteCookie = new Cookie("WTOKEN", null);
            deleteCookie.setHttpOnly(true);
            deleteCookie.setSecure(true);
            deleteCookie.setPath("/");
            deleteCookie.setMaxAge(0);
            res.addCookie(deleteCookie);
        }
        return "예약을 취소했습니다.";
    }

    public boolean leaveQueue(Reserve reserve, String email) {
        if (reserveRedisService.getOrder(reserve.getWorkingUUID(), email) != null) {
            reserveRedisService.remove(reserve.getWorkingUUID(), email);
            String promotedUser = reserveRedisService.promoteFirstMember(reserve.getWorkingUUID(), reserve.getWaitingUUID(), reserve.getTotalPeople());
            if (promotedUser != null) {
                String wToken = jwtService.createReserveToken(reserve.getIdx(), promotedUser);
                String workingTotal = reserveRedisService.getSize(reserve.getWorkingUUID());
                String waitingTotal = reserveRedisService.getSize(reserve.getWaitingUUID());
                Long newWorkingOrder = reserveRedisService.getOrder(reserve.getWorkingUUID(), promotedUser);
                Long accessExpiresAt = reserveRedisService.getMemberExpireAt(reserve.getWorkingUUID(), promotedUser);
                String statusMessage = "예약 승격! 예약접속자: " + workingTotal + " 예약대기자: " + waitingTotal + " 현재 순번: " + (newWorkingOrder != null ? newWorkingOrder + 1 : 0);
                messagingTemplate.convertAndSendToUser(
                        promotedUser,
                        "/queue/reserve/status",
                        GetReserveQueueRes.toDataWithToken(reserve.getIdx(), workingTotal, waitingTotal, statusMessage, 1, wToken, accessExpiresAt)
                );
                log.info("승격 알림 전송 완료: {}, 토큰 포함", promotedUser);
            }
            log.info("예약큐 이탈: {}, 예약 ID: {}, 승격: {}", email, reserve.getIdx(), promotedUser);
            return true;
        }

        if (reserveRedisService.getOrder(reserve.getWaitingUUID(), email) != null) {
            reserveRedisService.remove(reserve.getWaitingUUID(), email);
            log.info("대기큐 이탈: {}, 예약 ID: {}", email, reserve.getIdx());
        }
        return false;
    }

    // 소켓 방식
    public void status(Principal principal, GetReserveQueueReq req) throws ServiceException {

        int access; // 결제 페이지로 접근 가능한지 여부
        String statusMessage; // 클라이언트로 전송할 상태 메시지
        String username = principal.getName();

        Reserve reserve = reserveRepository.findById(req.getReserveIdx())
                .orElseThrow(() -> new ServiceException(ServiceErrorCode.RESERVE_SEARCH_STATUS_FAIL_NOT_FOUND));

        if (!reserve.getEndTime().isAfter(LocalDateTime.now()) || reserve.getPopup().getStatus() == PopupStatus.POPUP_END) {
            messagingTemplate.convertAndSendToUser(
                    principal.getName(),
                    "/queue/reserve/status",
                    GetReserveQueueRes.toData(reserve.getIdx(), "0", "0", "예약이 종료되었습니다.", 2)
            );
            return;
        }

        // 만료된 사용자 제거
        reserveRedisService.removeExpireMembers(reserve.getWorkingUUID());

        String workingTotal = reserveRedisService.getSize(reserve.getWorkingUUID());
        String waitingTotal = reserveRedisService.getSize(reserve.getWaitingUUID());
        Long workingOrder = reserveRedisService.getOrder(reserve.getWorkingUUID(), username);
        Long waitingOrder = workingOrder == null ? reserveRedisService.getOrder(reserve.getWaitingUUID(), username): null;

        if (reserve.getTotalPeople() <= 0) {
            access = 3;
            statusMessage = "예약이 마감되었습니다.";
        } else if (workingOrder != null) {
            access = 1;
            statusMessage = "예약접속자: " + workingTotal + " 예약대기자: " + waitingTotal + " 현재 순번: " + (workingOrder + 1);
        } else if (waitingOrder != null) {
            access = 0;
            statusMessage = "예약접속자: " + workingTotal + " 예약대기자: " + waitingTotal + " 현재 순번: " + (waitingOrder + 1);
        } else {
            access = 4;
            statusMessage = "예약 입장 유효시간이 만료되었습니다.";
        }

        // 특정 사용자에게만 상태 정보 전송
        String wToken = access == 1 ? jwtService.createReserveToken(reserve.getIdx(), username) : null;
        Long accessExpiresAt = access == 1  ? reserveRedisService.getMemberExpireAt(reserve.getWorkingUUID(), principal.getName()) : null;

        messagingTemplate.convertAndSendToUser(
                username,
                "/queue/reserve/status",
                access == 1
                        ? GetReserveQueueRes.toDataWithToken(reserve.getIdx(), workingTotal, waitingTotal, statusMessage, access, wToken, accessExpiresAt)
                        : GetReserveQueueRes.toData(reserve.getIdx(), workingTotal, waitingTotal, statusMessage, access)
        );

        log.info("Sending message to user: {}, {}", principal.getName(), access);
    }

    // Redis 큐 관리 (만료 큐 정리 + 복구)
    public void managementReserveQueue() {
        List<Reserve> reserveList = reserveRepository.findQueuesForMaintenance(LocalDateTime.now());
        if (reserveList.isEmpty()) return;

        LocalDateTime now = LocalDateTime.now();

        for (Reserve reserve : reserveList) {
            // 종료 시간이 지난 예약: Redis 큐 삭제 (정리)
            if (!reserve.getEndTime().isAfter(now) || reserve.getPopup().getStatus() == PopupStatus.POPUP_END) {
                reserveRedisService.deleteQueue(reserve.getWorkingUUID(), reserve.getWaitingUUID());
                log.debug("만료 큐 삭제 - 예약 ID: {}, 스토어: {}", reserve.getIdx(), reserve.getPopup().getName());
                continue;
            }

            // 만료된 사용자 정리
            reserveRedisService.removeExpireMembers(reserve.getWorkingUUID());

            // Working Queue 확인 및 생성 (큐가 없으면 생성)
            if (!reserveRedisService.existQueue(reserve.getWorkingUUID())) {
                reserveRedisService.createQueue(reserve.getWorkingUUID(), reserve.getEndTime());
                log.debug("Working 큐 생성: {} (예약 ID: {})", reserve.getWorkingUUID(), reserve.getIdx());
            }

            // Waiting Queue 확인 및 생성 (큐가 없으면 생성)
            if (!reserveRedisService.existQueue(reserve.getWaitingUUID())) {
                reserveRedisService.createQueue(reserve.getWaitingUUID(), reserve.getEndTime());
                log.debug("Waiting 큐 생성: {} (예약 ID: {})", reserve.getWaitingUUID(), reserve.getIdx());
            }
        }
    }

    public void promoteWaitingMembers() {
        List<Reserve> reserves = reserveRepository.findActiveQueuesForPromotion(LocalDateTime.now());
        for (Reserve reserve : reserves) {
            String promoted;
            while ((promoted = reserveRedisService.promoteFirstMember(reserve.getWorkingUUID(), reserve.getWaitingUUID(), reserve.getTotalPeople())) != null) {
                messagingTemplate.convertAndSendToUser(
                        promoted, "/queue/reserve/status",
                        GetReserveQueueRes.toDataWithToken(reserve.getIdx(),
                                reserveRedisService.getSize(reserve.getWorkingUUID()),
                                reserveRedisService.getSize(reserve.getWaitingUUID()), "예약 승격", 1,
                                jwtService.createReserveToken(reserve.getIdx(), promoted),
                                reserveRedisService.getMemberExpireAt(reserve.getWorkingUUID(), promoted))
                );
            }
        }
    }

    private void afterCommit(String operation, Runnable action) {
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                try {
                    action.run();
                } catch (RuntimeException exception) {
                    log.error("{} 실패: DB 변경은 이미 커밋되었습니다.", operation, exception);
                }
            }
        });
    }


}
