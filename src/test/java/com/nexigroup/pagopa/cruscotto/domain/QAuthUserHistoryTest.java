package com.nexigroup.pagopa.cruscotto.domain;

import com.querydsl.core.types.dsl.PathInits;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class QAuthUserHistoryTest {

    @Test
    void testDefaultInstance() {
        QAuthUserHistory instance = QAuthUserHistory.authUserHistory;
        assertNotNull(instance);
        assertNotNull(instance.dataModifica);
        assertNotNull(instance.id);
        assertNotNull(instance.password);
        // authUser may be null depending on initialization
    }

    @Test
    void testConstructors() {
        QAuthUserHistory instance1 = new QAuthUserHistory("variable");
        assertEquals("variable", instance1.getMetadata().getName());

        QAuthUserHistory instance2 = new QAuthUserHistory(QAuthUserHistory.authUserHistory);
        assertNotNull(instance2);

        QAuthUserHistory instance3 = new QAuthUserHistory(instance1.getMetadata());
        assertNotNull(instance3);

        QAuthUserHistory instance4 = new QAuthUserHistory(instance1.getMetadata(), PathInits.DIRECT2);
        assertNotNull(instance4);
    }

    @Test
    void testAuthUserInitialization() {
        QAuthUserHistory instance = new QAuthUserHistory("authUserHistory");
        if (instance.authUser != null) {
            assertNotNull(instance.authUser.id);
        }
    }
}
