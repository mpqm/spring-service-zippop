package com.fiiiiive.zippop.global.socket;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class ReserveSocketSessionsTest {
    private final ReserveSocketSessions sessions = new ReserveSocketSessions();

    @Test void aDifferentReservationIsNeverRemovedByAnotherTabsDisconnect() {
        sessions.bind("tab-a", "user@example.test", 1L);
        sessions.bind("tab-b", "user@example.test", 2L);
        assertThat(sessions.disconnectLast("tab-b").reserveId()).isEqualTo(2L);
        assertThat(sessions.disconnectLast("tab-a").reserveId()).isEqualTo(1L);
    }

    @Test void sameReservationIsRemovedOnlyAfterItsLastConnectionCloses() {
        sessions.bind("tab-a", "user@example.test", 1L);
        sessions.bind("tab-b", "user@example.test", 1L);
        assertThat(sessions.disconnectLast("tab-a")).isNull();
        assertThat(sessions.disconnectLast("tab-a")).isNull();
        assertThat(sessions.disconnectLast("tab-b").reserveId()).isEqualTo(1L);
    }

    @Test void sessionCannotSilentlySwitchReservations() {
        sessions.bind("tab-a", "user@example.test", 1L);
        assertThatThrownBy(() -> sessions.bind("tab-a", "user@example.test", 2L))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
