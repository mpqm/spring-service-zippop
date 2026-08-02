package com.fiiiiive.zippop.global.socket;

import com.fiiiiive.zippop.global.base.ServerErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class CustomAuthenticationInterceptor implements ChannelInterceptor {

    @Override
    public Message<?> preSend(@NotNull Message<?> message, @NotNull MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        if (accessor.getCommand() != StompCommand.CONNECT) {
            return message;
        }

        Map<String, Object> attributes = accessor.getSessionAttributes();
        Object value = attributes == null
                ? null
                : attributes.get(CustomHandshakeInterceptor.SESSION_AUTHENTICATION);
        if (!(value instanceof Authentication authentication) || !authentication.isAuthenticated()) {
            log.debug("Rejected unauthenticated STOMP connection");
            throw new MessageDeliveryException(message, ServerErrorCode.AUTHENTICATION_REQUIRED.name());
        }

        accessor.setUser(authentication);
        return MessageBuilder.createMessage(message.getPayload(), accessor.getMessageHeaders());
    }
}
