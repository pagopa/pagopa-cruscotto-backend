package com.nexigroup.pagopa.cruscotto.domain;

import static org.junit.jupiter.api.Assertions.*;

import java.time.ZonedDateTime;
import org.junit.jupiter.api.Test;

class AuthUserHistoryTest {

    @Test
    void testGettersAndSetters() {
        AuthUserHistory history = new AuthUserHistory();
        AuthUser authUser = new AuthUser();
        ZonedDateTime now = ZonedDateTime.now();

        history.setId(1L);
        history.setAuthUser(authUser);
        history.setDataModifica(now);
        history.setPassword("a".repeat(60));

        assertEquals(1L, history.getId());
        assertEquals(authUser, history.getAuthUser());
        assertEquals(now, history.getDataModifica());
        assertEquals("a".repeat(60), history.getPassword());
        assertEquals(1L, history.getSerialversionuid());
    }

    @Test
    void testToStringContainsExpectedFields() {
        AuthUserHistory history = new AuthUserHistory();
        history.setId(1L);
        history.setAuthUser(new AuthUser());
        history.setDataModifica(ZonedDateTime.parse("2024-10-07T10:00:00Z"));

        String result = history.toString();

        assertTrue(result.contains("UserHistory"));
        assertTrue(result.contains("id=1"));
        assertTrue(result.contains("dataModifica=2024-10-07T10:00Z"));
    }

    @Test
    void testDefaultValues() {
        AuthUserHistory history = new AuthUserHistory();
        assertNull(history.getId());
        assertNull(history.getAuthUser());
        assertNull(history.getDataModifica());
        assertNull(history.getPassword());
    }
}
