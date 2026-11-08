package com.fiiiiive.zippop.reserve.infrastructure.stomp;

import com.fiiiiive.zippop.reserve.model.entity.Reserve;
import com.fiiiiive.zippop.reserve.repository.ReserveRepository;
import com.fiiiiive.zippop.reserve.service.ReserveService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ReserveStompSession {

    public record Membership(String email, Long reserveId) {}

    private final Map<String, Membership> sessions = new HashMap<>();
    private final ReserveRepository reserveRepository;
    private final ReserveService reserveService;

    public synchronized void bind(String sessionId, String email, Long reserveId) {
        if (sessionId == null || reserveId == null || reserveId <= 0) {
            throw new IllegalArgumentException("Invalid reservation session");
        }
        Membership member = new Membership(email, reserveId);
        Membership existing = sessions.putIfAbsent(sessionId, member);
        if (existing != null && !existing.equals(member)) {
            throw new IllegalArgumentException("Reservation cannot change within a session");
        }
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        Principal user = accessor.getUser();
        if (user == null) return;

        String email = user.getName();
        Membership member;
        synchronized (this) {
            member = sessions.remove(accessor.getSessionId());
            if (member == null || sessions.containsValue(member)) return;
        }
        if (!member.email().equals(email)) return;

        Reserve reserve = reserveRepository.findById(member.reserveId()).orElse(null);
        if (reserve == null || reserve.getEndTime().isBefore(LocalDateTime.now())) return;
        reserveService.leaveQueue(reserve, email);
    }
}
