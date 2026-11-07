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
@lombok.RequiredArgsConstructor
public class CustomAuthenticationInterceptor implements ChannelInterceptor {

    private final ReserveSocketSessions sessions;

    @Override
    public Message<?> preSend(@NotNull Message<?> message, @NotNull MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        if (accessor.getCommand() != StompCommand.CONNECT) {
            if (accessor.getCommand() == StompCommand.SEND) {
                if (!"/pub/reserve/status".equals(accessor.getDestination()) || accessor.getUser() == null) {
                    throw new MessageDeliveryException(message, "ACCESS_DENIED");
                }
                try {
                    String json = new String((byte[]) message.getPayload(), java.nio.charset.StandardCharsets.UTF_8);
                    Long reserveId = com.google.gson.JsonParser.parseString(json).getAsJsonObject().get("reserveIdx").getAsLong();
                    sessions.bind(accessor.getSessionId(), accessor.getUser().getName(), reserveId);
                } catch (RuntimeException ex) {
                    throw new MessageDeliveryException(message, "INVALID_RESERVATION_SESSION");
                }
            } else if (accessor.getCommand() == StompCommand.SUBSCRIBE
                    && !"/user/queue/reserve/status".equals(accessor.getDestination())) {
                throw new MessageDeliveryException(message, "ACCESS_DENIED");
            }
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
