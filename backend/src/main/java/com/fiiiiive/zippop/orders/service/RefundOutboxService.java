package com.fiiiiive.zippop.orders.service;

import com.fiiiiive.zippop.orders.event.RefundOutboxReadyEvent;
import com.fiiiiive.zippop.orders.model.entity.Orders;
import com.fiiiiive.zippop.orders.model.entity.RefundOutbox;
import com.fiiiiive.zippop.orders.repository.RefundOutboxRepository;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.request.CancelData;
import com.siot.IamportRestClient.response.Payment;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RefundOutboxService {
    private final RefundOutboxRepository repository;
    private final IamportClient iamportClient;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public RefundOutbox enqueueOrderRefund(Orders orders, Payment payment) {
        RefundOutbox outbox = repository.findByImpUid(payment.getImpUid())
                .orElseGet(() -> repository.save(RefundOutbox.pending(payment.getImpUid(), payment.getAmount(), orders)));
        orders.markRefundPending();
        eventPublisher.publishEvent(new RefundOutboxReadyEvent(outbox.getIdx()));
        return outbox;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void enqueueCompensation(Payment payment) {
        RefundOutbox outbox = repository.findByImpUid(payment.getImpUid())
                .orElseGet(() -> repository.save(RefundOutbox.pending(payment.getImpUid(), payment.getAmount(), null)));
        eventPublisher.publishEvent(new RefundOutboxReadyEvent(outbox.getIdx()));
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void process(Long outboxId) {
        RefundOutbox outbox = repository.findForUpdate(outboxId).orElse(null);
        if (outbox == null || outbox.getStatus() == com.fiiiiive.zippop.orders.model.entity.RefundOutboxStatus.COMPLETED) return;
        try {
            var response = iamportClient.cancelPaymentByImpUid(new CancelData(outbox.getImpUid(), true, outbox.getAmount()));
            if (response == null || response.getResponse() == null) throw new IllegalStateException("PG refund response was empty");
            outbox.completed();
        } catch (Exception exception) {
            outbox.retry(exception.getMessage());
        }
    }
}
