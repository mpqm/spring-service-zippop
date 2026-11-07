package com.fiiiiive.zippop.orders.service;

import com.fiiiiive.zippop.orders.model.entity.RefundOutbox;
import com.fiiiiive.zippop.orders.model.entity.RefundOutboxStatus;
import com.fiiiiive.zippop.orders.repository.RefundOutboxRepository;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RefundOutboxServiceTest {
    @Test
    void providerFailureIsPersistedForRetryAndLaterCompletionIsIdempotent() throws Exception {
        RefundOutboxRepository repository = mock(RefundOutboxRepository.class);
        IamportClient client = mock(IamportClient.class);
        ApplicationEventPublisher publisher = mock(ApplicationEventPublisher.class);
        RefundOutboxService service = new RefundOutboxService(repository, client, publisher);
        RefundOutbox outbox = RefundOutbox.builder().idx(1L).impUid("imp-1")
                .amount(BigDecimal.valueOf(1000)).status(RefundOutboxStatus.PENDING)
                .attempts(0).nextAttemptAt(java.time.LocalDateTime.now()).build();
        when(repository.findForUpdate(1L)).thenReturn(Optional.of(outbox));
        when(client.cancelPaymentByImpUid(any())).thenReturn(null);

        service.process(1L);

        assertThat(outbox.getStatus()).isEqualTo(RefundOutboxStatus.RETRY);
        assertThat(outbox.getAttempts()).isEqualTo(1);
        @SuppressWarnings("unchecked") IamportResponse<Payment> success = mock(IamportResponse.class);
        when(success.getResponse()).thenReturn(mock(Payment.class));
        when(client.cancelPaymentByImpUid(any())).thenReturn(success);

        service.process(1L);
        service.process(1L);

        assertThat(outbox.getStatus()).isEqualTo(RefundOutboxStatus.COMPLETED);
        verify(client, times(2)).cancelPaymentByImpUid(any());
    }
}
