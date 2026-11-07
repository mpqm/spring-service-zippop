package com.fiiiiive.zippop.orders.scheduler;

import com.fiiiiive.zippop.orders.model.entity.RefundOutboxStatus;
import com.fiiiiive.zippop.orders.repository.RefundOutboxRepository;
import com.fiiiiive.zippop.orders.service.RefundOutboxService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class RefundOutboxScheduler {
    private final RefundOutboxRepository repository;
    private final RefundOutboxService service;

    @Scheduled(fixedDelay = 60_000)
    public void retryDueRefunds() {
        repository.findTop100ByStatusInAndNextAttemptAtLessThanEqualOrderByNextAttemptAt(
                List.of(RefundOutboxStatus.PENDING, RefundOutboxStatus.RETRY), LocalDateTime.now())
                .forEach(outbox -> service.process(outbox.getIdx()));
    }
}
