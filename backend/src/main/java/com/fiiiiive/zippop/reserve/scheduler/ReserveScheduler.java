package com.fiiiiive.zippop.reserve.scheduler;

import com.fiiiiive.zippop.reserve.service.ReserveService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReserveScheduler {

    private final ReserveService reserveService;

    // 만료 입장자·종료 큐 정리 및 누락 큐 복구
    @Scheduled(cron = "*/10 * * * * *", zone = "Asia/Seoul")
    public void manageReserveQueue() {
        try {
            log.info("[예약 큐 관리 스케줄러]");
            reserveService.managementReserveQueue();
        } catch (Exception e) {
            log.error("예약 큐 관리 중 오류 발생: {}", e.getMessage());
        }
    }

    @Scheduled(cron = "0 * * * * *", zone = "Asia/Seoul")
    public void promoteWaitingMembers() {
        try {
            reserveService.promoteWaitingMembers();
        } catch (Exception exception) {
            log.error("예약 대기자 승격 중 오류 발생", exception);
        }
    }
}
