package com.nexigroup.pagopa.cruscotto.service.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class KpiB3AnalyticDataDTOTest {

    @Test
    void testGettersAndSetters() {
        KpiB3AnalyticDataDTO dto = new KpiB3AnalyticDataDTO();

        // Set fields
        dto.setId(1L);
        dto.setInstanceId(2L);
        dto.setInstanceModuleId(3L);
        dto.setAnagStationId(4L);
        dto.setKpiB3DetailResultId(5L);
        dto.setEventId("event-123");
        dto.setEventType("TYPE_A");
        LocalDateTime eventTimestamp = LocalDateTime.now();
        dto.setEventTimestamp(eventTimestamp);
        dto.setStandInCount(10);
        LocalDate analysisDate = LocalDate.of(2025, 10, 30);
        dto.setAnalysisDate(analysisDate);
        dto.setAnalysisPeriod("MONTHLY");
        dto.setStationFiscalCode("FISCAL123");

        // Assert fields
        assertEquals(1L, dto.getId());
        assertEquals(2L, dto.getInstanceId());
        assertEquals(3L, dto.getInstanceModuleId());
        assertEquals(4L, dto.getAnagStationId());
        assertEquals(5L, dto.getKpiB3DetailResultId());
        assertEquals("event-123", dto.getEventId());
        assertEquals("TYPE_A", dto.getEventType());
        assertEquals(eventTimestamp, dto.getEventTimestamp());
        assertEquals(10, dto.getStandInCount());
        assertEquals(analysisDate, dto.getAnalysisDate());
        assertEquals("MONTHLY", dto.getAnalysisPeriod());
        assertEquals("FISCAL123", dto.getStationFiscalCode());
    }

    @Test
    void testToString() {
        KpiB3AnalyticDataDTO dto = new KpiB3AnalyticDataDTO();
        dto.setId(1L);
        String toString = dto.toString();
        assertTrue(toString.contains("id=1"));
    }

}
