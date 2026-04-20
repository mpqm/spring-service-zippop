package com.fiiiiive.zippop.global.socket;

import com.fiiiiive.zippop.global.crypto.JwtService;
import com.fiiiiive.zippop.global.redis.RedisQueueService;
import com.fiiiiive.zippop.reserve.model.dto.GetReserveQueueRes;
import com.fiiiiive.zippop.reserve.model.entity.Reserve;
import com.fiiiiive.zippop.reserve.repository.ReserveRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;
import java.util.List;


@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketDisconnectHandler {

    private final RedisQueueService redisQueueService;
    private final JwtService jwtService;
    private final ReserveRepository reserveRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        Principal user = headerAccessor.getUser();

        if (user != null) {
            String userEmail = user.getName();
            log.info("WebSocket 연결 끊김 감지 - 사용자: {}", userEmail);

            // 오늘 날짜의 모든 예약 조회 (진행 중인 예약만)
            List<Reserve> activeReserves = reserveRepository.findAllByStartDate(java.time.LocalDate.now());

            for (Reserve reserve : activeReserves) {
                // 예약 종료 시간이 지났으면 스킵
                if (reserve.getEndTime().isBefore(java.time.LocalDateTime.now())) continue;

                // 예약큐에서 사용자 확인 및 제거
                Long workingOrder = redisQueueService.getOrder(reserve.getWorkingUUID(), userEmail);
                if (workingOrder != null) {

                    log.info("예약큐 연결 끊김 처리 - 예약 ID: {}, 사용자: {}", reserve.getIdx(), userEmail);

                    // 토큰 재생성 및 블랙리스트 추가 (같은 방식으로 생성되므로 동일한 토큰)
                    String revokedToken = jwtService.createReserveToken(reserve.getIdx(), userEmail);
                    redisQueueService.blacklistReserveToken(revokedToken);
                    log.info("연결 끊김으로 인한 토큰 블랙리스트 추가: {}", userEmail);

                    // 예약큐에서 제거
                    redisQueueService.remove(reserve.getWorkingUUID(), userEmail);

                    // 첫번째 대기자 → 예약자 승격
                    String firstWaitingUser = redisQueueService.firstWaitingUserToWorking(reserve.getWorkingUUID(),reserve.getWaitingUUID(),reserve.getTotalPeople());

                    // 승격된 사용자에게 알림 및 토큰 전송
                    if (firstWaitingUser != null) {
                        String workingTotal = redisQueueService.getSize(reserve.getWorkingUUID());
                        String waitingTotal = redisQueueService.getSize(reserve.getWaitingUUID());
                        Long newWorkingOrder = redisQueueService.getOrder(reserve.getWorkingUUID(), firstWaitingUser);
                        String wToken = jwtService.createReserveToken(reserve.getIdx(), firstWaitingUser); // 토큰 발급
                        String statusMessage = String.format("🎉 예약 승격! 다른 사용자가 나가서 자리가 났습니다. 예약접속자: %s, 예약대기자: %s, 현재 순번: %d", workingTotal, waitingTotal, (newWorkingOrder != null ? newWorkingOrder + 1 : 0));

                        // WebSocket으로 알림 전송 (토큰은 다음 상태 요청 시 발급)
                        messagingTemplate.convertAndSendToUser(
                                firstWaitingUser,
                                "/reserve/status",
                                GetReserveQueueRes.toDataWithToken(workingTotal, waitingTotal, statusMessage, 1, wToken)
                        );
                        log.info("승격 알림 전송 완료 - 사용자: {}", firstWaitingUser);
                    }
                    log.info("예약큐 연결 끊김 처리 완료 - 제거: {}, 승격: {}", userEmail, firstWaitingUser);
                    return; // 하나의 예약에만 속할 수 있으므로 종료
                }

                // 대기큐에서 사용자 확인 및 제거
                Long waitingOrder = redisQueueService.getOrder(reserve.getWaitingUUID(), userEmail);
                if (waitingOrder != null) {
                    log.info("대기큐 연결 끊김 처리 - 예약 ID: {}, 사용자: {}", reserve.getIdx(), userEmail);
                    redisQueueService.remove(reserve.getWaitingUUID(), userEmail);
                    log.info("대기큐 연결 끊김 처리 완료 - 제거: {}", userEmail);
                    return; // 하나의 예약에만 속할 수 있으므로 종료
                }
            }
        }
    }
}

