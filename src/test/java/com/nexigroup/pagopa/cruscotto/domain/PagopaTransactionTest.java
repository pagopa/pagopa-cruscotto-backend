package com.nexigroup.pagopa.cruscotto.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class PagopaTransactionTest {

    @Test
    void gettersAndSettersAndToString() {
        PagopaTransaction tx = new PagopaTransaction();
        tx.setId(42L);
        tx.setCfPartner("PARTNER1");
        tx.setCfInstitution("INST1");
        tx.setDate(LocalDate.of(2025, 1, 2));
        tx.setStation("STN-001");
        tx.setTransactionTotal(10);

        assertEquals(42L, tx.getId());
        assertEquals("PARTNER1", tx.getCfPartner());
        assertEquals("INST1", tx.getCfInstitution());
        assertEquals(LocalDate.of(2025, 1, 2), tx.getDate());
        assertEquals("STN-001", tx.getStation());
        assertEquals(10, tx.getTransactionTotal());

        String s = tx.toString();
        assertTrue(s.contains("id=42"));
        assertTrue(s.contains("cfPartner='PARTNER1'"));
        assertTrue(s.contains("cfInstitution='INST1'"));
        assertTrue(s.contains("date=2025-01-02"));
        assertTrue(s.contains("station='STN-001'"));
        assertTrue(s.contains("transactionTotal=10"));
    }

    @Test
    void equalsAndHashCode_sameId() {
        PagopaTransaction a = new PagopaTransaction();
        PagopaTransaction b = new PagopaTransaction();
        a.setId(1L);
        b.setId(1L);

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void equalsAndHashCode_differentId() {
        PagopaTransaction a = new PagopaTransaction();
        PagopaTransaction b = new PagopaTransaction();
        a.setId(1L);
        b.setId(2L);

        assertNotEquals(a, b);
        // hashCode may collide rarely but for different ids it's expected to differ in normal cases
        assertNotEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void equals_withNullId_bothNull() {
        PagopaTransaction a = new PagopaTransaction();
        PagopaTransaction b = new PagopaTransaction();
        // both ids null
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void equals_withNullAndOtherType() {
        PagopaTransaction a = new PagopaTransaction();
        a.setId(5L);

        assertNotEquals(null, a);
        assertNotEquals(a, null);
        assertNotEquals(a, "some string");
    }
}
