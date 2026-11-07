package com.fiiiiive.zippop.orders.event;

import com.fiiiiive.zippop.orders.service.RefundOutboxService;
import com.siot.IamportRestClient.response.Payment;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RefundEventListenerTest {

    @Test
    void refundHandlersAreBoundToRollbackAndCommitSeparately() throws Exception {
        RefundOutboxService refundService = mock(RefundOutboxService.class);
        RefundEventListener listener = new RefundEventListener(refundService);
        Payment payment = mock(Payment.class);
        when(payment.getImpUid()).thenReturn("imp-1");
        when(payment.getAmount()).thenReturn(BigDecimal.valueOf(1000));

        listener.handleRefundAfterRollback(new RefundEvent(payment, true));
        listener.handleRefundAfterCommit(new RefundEvent(payment, false));

        verify(refundService, times(2)).enqueueCompensation(payment);
        TransactionalEventListener rollback = RefundEventListener.class
                .getMethod("handleRefundAfterRollback", RefundEvent.class)
                .getAnnotation(TransactionalEventListener.class);
        TransactionalEventListener commit = RefundEventListener.class
                .getMethod("handleRefundAfterCommit", RefundEvent.class)
                .getAnnotation(TransactionalEventListener.class);
        assertThat(rollback.phase()).isEqualTo(TransactionPhase.AFTER_ROLLBACK);
        assertThat(commit.phase()).isEqualTo(TransactionPhase.AFTER_COMMIT);
    }
}
