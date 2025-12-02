package com.nexigroup.pagopa.cruscotto.service.dto;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

class KpiB6AnalyticDataDTOTest {

    @Test
    void testDefaultConstructorAndSettersGetters() {
        KpiB6AnalyticDataDTO dto = new KpiB6AnalyticDataDTO();
        dto.setId(1L);
        dto.setInstanceId(2L);
        dto.setInstanceModuleId(3L);
        dto.setAnagStationId(4L);
        dto.setKpiB6DetailResultId(5L);
        dto.setEventId("evt123");
        dto.setEventType("TYPE_A");
        dto.setAnalysisDate(LocalDate.of(2025, 10, 29));
        dto.setStationCode("101");
        dto.setPaymentOption("CARD");

        assertEquals(1L, dto.getId());
        assertEquals(2L, dto.getInstanceId());
        assertEquals(3L, dto.getInstanceModuleId());
        assertEquals(4L, dto.getAnagStationId());
        assertEquals(5L, dto.getKpiB6DetailResultId());
        assertEquals("evt123", dto.getEventId());
        assertEquals("TYPE_A", dto.getEventType());
        assertEquals(LocalDate.of(2025, 10, 29), dto.getAnalysisDate());
        assertEquals("101", dto.getStationCode());
        assertEquals("CARD", dto.getPaymentOption());
    }

    @Test
    void testConstructorFromGenericDtoWithJson() throws JsonProcessingException {
        String analyticDataJson = """
                {
                    "stationCode": 200,
                    "anagStationId": 10,
                    "eventId": "evt999",
                    "eventType": "TYPE_B",
                    "paymentOption": "CASH"
                }
                """;

        KpiAnalyticDataDTO genericDto = new KpiAnalyticDataDTO();
        genericDto.setId(1L);
        genericDto.setInstanceId(2L);
        genericDto.setInstanceModuleId(3L);
        genericDto.setKpiDetailResultId(5L);
        genericDto.setAnalysisDate(LocalDate.of(2025, 10, 29));
        genericDto.setAnalyticData(analyticDataJson);

        KpiB6AnalyticDataDTO b6Dto = new KpiB6AnalyticDataDTO(genericDto);

        assertEquals(1L, b6Dto.getId());
        assertEquals(2L, b6Dto.getInstanceId());
        assertEquals(3L, b6Dto.getInstanceModuleId());
        assertEquals(5L, b6Dto.getKpiB6DetailResultId());
        assertEquals(LocalDate.of(2025, 10, 29), b6Dto.getAnalysisDate());
        assertEquals("200", b6Dto.getStationCode());
        assertEquals(10L, b6Dto.getAnagStationId());
        assertEquals("evt999", b6Dto.getEventId());
        assertEquals("TYPE_B", b6Dto.getEventType());
        assertEquals("CASH", b6Dto.getPaymentOption());
    }

    @Test
    void testEqualsAndHashCode() {
        KpiB6AnalyticDataDTO dto1 = new KpiB6AnalyticDataDTO();
        dto1.setId(1L);

        KpiB6AnalyticDataDTO dto2 = new KpiB6AnalyticDataDTO();
        dto2.setId(1L);

        KpiB6AnalyticDataDTO dto3 = new KpiB6AnalyticDataDTO();
        dto3.setId(2L);

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
        assertNotEquals(dto1, dto3);
    }

    @Test
    void testToStringContainsFields() {
        KpiB6AnalyticDataDTO dto = new KpiB6AnalyticDataDTO();
        dto.setId(1L);
        dto.setEventId("evt1");
        String toString = dto.toString();
        assertTrue(toString.contains("id=1"));
        assertTrue(toString.contains("eventId='evt1'"));
    }

    @Test
    void testConstructorHandlesInvalidJsonGracefully() {
        KpiAnalyticDataDTO genericDto = new KpiAnalyticDataDTO();
        genericDto.setAnalyticData("invalid json");

        // Should not throw an exception
        KpiB6AnalyticDataDTO dto = new KpiB6AnalyticDataDTO(genericDto);

        assertNull(dto.getStationCode());
        assertNull(dto.getAnagStationId());
        assertNull(dto.getEventId());
        assertNull(dto.getEventType());
        assertNull(dto.getPaymentOption());
    }
}
