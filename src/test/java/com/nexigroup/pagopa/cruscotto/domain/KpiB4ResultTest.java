package com.nexigroup.pagopa.cruscotto.domain;

import static org.junit.jupiter.api.Assertions.*;

import com.nexigroup.pagopa.cruscotto.domain.enumeration.EvaluationType;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

class KpiB4ResultTest {

    @Test
    void testGettersAndSetters() {
        KpiB4Result result = new KpiB4Result();

        Instance instance = new Instance();
        InstanceModule module = new InstanceModule();

        result.setId(1L);
        result.setInstance(instance);
        result.setInstanceModule(module);
        result.setAnalysisDate(LocalDate.of(2025, 1, 1));
        result.setExcludePlannedShutdown(true);
        result.setExcludeUnplannedShutdown(false);
        result.setEligibilityThreshold(95.5);
        result.setTolerance(0.05);
        result.setEvaluationType(EvaluationType.MESE);
        result.setOutcome(OutcomeStatus.OK);

        assertEquals(1L, result.getId());
        assertEquals(instance, result.getInstance());
        assertEquals(module, result.getInstanceModule());
        assertEquals(LocalDate.of(2025, 1, 1), result.getAnalysisDate());
        assertTrue(result.getExcludePlannedShutdown());
        assertFalse(result.getExcludeUnplannedShutdown());
        assertEquals(95.5, result.getEligibilityThreshold());
        assertEquals(0.05, result.getTolerance());
        assertEquals(EvaluationType.MESE, result.getEvaluationType());
        assertEquals(OutcomeStatus.OK, result.getOutcome());
    }

    @Test
    void testEqualsAndHashCode() {
        KpiB4Result r1 = new KpiB4Result();
        r1.setId(100L);

        KpiB4Result r2 = new KpiB4Result();
        r2.setId(100L);

        KpiB4Result r3 = new KpiB4Result();
        r3.setId(200L);

        assertEquals(r1, r2);
        assertEquals(r1.hashCode(), r2.hashCode());
        assertNotEquals(r1, r3);
        assertNotEquals(r1.hashCode(), r3.hashCode());
    }

    @Test
    void testToStringContainsFields() {
        KpiB4Result result = new KpiB4Result();
        result.setId(1L);
        result.setAnalysisDate(LocalDate.of(2025, 1, 1));
        result.setExcludePlannedShutdown(true);
        result.setExcludeUnplannedShutdown(false);
        result.setEligibilityThreshold(90.0);
        result.setTolerance(0.1);
        result.setEvaluationType(EvaluationType.TOTALE);
        result.setOutcome(OutcomeStatus.KO);

        String str = result.toString();
        assertTrue(str.contains("KpiB4Result"));
        assertTrue(str.contains("id=1"));
        assertTrue(str.contains("analysisDate=2025-01-01"));
        assertTrue(str.contains("excludePlannedShutdown=true"));
        assertTrue(str.contains("excludeUnplannedShutdown=false"));
        assertTrue(str.contains("eligibilityThreshold=90.0"));
        assertTrue(str.contains("tolerance=0.1"));
        assertTrue(str.contains("evaluationType=TOTALE"));
        assertTrue(str.contains("outcome=KO"));
    }
}
