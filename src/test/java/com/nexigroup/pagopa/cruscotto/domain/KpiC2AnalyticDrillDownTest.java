package com.nexigroup.pagopa.cruscotto.domain;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class KpiC2AnalyticDrillDownTest {

    @Test
    void testGettersAndSetters() {
        KpiC2AnalyticDrillDown entity = new KpiC2AnalyticDrillDown();
        Instance instance = new Instance();
        InstanceModule instanceModule = new InstanceModule();
        KpiC2AnalyticData analyticData = new KpiC2AnalyticData();

        LocalDate date = LocalDate.now();

        entity.setId(1L);
        entity.setInstance(instance);
        entity.setInstanceModule(instanceModule);
        entity.setAnalysisDate(date);
        entity.setEvaluationDate(date);
        entity.setInstitutionCf("ABC123");
        entity.setNumPayment(10L);
        entity.setNumNotification(5L);
        entity.setPercentNotification(BigDecimal.TEN);
        entity.setKpiC2AnalyticData(analyticData);

        assertEquals(1L, entity.getId());
        assertEquals(instance, entity.getInstance());
        assertEquals(instanceModule, entity.getInstanceModule());
        assertEquals(date, entity.getAnalysisDate());
        assertEquals(date, entity.getEvaluationDate());
        assertEquals("ABC123", entity.getInstitutionCf());
        assertEquals(10L, entity.getNumPayment());
        assertEquals(5L, entity.getNumNotification());
        assertEquals(BigDecimal.TEN, entity.getPercentNotification());
        assertEquals(analyticData, entity.getKpiC2AnalyticData());
    }

    @Test
    void testEqualsAndHashCode() {
        KpiC2AnalyticDrillDown a = new KpiC2AnalyticDrillDown();
        a.setId(1L);

        KpiC2AnalyticDrillDown b = new KpiC2AnalyticDrillDown();
        b.setId(1L);

        KpiC2AnalyticDrillDown c = new KpiC2AnalyticDrillDown();
        c.setId(2L);

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, c);
        assertNotEquals(a, new Object());
        assertNotEquals(a, null);
    }

    @Test
    void testEqualsWhenIdIsNull() {
        KpiC2AnalyticDrillDown a = new KpiC2AnalyticDrillDown();
        KpiC2AnalyticDrillDown b = new KpiC2AnalyticDrillDown();
        assertNotEquals(a, b);
    }

    @Test
    void testToStringContainsFields() {
        KpiC2AnalyticDrillDown entity = new KpiC2AnalyticDrillDown();
        entity.setId(1L);
        entity.setAnalysisDate(LocalDate.of(2024,1,1));
        entity.setInstitutionCf("CF123");
        entity.setNumPayment(2L);
        entity.setNumNotification(1L);
        entity.setPercentNotification(BigDecimal.ONE);

        String result = entity.toString();

        assertTrue(result.contains("id=1"));
        assertTrue(result.contains("analysisDate=2024-01-01"));
        assertTrue(result.contains("institutionCf='CF123'"));
        assertTrue(result.contains("numPayment=2"));
        assertTrue(result.contains("numNotification=1"));
        assertTrue(result.contains("percentNotification=1"));
    }

    @Test
    void testSerialVersionUID() throws Exception {
        long expectedUid = 1L;
        var field = KpiC2AnalyticDrillDown.class.getDeclaredField("serialVersionUID");
        field.setAccessible(true);
        assertEquals(expectedUid, field.get(null));
    }
}
