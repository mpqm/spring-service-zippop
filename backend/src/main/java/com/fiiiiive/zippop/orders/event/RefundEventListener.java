package com.fiiiiive.zippop.orders.event;

import com.fiiiiive.zippop.orders.service.RefundOutboxService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class RefundEventListener {

    private final RefundOutboxService refundOutboxService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK, condition = "#event.rollbackOnly")
    public void handleRefundAfterRollback(RefundEvent event) {
        refundOutboxService.enqueueCompensation(event.getPayment());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, condition = "!#event.rollbackOnly")
    public void handleRefundAfterCommit(RefundEvent event) {
        refundOutboxService.enqueueCompensation(event.getPayment());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void processOutbox(RefundOutboxReadyEvent event) {
        refundOutboxService.process(event.outboxId());
    }

}
