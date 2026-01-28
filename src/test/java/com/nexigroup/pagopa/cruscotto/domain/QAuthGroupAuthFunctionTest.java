package com.nexigroup.pagopa.cruscotto.domain;

import com.querydsl.core.types.PathMetadataFactory;
import com.querydsl.core.types.dsl.PathInits;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class QAuthGroupAuthFunctionTest {

    @Test
    void testDefaultInstance() {
        QAuthGroupAuthFunction q = QAuthGroupAuthFunction.authGroupAuthFunction;
        assertNotNull(q);
        assertEquals("authGroupAuthFunction", q.getMetadata().getName());
    }

    @Test
    void testConstructorWithVariable() {
        QAuthGroupAuthFunction q = new QAuthGroupAuthFunction("testVar");
        assertNotNull(q);
        assertEquals("testVar", q.getMetadata().getName());
    }

    @Test
    void testConstructorWithPath() {
        QAuthGroupAuthFunction base = new QAuthGroupAuthFunction("base");
        QAuthGroupAuthFunction q = new QAuthGroupAuthFunction(base);
        assertNotNull(q);
        assertEquals("base", q.getMetadata().getName());
    }

    @Test
    void testConstructorWithMetadata() {
        var metadata = PathMetadataFactory.forVariable("metaVar");
        QAuthGroupAuthFunction q = new QAuthGroupAuthFunction(metadata);
        assertNotNull(q);
        assertEquals("metaVar", q.getMetadata().getName());
    }

    @Test
    void testConstructorWithMetadataAndInits() {
        var metadata = PathMetadataFactory.forVariable("metaVar2");
        QAuthGroupAuthFunction q = new QAuthGroupAuthFunction(metadata, PathInits.DIRECT2);
        assertNotNull(q);
        assertEquals("metaVar2", q.getMetadata().getName());
    }

    @Test
    void testFullConstructor() {
        var metadata = PathMetadataFactory.forVariable("fullVar");
        QAuthGroupAuthFunction q = new QAuthGroupAuthFunction(AuthGroupAuthFunction.class, metadata, PathInits.DIRECT2);
        assertNotNull(q);
        assertEquals("fullVar", q.getMetadata().getName());
    }

    @Test
    void testFunzioneAndGruppoInitialization() {
        QAuthGroupAuthFunction q = new QAuthGroupAuthFunction("checkInit");
        // The generated Q-class initializes these only if PathInits indicates so
        assertNotNull(q);
        // Not guaranteed to be initialized depending on QueryDSL version
        // So we only verify that access does not throw
        assertDoesNotThrow(q.funzione::getType, "funzione should be accessible without exceptions");
        assertDoesNotThrow(q.gruppo::getType, "gruppo should be accessible without exceptions");
    }
}
