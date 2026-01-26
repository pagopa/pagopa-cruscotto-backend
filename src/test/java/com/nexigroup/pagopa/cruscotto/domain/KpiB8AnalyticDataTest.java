package com.nexigroup.pagopa.cruscotto.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class KpiB8AnalyticDataTest {

    @Test
    void testGettersAndSetters() {
        KpiB8AnalyticData data = new KpiB8AnalyticData();

        Instance instance = new Instance();
        KpiB8DetailResult detailResult = new KpiB8DetailResult();

        data.setId(1L);
        data.setInstanceId(100L);
        data.setInstance(instance);
        data.setAnalysisDate(LocalDate.of(2025, 1, 1));
        data.setEvaluationDate(LocalDate.of(2025, 1, 2));
        data.setTotReq(50L);
        data.setReqKO(5L);
        data.setKpiB8DetailResult(detailResult);

        assertEquals(1L, data.getId());
        assertEquals(100L, data.getInstanceId());
        assertEquals(instance, data.getInstance());
        assertEquals(LocalDate.of(2025, 1, 1), data.getAnalysisDate());
        assertEquals(LocalDate.of(2025, 1, 2), data.getEvaluationDate());
        assertEquals(50L, data.getTotReq());
        assertEquals(5L, data.getReqKO());
        assertEquals(detailResult, data.getKpiB8DetailResult());
    }

    @Test
    void testEqualsAndHashCode() {
        KpiB8AnalyticData data1 = new KpiB8AnalyticData();
        data1.setId(1L);

        KpiB8AnalyticData data2 = new KpiB8AnalyticData();
        data2.setId(1L);

        KpiB8AnalyticData data3 = new KpiB8AnalyticData();
        data3.setId(2L);

        assertEquals(data1, data2);
        assertNotEquals(data1, data3);
        assertEquals(data1.hashCode(), data2.hashCode());
    }

    @Test
    void testEqualsWithNullId() {
        KpiB8AnalyticData data1 = new KpiB8AnalyticData();
        KpiB8AnalyticData data2 = new KpiB8AnalyticData();

        assertNotEquals(data1, data2); // both ids null
    }

    @Test
    void testToString() {
        KpiB8AnalyticData data = new KpiB8AnalyticData();
        data.setId(1L);
        data.setInstanceId(100L);
        data.setAnalysisDate(LocalDate.of(2025, 1, 1));
        data.setEvaluationDate(LocalDate.of(2025, 1, 2));
        data.setTotReq(50L);
        data.setReqKO(5L);

        String str = data.toString();
        assertTrue(str.contains("id=1"));
        assertTrue(str.contains("instanceId=100"));
        assertTrue(str.contains("totReq=50"));
        assertTrue(str.contains("reqKO=5"));
    }
}
