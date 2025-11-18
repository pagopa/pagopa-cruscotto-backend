package com.nexigroup.pagopa.cruscotto.domain;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class KpiC2AnalyticDataTest {

    @Test
    void testGettersAndSetters() {

        KpiC2AnalyticData data = new KpiC2AnalyticData();

        Instance instance = new Instance();
        InstanceModule instanceModule = new InstanceModule();
        KpiC2DetailResult detail = new KpiC2DetailResult();

        LocalDate analysisDate = LocalDate.now();
        LocalDate evaluationDate = LocalDate.now();

        data.setId(1L);
        data.setInstance(instance);
        data.setInstanceModule(instanceModule);
        data.setKpiC2DetailResult(detail);
        data.setAnalysisDate(analysisDate);
        data.setEvaluationDate(evaluationDate);
        data.setNumInstitution(10L);
        data.setNumInstitutionSend(8L);
        data.setPerInstitutionSend(new BigDecimal("80.00"));
        data.setNumPayment(20L);
        data.setNumNotification(100L);
        data.setPerNotification(new BigDecimal("50.00"));

        assertEquals(1L, data.getId());
        assertEquals(instance, data.getInstance());
        assertEquals(instanceModule, data.getInstanceModule());
        assertEquals(detail, data.getKpiC2DetailResult());
        assertEquals(analysisDate, data.getAnalysisDate());
        assertEquals(evaluationDate, data.getEvaluationDate());
        assertEquals(10L, data.getNumInstitution());
        assertEquals(8L, data.getNumInstitutionSend());
        assertEquals(new BigDecimal("80.00"), data.getPerInstitutionSend());
        assertEquals(20L, data.getNumPayment());
        assertEquals(100L, data.getNumNotification());
        assertEquals(new BigDecimal("50.00"), data.getPerNotification());
    }

    @Test
    void testEqualsAndHashCode() {
        KpiC2AnalyticData d1 = new KpiC2AnalyticData();
        KpiC2AnalyticData d2 = new KpiC2AnalyticData();

        d1.setId(1L);
        d2.setId(1L);

        assertEquals(d1, d2);
        assertEquals(d1.hashCode(), d2.hashCode());

        d2.setId(2L);
        assertNotEquals(d1, d2);

        assertNotEquals(d1, null);
        assertNotEquals(d1, new Object());
    }

    @Test
    void testToString() {
        KpiC2AnalyticData data = new KpiC2AnalyticData();
        data.setId(1L);
        data.setAnalysisDate(LocalDate.of(2024, 1, 1));
        data.setEvaluationDate(LocalDate.of(2024, 2, 1));
        data.setNumNotification(50L);

        String result = data.toString();

        assertTrue(result.contains("id=1"));
        assertTrue(result.contains("DT_ANALISYS_DATE") || result.contains("analysisDate"));
        assertTrue(result.contains("numNotification"));
    }
}
