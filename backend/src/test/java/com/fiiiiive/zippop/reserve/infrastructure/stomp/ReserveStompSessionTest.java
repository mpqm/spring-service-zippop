package com.fiiiiive.zippop.reserve.infrastructure.stomp;

import com.fiiiiive.zippop.reserve.model.entity.Reserve;
import com.fiiiiive.zippop.reserve.repository.ReserveRepository;
import com.fiiiiive.zippop.reserve.service.ReserveService;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class ReserveStompSessionTest {
    private final ReserveRepository reserves = mock(ReserveRepository.class);
    private final ReserveService reserveService = mock(ReserveService.class);
    private final ReserveStompSession service = new ReserveStompSession(reserves, reserveService);

    @Test
    void aDifferentReservationIsNeverRemovedByAnotherTabsDisconnect() {
        Reserve first = activeReserve(1L);
        Reserve second = activeReserve(2L);
        when(reserves.findById(1L)).thenReturn(Optional.of(first));
        when(reserves.findById(2L)).thenReturn(Optional.of(second));
        service.bind("tab-a", "user@example.test", 1L);
        service.bind("tab-b", "user@example.test", 2L);

        service.handleWebSocketDisconnectListener(disconnectEvent("tab-b"));
        verify(reserveService).leaveQueue(second, "user@example.test");
        verify(reserveService, never()).leaveQueue(first, "user@example.test");

        service.handleWebSocketDisconnectListener(disconnectEvent("tab-a"));
        verify(reserveService).leaveQueue(first, "user@example.test");
    }

    @Test
    void sameReservationIsRemovedOnlyAfterItsLastConnectionCloses() {
        Reserve reserve = activeReserve(1L);
        when(reserves.findById(1L)).thenReturn(Optional.of(reserve));
        service.bind("tab-a", "user@example.test", 1L);
        service.bind("tab-b", "user@example.test", 1L);

        service.handleWebSocketDisconnectListener(disconnectEvent("tab-a"));
        service.handleWebSocketDisconnectListener(disconnectEvent("tab-a"));
        verifyNoInteractions(reserveService);

        service.handleWebSocketDisconnectListener(disconnectEvent("tab-b"));
        verify(reserveService).leaveQueue(reserve, "user@example.test");
    }

    @Test
    void sessionCannotSilentlySwitchReservations() {
        service.bind("tab-a", "user@example.test", 1L);
        assertThatThrownBy(() -> service.bind("tab-a", "user@example.test", 2L))
                .isInstanceOf(IllegalArgumentException.class);
    }

    private Reserve activeReserve(Long reserveId) {
        return Reserve.builder().idx(reserveId).workingUUID("working").waitingUUID("waiting")
                .totalPeople(1).endTime(LocalDateTime.now().plusMinutes(5)).build();
    }

    private SessionDisconnectEvent disconnectEvent(String sessionId) {
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.DISCONNECT);
        accessor.setSessionId(sessionId);
        accessor.setUser(() -> "user@example.test");
        SessionDisconnectEvent event = mock(SessionDisconnectEvent.class);
        when(event.getMessage()).thenReturn(MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders()));
        return event;
    }
}
