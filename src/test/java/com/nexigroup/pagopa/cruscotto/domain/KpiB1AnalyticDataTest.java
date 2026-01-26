package com.nexigroup.pagopa.cruscotto.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class KpiB1AnalyticDataTest {

    @Test
    void testGettersAndSetters() {
        KpiB1AnalyticData data = new KpiB1AnalyticData();

        data.setId(1L);
        assertEquals(1L, data.getId());

        Instance instance = new Instance();
        data.setInstance(instance);
        assertEquals(instance, data.getInstance());

        InstanceModule module = new InstanceModule();
        data.setInstanceModule(module);
        assertEquals(module, data.getInstanceModule());

        LocalDate analysisDate = LocalDate.of(2025, 1, 1);
        data.setAnalysisDate(analysisDate);
        assertEquals(analysisDate, data.getAnalysisDate());

        LocalDate dataDate = LocalDate.of(2025, 1, 2);
        data.setDataDate(dataDate);
        assertEquals(dataDate, data.getDataDate());

        data.setInstitutionCount(10);
        assertEquals(10, data.getInstitutionCount());

        data.setTransactionCount(100);
        assertEquals(100, data.getTransactionCount());

        KpiB1DetailResult detailResult = new KpiB1DetailResult();
        data.setKpiB1DetailResult(detailResult);
        assertEquals(detailResult, data.getKpiB1DetailResult());
    }

    @Test
    void testEqualsAndHashCode() {
        KpiB1AnalyticData data1 = new KpiB1AnalyticData();
        data1.setId(1L);

        KpiB1AnalyticData data2 = new KpiB1AnalyticData();
        data2.setId(1L);

        KpiB1AnalyticData data3 = new KpiB1AnalyticData();
        data3.setId(2L);

        assertEquals(data1, data2);
        assertEquals(data1.hashCode(), data2.hashCode());
        assertNotEquals(data1, data3);
        assertNotEquals(data1.hashCode(), data3.hashCode());
    }

    @Test
    void testToString() {
        KpiB1AnalyticData data = new KpiB1AnalyticData();
        data.setId(1L);

        String str = data.toString();
        assertTrue(str.contains("id=1"));
    }
}
