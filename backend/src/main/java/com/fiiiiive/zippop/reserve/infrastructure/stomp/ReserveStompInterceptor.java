package com.fiiiiive.zippop.reserve.infrastructure.stomp;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

import static com.google.gson.JsonParser.parseString;

@Component
@RequiredArgsConstructor
public class ReserveStompInterceptor implements ChannelInterceptor {

    private final ReserveStompSession reserveStompSession;

    @Override
    public Message<?> preSend(@NotNull Message<?> message, @NotNull MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        if (accessor.getCommand() == StompCommand.SEND) {
            if (!"/pub/reserve/status".equals(accessor.getDestination()) || accessor.getUser() == null) {
                throw new MessageDeliveryException(message, "ACCESS_DENIED");
            }

            try {
                String json = new String((byte[]) message.getPayload(), StandardCharsets.UTF_8);
                Long reserveId = parseString(json).getAsJsonObject().get("reserveIdx").getAsLong();
                reserveStompSession.bind(accessor.getSessionId(), accessor.getUser().getName(), reserveId);
            } catch (RuntimeException ex) {
                throw new MessageDeliveryException(message, "INVALID_RESERVATION_SESSION");
            }

        } else if (accessor.getCommand() == StompCommand.SUBSCRIBE && !"/user/queue/reserve/status".equals(accessor.getDestination())) {
            throw new MessageDeliveryException(message, "ACCESS_DENIED");
        }
        return message;
    }
}
