package com.fiiiiive.zippop.payout.scheduler;

import com.fiiiiive.zippop.payout.service.PayoutService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@RequiredArgsConstructor
public class PayoutScheduler {

    private final PayoutService payoutService;

    // 팝업 판매 수익금 정산 스케줄러 (매일 00:00:00에 실행)
    @Scheduled(cron = "0 0 0 * * ?")
    public void createDailyPayout() {
        log.info("[PayoutScheduler] 정산 스케줄러 실행");
        payoutService.createDailyPayout();
    }

}
