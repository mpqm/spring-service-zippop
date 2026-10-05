package com.fiiiiive.zippop.orders.event;

import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.response.Payment;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RefundEventListenerTest {

    @Test
    void refundHandlersAreBoundToRollbackAndCommitSeparately() throws Exception {
        IamportClient iamportClient = mock(IamportClient.class);
        RefundEventListener listener = new RefundEventListener(iamportClient);
        Payment payment = mock(Payment.class);
        when(payment.getImpUid()).thenReturn("imp-1");
        when(payment.getAmount()).thenReturn(BigDecimal.valueOf(1000));

        listener.handleRefundAfterRollback(new RefundEvent(payment, true));
        listener.handleRefundAfterCommit(new RefundEvent(payment, false));

        verify(iamportClient, times(2)).cancelPaymentByImpUid(any());
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
