package com.nexigroup.pagopa.cruscotto.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AuthGroupAuthFunctionTest {

    @Test
    void testGetterSetter() {
        AuthGroupAuthFunction entity = new AuthGroupAuthFunction();
        AuthFunction funzione = new AuthFunction();
        AuthGroup gruppo = new AuthGroup();

        entity.setFunzione(funzione);
        entity.setGruppo(gruppo);

        assertEquals(funzione, entity.getFunzione());
        assertEquals(gruppo, entity.getGruppo());
    }

    @Test
    void testEqualsSameObject() {
        AuthGroupAuthFunction entity = new AuthGroupAuthFunction();
        assertEquals(entity, entity);
    }

    @Test
    void testEqualsDifferentType() {
        AuthGroupAuthFunction entity = new AuthGroupAuthFunction();
        assertNotEquals(new Object(), entity);
    }

    @Test
    void testEqualsNull() {
        AuthGroupAuthFunction entity = new AuthGroupAuthFunction();
        assertNotEquals(null, entity);
    }

    @Test
    void testEqualsAndHashCodeWithSameValues() {
        AuthFunction funzione = new AuthFunction();
        AuthGroup gruppo = new AuthGroup();

        AuthGroupAuthFunction entity1 = new AuthGroupAuthFunction();
        entity1.setFunzione(funzione);
        entity1.setGruppo(gruppo);

        AuthGroupAuthFunction entity2 = new AuthGroupAuthFunction();
        entity2.setFunzione(funzione);
        entity2.setGruppo(gruppo);

        assertEquals(entity2, entity1);
        assertEquals(entity2.hashCode(), entity1.hashCode());
    }

    @Test
    void testEqualsAndHashCodeWithDifferentValues() {
        AuthFunction funzione1 = new AuthFunction();
        AuthFunction funzione2 = new AuthFunction();

        AuthGroup gruppo1 = new AuthGroup();
        AuthGroup gruppo2 = new AuthGroup();

        AuthGroupAuthFunction entity1 = new AuthGroupAuthFunction();
        entity1.setFunzione(funzione1);
        entity1.setGruppo(gruppo1);

        AuthGroupAuthFunction entity2 = new AuthGroupAuthFunction();
        entity2.setFunzione(funzione2);
        entity2.setGruppo(gruppo2);

        assertNotEquals(entity2, entity1);
        // don't assert hashCode inequality when underlying entities might have null IDs
    }

    @Test
    void testEqualsWhenOneFieldIsNull() {
        AuthGroupAuthFunction entity1 = new AuthGroupAuthFunction();
        AuthGroupAuthFunction entity2 = new AuthGroupAuthFunction();

        entity1.setFunzione(new AuthFunction());
        entity2.setFunzione(null);

        entity1.setGruppo(new AuthGroup());
        entity2.setGruppo(new AuthGroup());

        assertNotEquals(entity2, entity1);
    }
}
