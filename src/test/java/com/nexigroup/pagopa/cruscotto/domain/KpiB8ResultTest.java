package com.nexigroup.pagopa.cruscotto.domain;

import com.nexigroup.pagopa.cruscotto.domain.enumeration.EvaluationType;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class KpiB8ResultTest {

    @Test
    void testGettersAndSetters() {
        KpiB8Result result = new KpiB8Result();
        Instance instance = new Instance();
        InstanceModule module = new InstanceModule();

        result.setId(1L);
        result.setInstance(instance);
        result.setInstanceModule(module);
        result.setAnalysisDate(LocalDate.of(2025, 1, 1));
        result.setEligibilityThreshold(0.75);
        result.setTolerance(0.05);
        result.setEvaluationType(EvaluationType.MESE);
        result.setOutcome(OutcomeStatus.OK);

        assertEquals(1L, result.getId());
        assertEquals(instance, result.getInstance());
        assertEquals(module, result.getInstanceModule());
        assertEquals(LocalDate.of(2025, 1, 1), result.getAnalysisDate());
        assertEquals(0.75, result.getEligibilityThreshold());
        assertEquals(0.05, result.getTolerance());
        assertEquals(EvaluationType.MESE, result.getEvaluationType());
        assertEquals(OutcomeStatus.OK, result.getOutcome());
    }

    @Test
    void testEqualsAndHashCode() {
        KpiB8Result r1 = new KpiB8Result();
        r1.setId(1L);

        KpiB8Result r2 = new KpiB8Result();
        r2.setId(1L);

        KpiB8Result r3 = new KpiB8Result();
        r3.setId(2L);

        assertEquals(r1, r2);
        assertEquals(r1.hashCode(), r2.hashCode());
        assertNotEquals(r1, r3);
        assertNotEquals(r1.hashCode(), r3.hashCode());
    }

    @Test
    void testToString() {
        KpiB8Result result = new KpiB8Result();
        result.setId(10L);
        result.setAnalysisDate(LocalDate.of(2025, 11, 18));
        result.setEligibilityThreshold(0.9);
        result.setTolerance(0.1);
        result.setEvaluationType(EvaluationType.TOTALE);
        result.setOutcome(OutcomeStatus.KO);

        String str = result.toString();
        assertTrue(str.contains("KpiB8Result"));
        assertTrue(str.contains("id=10"));
        assertTrue(str.contains("eligibilityThreshold=0.9"));
        assertTrue(str.contains("tolerance=0.1"));
        assertTrue(str.contains("evaluationType=TOTALE"));
        assertTrue(str.contains("outcome=KO"));
    }
}
