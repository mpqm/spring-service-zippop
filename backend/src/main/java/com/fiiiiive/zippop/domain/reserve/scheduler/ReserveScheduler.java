package com.fiiiiive.zippop.domain.reserve.scheduler;

import com.fiiiiive.zippop.global.service.RedisService;
import com.fiiiiive.zippop.domain.reserve.entity.Reserve;
import com.fiiiiive.zippop.domain.reserve.repository.ReserveRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReserveScheduler {

    private final ReserveRepository reserveRepository;
    private final RedisService redisService;

    // 매 10분마다 실행: Redis 큐 관리 (만료 큐 정리 + 복구)
    @Scheduled(cron = "0 */10 * * * *")
    public void manageReserveQueue() {
        try {
            log.info("[예약 큐 관리 스케줄러]");
            // 오늘 날짜의 모든 예약 조회

            List<Reserve> reserveList = reserveRepository.findAllByStartDate(LocalDate.now());
            if (reserveList.isEmpty()) {
                return;
            }

            LocalDateTime now = LocalDateTime.now();

            for (Reserve reserve : reserveList) {
                // 종료 시간이 지난 예약: Redis 큐 삭제 (정리)
                if (reserve.getEndTime().isBefore(now)) {
                    redisService.deleteQueue(reserve.getWorkingUUID(), reserve.getWaitingUUID());
                    log.debug("만료 큐 삭제 - 예약 ID: {}, 스토어: {}", reserve.getIdx(), reserve.getStore().getName());
                    continue;
                }

                // 진행 중인 예약: 큐가 없으면 생성 (복구)
                // 현재 시간부터 종료 시간까지의 남은 시간 계산 (분 단위)
                long remainingMinutes = Duration.between(now, reserve.getEndTime()).toMinutes();

                if (remainingMinutes <= 0) {
                    continue;
                }

                // Working Queue 확인 및 생성 (큐가 없으면 생성)
                if (redisService.existQueue(reserve.getWorkingUUID())) {
                    redisService.createQueue(reserve.getWorkingUUID(), remainingMinutes);
                    log.debug("Working 큐 생성: {} (예약 ID: {})", reserve.getWorkingUUID(), reserve.getIdx());
                }

                // Waiting Queue 확인 및 생성 (큐가 없으면 생성)
                if (redisService.existQueue(reserve.getWaitingUUID())) {
                    redisService.createQueue(reserve.getWaitingUUID(), remainingMinutes);
                    log.debug("Waiting 큐 생성: {} (예약 ID: {})", reserve.getWaitingUUID(), reserve.getIdx());
                }
            }
        } catch (Exception e) {
            log.error("예약 큐 관리 중 오류 발생: {}", e.getMessage());
        }
    }
}
