package com.nexigroup.pagopa.cruscotto.domain;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;

class PagoPaPaymentReceiptDrilldownTest {

    @Test
    void testEqualsAndHashCode_sameId_shouldBeEqual() {
        PagoPaPaymentReceiptDrilldown d1 = new PagoPaPaymentReceiptDrilldown();
        d1.setId(1L);

        PagoPaPaymentReceiptDrilldown d2 = new PagoPaPaymentReceiptDrilldown();
        d2.setId(1L);

        assertEquals(d1, d2);
        assertEquals(d1.hashCode(), d2.hashCode());
    }

    @Test
    void testEqualsAndHashCode_differentId_shouldNotBeEqual() {
        PagoPaPaymentReceiptDrilldown d1 = new PagoPaPaymentReceiptDrilldown();
        d1.setId(1L);

        PagoPaPaymentReceiptDrilldown d2 = new PagoPaPaymentReceiptDrilldown();
        d2.setId(2L);

        assertNotEquals(d1, d2);
        assertNotEquals(d1.hashCode(), d2.hashCode());
    }

    @Test
    void testEquals_withNullAndDifferentClass() {
        PagoPaPaymentReceiptDrilldown d1 = new PagoPaPaymentReceiptDrilldown();
        d1.setId(1L);

        assertNotEquals(d1, null);
        assertNotEquals(d1, new Object());
    }

    @Test
    void testToString_containsExpectedFields() {
        PagoPaPaymentReceiptDrilldown d = new PagoPaPaymentReceiptDrilldown();
        d.setId(99L);
        d.setAnalysisDate(LocalDate.of(2025, 1, 1));
        d.setEvaluationDate(LocalDate.of(2025, 1, 2));
        d.setStartTime(Instant.parse("2025-01-01T10:00:00Z"));
        d.setEndTime(Instant.parse("2025-01-01T10:15:00Z"));
        d.setTotRes(100L);
        d.setResOk(90L);
        d.setResKo(10L);

        String str = d.toString();

        assertTrue(str.contains("id=99"));
        assertTrue(str.contains("analysisDate=2025-01-01"));
        assertTrue(str.contains("evaluationDate=2025-01-02"));
        assertTrue(str.contains("totRes=100"));
        assertTrue(str.contains("resOk=90"));
        assertTrue(str.contains("resKo=10"));
    }

    @Test
    void testGettersAndSetters() {
        PagoPaPaymentReceiptDrilldown d = new PagoPaPaymentReceiptDrilldown();
        LocalDate analysisDate = LocalDate.now();
        LocalDate evaluationDate = LocalDate.now().plusDays(1);
        Instant start = Instant.now();
        Instant end = start.plusSeconds(900);

        d.setId(5L);
        d.setAnalysisDate(analysisDate);
        d.setEvaluationDate(evaluationDate);
        d.setStartTime(start);
        d.setEndTime(end);
        d.setTotRes(50L);
        d.setResOk(45L);
        d.setResKo(5L);

        assertEquals(5L, d.getId());
        assertEquals(analysisDate, d.getAnalysisDate());
        assertEquals(evaluationDate, d.getEvaluationDate());
        assertEquals(start, d.getStartTime());
        assertEquals(end, d.getEndTime());
        assertEquals(50L, d.getTotRes());
        assertEquals(45L, d.getResOk());
        assertEquals(5L, d.getResKo());
    }
}
