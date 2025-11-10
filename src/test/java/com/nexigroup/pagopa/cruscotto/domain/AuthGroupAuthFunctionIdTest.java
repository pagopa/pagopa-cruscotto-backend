package com.nexigroup.pagopa.cruscotto.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AuthGroupAuthFunctionIdTest {

    @Test
    void testGettersAndSetters() {
        AuthGroupAuthFunctionId authId = new AuthGroupAuthFunctionId();

        // Test setting and getting 'gruppo'
        Long gruppoValue = 100L;
        authId.setGruppo(gruppoValue);
        assertEquals(gruppoValue, authId.getGruppo(), "Gruppo getter/setter failed");

        // Test setting and getting 'funzione'
        Long funzioneValue = 200L;
        authId.setFunzione(funzioneValue);
        assertEquals(funzioneValue, authId.getFunzione(), "Funzione getter/setter failed");
    }

    @Test
    void testEqualsAndHashCode() {
        AuthGroupAuthFunctionId authId1 = new AuthGroupAuthFunctionId();
        AuthGroupAuthFunctionId authId2 = new AuthGroupAuthFunctionId();

        authId1.setGruppo(1L);
        authId1.setFunzione(2L);

        authId2.setGruppo(1L);
        authId2.setFunzione(2L);

        // By default, equals() and hashCode() are from Object, so these will be false
        assertNotEquals(authId1, authId2, "Default equals might not behave as expected");
        assertNotEquals(authId1.hashCode(), authId2.hashCode(), "Default hashCode might not behave as expected");
    }
}
