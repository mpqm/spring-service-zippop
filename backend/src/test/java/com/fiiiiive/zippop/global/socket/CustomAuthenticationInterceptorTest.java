package com.fiiiiive.zippop.global.socket;

import org.junit.jupiter.api.Test;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.support.ExecutorSubscribableChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CustomAuthenticationInterceptorTest {

    private final CustomAuthenticationInterceptor interceptor = new CustomAuthenticationInterceptor();
    private final ExecutorSubscribableChannel channel = new ExecutorSubscribableChannel();

    @Test
    void rejectsConnectWithoutHandshakeAuthenticationUsingSharedErrorCode() {
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.CONNECT);
        accessor.setSessionAttributes(new HashMap<>());
        var message = MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders());

        assertThatThrownBy(() -> interceptor.preSend(message, channel))
                .isInstanceOf(MessageDeliveryException.class)
                .hasMessageContaining("AUTHENTICATION_REQUIRED");
    }

    @Test
    void attachesHandshakeAuthenticationToStompPrincipal() {
        var authentication = UsernamePasswordAuthenticationToken.authenticated("user", null, java.util.List.of());
        var attributes = new HashMap<String, Object>();
        attributes.put(CustomHandshakeInterceptor.SESSION_AUTHENTICATION, authentication);
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.CONNECT);
        accessor.setSessionAttributes(attributes);
        var message = MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders());

        var result = interceptor.preSend(message, channel);
        StompHeaderAccessor resultAccessor = StompHeaderAccessor.wrap(result);

        assertThat(resultAccessor.getUser()).isEqualTo(authentication);
    }
}
