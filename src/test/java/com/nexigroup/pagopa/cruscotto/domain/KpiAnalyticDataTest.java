package com.nexigroup.pagopa.cruscotto.domain;

import com.nexigroup.pagopa.cruscotto.domain.enumeration.ModuleCode;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class KpiAnalyticDataTest {

    @Test
    void testEqualsAndHashCode() {
        KpiAnalyticData data1 = new KpiAnalyticData();
        data1.setId(1L);

        KpiAnalyticData data2 = new KpiAnalyticData();
        data2.setId(1L);

        KpiAnalyticData data3 = new KpiAnalyticData();
        data3.setId(2L);

        // same object
        assertEquals(data1, data1);

        // different objects with same ID
        assertEquals(data1, data2);
        assertEquals(data1.hashCode(), data2.hashCode());

        // different IDs
        assertNotEquals(data1, data3);

        // null comparison
        assertNotEquals(data1, null);

        // different class
        assertNotEquals(data1, "string");
    }

    @Test
    void testToStringContainsAllFields() {
        KpiAnalyticData data = new KpiAnalyticData();
        data.setId(10L);
        data.setModuleCode(ModuleCode.B3);
        data.setInstanceId(100L);
        data.setInstanceModuleId(200L);
        data.setAnalysisDate(LocalDate.of(2025, 11, 12));
        data.setDataDate(LocalDate.of(2025, 11, 11));
        data.setKpiDetailResultId(300L);
        data.setAnalyticData("{\"metric\":123}");

        String str = data.toString();

        assertTrue(str.contains("id=10"));
        assertTrue(str.contains("moduleCode=B3"));
        assertTrue(str.contains("instanceId=100"));
        assertTrue(str.contains("instanceModuleId=200"));
        assertTrue(str.contains("analysisDate=2025-11-12"));
        assertTrue(str.contains("dataDate=2025-11-11"));
        assertTrue(str.contains("kpiDetailResultId=300"));
        assertTrue(str.contains("analyticData={\"metric\":123}"));
    }

    @Test
    void testAllGettersAndSetters() {
        KpiAnalyticData data = new KpiAnalyticData();

        data.setId(1L);
        assertEquals(1L, data.getId());

        data.setModuleCode(ModuleCode.B1);
        assertEquals(ModuleCode.B1, data.getModuleCode());

        data.setInstanceId(10L);
        assertEquals(10L, data.getInstanceId());

        data.setInstanceModuleId(20L);
        assertEquals(20L, data.getInstanceModuleId());

        LocalDate analysisDate = LocalDate.now();
        data.setAnalysisDate(analysisDate);
        assertEquals(analysisDate, data.getAnalysisDate());

        LocalDate dataDate = LocalDate.now().minusDays(1);
        data.setDataDate(dataDate);
        assertEquals(dataDate, data.getDataDate());

        data.setKpiDetailResultId(30L);
        assertEquals(30L, data.getKpiDetailResultId());

        String json = "{\"value\":42}";
        data.setAnalyticData(json);
        assertEquals(json, data.getAnalyticData());
    }
}
