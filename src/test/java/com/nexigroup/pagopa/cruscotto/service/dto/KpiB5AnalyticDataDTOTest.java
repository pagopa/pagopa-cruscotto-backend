package com.nexigroup.pagopa.cruscotto.service.dto;

import static org.junit.jupiter.api.Assertions.*;

import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class KpiB5AnalyticDataDTOTest {

    @Test
    void testGettersAndSetters() {
        KpiB5AnalyticDataDTO dto = new KpiB5AnalyticDataDTO();

        Long id = 1L;
        Long instanceId = 10L;
        Long instanceModuleId = 20L;
        Long kpiB5DetailResultId = 30L;
        LocalDate analysisDate = LocalDate.of(2025, 10, 30);
        Integer stationsPresent = 100;
        Integer stationsWithoutSpontaneous = 20;
        BigDecimal percentageNoSpontaneous = new BigDecimal("20.0");
        OutcomeStatus outcome = OutcomeStatus.OK; // adapt if enum differs

        dto.setId(id);
        dto.setInstanceId(instanceId);
        dto.setInstanceModuleId(instanceModuleId);
        dto.setKpiB5DetailResultId(kpiB5DetailResultId);
        dto.setAnalysisDate(analysisDate);
        dto.setStationsPresent(stationsPresent);
        dto.setStationsWithoutSpontaneous(stationsWithoutSpontaneous);
        dto.setPercentageNoSpontaneous(percentageNoSpontaneous);
        dto.setOutcome(outcome);

        assertEquals(id, dto.getId());
        assertEquals(instanceId, dto.getInstanceId());
        assertEquals(instanceModuleId, dto.getInstanceModuleId());
        assertEquals(kpiB5DetailResultId, dto.getKpiB5DetailResultId());
        assertEquals(analysisDate, dto.getAnalysisDate());
        assertEquals(stationsPresent, dto.getStationsPresent());
        assertEquals(stationsWithoutSpontaneous, dto.getStationsWithoutSpontaneous());
        assertEquals(percentageNoSpontaneous, dto.getPercentageNoSpontaneous());
        assertEquals(outcome, dto.getOutcome());
    }

    @Test
    void testEqualsAndHashCode() {
        KpiB5AnalyticDataDTO dto1 = new KpiB5AnalyticDataDTO();
        KpiB5AnalyticDataDTO dto2 = new KpiB5AnalyticDataDTO();
        KpiB5AnalyticDataDTO dto3 = new KpiB5AnalyticDataDTO();

        dto1.setId(1L);
        dto2.setId(1L);
        dto3.setId(2L);

        // Same ID -> equal
        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());

        // Different ID -> not equal
        assertNotEquals(dto1, dto3);

        // Null ID -> not equal
        dto1.setId(null);
        assertNotEquals(dto1, dto2);

        // Self comparison
        assertEquals(dto1, dto1);

        // Different type -> not equal
        assertNotEquals(dto1, "string");
    }

    @Test
    void testToStringContainsAllFields() {
        KpiB5AnalyticDataDTO dto = new KpiB5AnalyticDataDTO();
        dto.setId(1L);
        dto.setInstanceId(10L);
        dto.setInstanceModuleId(20L);
        dto.setKpiB5DetailResultId(30L);
        dto.setAnalysisDate(LocalDate.of(2025, 10, 30));
        dto.setStationsPresent(100);
        dto.setStationsWithoutSpontaneous(10);
        dto.setPercentageNoSpontaneous(new BigDecimal("10.0"));
        dto.setOutcome(OutcomeStatus.OK); // adapt if needed

        String result = dto.toString();

        assertTrue(result.contains("id=1"));
        assertTrue(result.contains("instanceId=10"));
        assertTrue(result.contains("instanceModuleId=20"));
        assertTrue(result.contains("kpiB5DetailResultId=30"));
        assertTrue(result.contains("analysisDate=2025-10-30"));
        assertTrue(result.contains("stationsPresent=100"));
        assertTrue(result.contains("stationsWithoutSpontaneous=10"));
        assertTrue(result.contains("percentageNoSpontaneous=10.0"));
        assertTrue(result.contains("outcome=OK"));
    }
}
