package com.fiiiiive.zippop.global.socket;

import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.Map;

@Component
public class ReserveSocketSessions {
    public record Membership(String email, Long reserveId) {}
    private final Map<String, Membership> sessions = new HashMap<>();

    public synchronized void bind(String sessionId, String email, Long reserveId) {
        if (sessionId == null || reserveId == null || reserveId <= 0) throw new IllegalArgumentException("Invalid reservation session");
        Membership member = new Membership(email, reserveId);
        Membership existing = sessions.putIfAbsent(sessionId, member);
        if (existing != null && !existing.equals(member)) throw new IllegalArgumentException("Reservation cannot change within a session");
    }

    public synchronized Membership disconnectLast(String sessionId) {
        Membership member = sessions.remove(sessionId);
        return member != null && !sessions.containsValue(member) ? member : null;
    }
}
