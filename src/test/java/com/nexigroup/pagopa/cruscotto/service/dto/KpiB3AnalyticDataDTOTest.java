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

        // Assert getters
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
    void testEqualsAndHashCode() {
        KpiB3AnalyticDataDTO dto1 = new KpiB3AnalyticDataDTO();
        KpiB3AnalyticDataDTO dto2 = new KpiB3AnalyticDataDTO();

        // Initially equal (all fields null)
        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());

        // Set one field
        dto1.setId(1L);
        assertNotEquals(dto1, dto2);
        dto2.setId(1L);
        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());

        // Reflexivity
        assertEquals(dto1, dto1);

        // Symmetry
        assertTrue(dto1.equals(dto2) && dto2.equals(dto1));

        // Transitivity
        KpiB3AnalyticDataDTO dto3 = new KpiB3AnalyticDataDTO();
        dto3.setId(1L);
        assertTrue(dto1.equals(dto2) && dto2.equals(dto3) && dto1.equals(dto3));

        // Not equal to null or different class
        assertNotEquals(dto1, null);
        assertNotEquals(dto1, "string");
    }

    @Test
    void testToStringContainsFields() {
        KpiB3AnalyticDataDTO dto = new KpiB3AnalyticDataDTO();
        dto.setId(99L);
        dto.setEventType("TEST_TYPE");
        String str = dto.toString();

        assertTrue(str.contains("id=99"));
        assertTrue(str.contains("eventType=TEST_TYPE"));
        assertTrue(str.startsWith("KpiB3AnalyticDataDTO"));
    }

    @Test
    void testEqualsWithDifferentFields() {
        KpiB3AnalyticDataDTO dto1 = new KpiB3AnalyticDataDTO();
        KpiB3AnalyticDataDTO dto2 = new KpiB3AnalyticDataDTO();

        dto1.setId(1L);
        dto2.setId(2L);
        assertNotEquals(dto1, dto2);

        dto2.setId(1L);
        dto1.setEventType("TYPE_A");
        dto2.setEventType("TYPE_B");
        assertNotEquals(dto1, dto2);

        dto2.setEventType("TYPE_A");
        assertEquals(dto1, dto2);
    }

    @Test
    void testNullSafety() {
        KpiB3AnalyticDataDTO dto = new KpiB3AnalyticDataDTO();
        dto.setEventId(null);
        dto.setEventType(null);
        dto.setEventTimestamp(null);
        dto.setAnalysisDate(null);
        dto.setAnalysisPeriod(null);
        dto.setStationFiscalCode(null);

        assertNull(dto.getEventId());
        assertNull(dto.getEventType());
        assertNull(dto.getEventTimestamp());
        assertNull(dto.getAnalysisDate());
        assertNull(dto.getAnalysisPeriod());
        assertNull(dto.getStationFiscalCode());
    }
}
