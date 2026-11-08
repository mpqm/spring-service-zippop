package com.fiiiiive.zippop.reserve.infrastructure.stomp;

import org.junit.jupiter.api.Test;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ExecutorSubscribableChannel;
import org.springframework.messaging.support.MessageBuilder;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

class ReserveStompInterceptorTest {

    private final ReserveStompSession reserveStompSession = mock(ReserveStompSession.class);
    private final ReserveStompInterceptor interceptor = new ReserveStompInterceptor(reserveStompSession);
    private final ExecutorSubscribableChannel channel = new ExecutorSubscribableChannel();

    @Test
    void bindsAuthenticatedReserveStatusSendToItsSession() {
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.SEND);
        accessor.setSessionId("session-1");
        accessor.setDestination("/pub/reserve/status");
        accessor.setUser(() -> "user@example.test");
        byte[] payload = "{\"reserveIdx\":12}".getBytes(StandardCharsets.UTF_8);

        interceptor.preSend(MessageBuilder.createMessage(payload, accessor.getMessageHeaders()), channel);

        verify(reserveStompSession).bind("session-1", "user@example.test", 12L);
    }

    @Test
    void rejectsOtherSendDestinations() {
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.SEND);
        accessor.setDestination("/pub/other");
        accessor.setUser(() -> "user@example.test");

        assertThatThrownBy(() -> interceptor.preSend(
                MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders()), channel))
                .isInstanceOf(MessageDeliveryException.class)
                .hasMessageContaining("ACCESS_DENIED");
        verifyNoInteractions(reserveStompSession);
    }

    @Test
    void rejectsOtherSubscriptions() {
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.SUBSCRIBE);
        accessor.setDestination("/user/queue/other");

        assertThatThrownBy(() -> interceptor.preSend(
                MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders()), channel))
                .isInstanceOf(MessageDeliveryException.class)
                .hasMessageContaining("ACCESS_DENIED");
    }
}
