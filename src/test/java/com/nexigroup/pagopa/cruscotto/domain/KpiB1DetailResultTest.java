package com.nexigroup.pagopa.cruscotto.domain;

import com.nexigroup.pagopa.cruscotto.domain.enumeration.EvaluationType;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class KpiB1DetailResultTest {

    private KpiB1DetailResult result;
    private Instance instance;
    private InstanceModule instanceModule;
    private KpiB1Result kpiB1Result;

    @BeforeEach
    void setUp() {
        result = new KpiB1DetailResult();
        instance = new Instance();
        instanceModule = new InstanceModule();
        kpiB1Result = new KpiB1Result();

        result.setId(1L);
        result.setInstance(instance);
        result.setInstanceModule(instanceModule);
        result.setKpiB1Result(kpiB1Result);
        result.setAnalysisDate(LocalDate.of(2024, 10, 30));
        result.setEvaluationType(EvaluationType.MESE);
        result.setEvaluationStartDate(LocalDate.of(2024, 10, 1));
        result.setEvaluationEndDate(LocalDate.of(2024, 10, 15));
        result.setTotalInstitutions(100);
        result.setInstitutionDifference(10);
        result.setInstitutionDifferencePercentage(new BigDecimal("10.00"));
        result.setInstitutionOutcome(OutcomeStatus.OK);
        result.setTotalTransactions(200);
        result.setTransactionDifference(20);
        result.setTransactionDifferencePercentage(new BigDecimal("10.00"));
        result.setTransactionOutcome(OutcomeStatus.KO);
    }

    @Test
    void testGettersAndSetters() {
        assertEquals(1L, result.getId());
        assertEquals(instance, result.getInstance());
        assertEquals(instanceModule, result.getInstanceModule());
        assertEquals(kpiB1Result, result.getKpiB1Result());
        assertEquals(LocalDate.of(2024, 10, 30), result.getAnalysisDate());
        assertEquals(EvaluationType.MESE, result.getEvaluationType());
        assertEquals(LocalDate.of(2024, 10, 1), result.getEvaluationStartDate());
        assertEquals(LocalDate.of(2024, 10, 15), result.getEvaluationEndDate());
        assertEquals(100, result.getTotalInstitutions());
        assertEquals(10, result.getInstitutionDifference());
        assertEquals(new BigDecimal("10.00"), result.getInstitutionDifferencePercentage());
        assertEquals(OutcomeStatus.OK, result.getInstitutionOutcome());
        assertEquals(200, result.getTotalTransactions());
        assertEquals(20, result.getTransactionDifference());
        assertEquals(new BigDecimal("10.00"), result.getTransactionDifferencePercentage());
        assertEquals(OutcomeStatus.KO, result.getTransactionOutcome());
    }

    @Test
    void testEqualsAndHashCode_sameId() {
        KpiB1DetailResult another = new KpiB1DetailResult();
        another.setId(1L);

        assertEquals(result, another);
        assertEquals(result.hashCode(), another.hashCode());
    }

    @Test
    void testEqualsAndHashCode_differentId() {
        KpiB1DetailResult another = new KpiB1DetailResult();
        another.setId(2L);

        assertNotEquals(result, another);
        assertNotEquals(result.hashCode(), another.hashCode());
    }

    @Test
    void testEquals_nullOrDifferentType() {
        assertNotEquals(result, null);
        assertNotEquals(result, "Some String");
    }

    @Test
    void testToString_containsKeyFields() {
        String str = result.toString();
        assertTrue(str.contains("KpiB1DetailResult"));
        assertTrue(str.contains("id=1"));
        assertTrue(str.contains("analysisDate=2024-10-30"));
        assertTrue(str.contains("evaluationType=MESE"));
    }
}
