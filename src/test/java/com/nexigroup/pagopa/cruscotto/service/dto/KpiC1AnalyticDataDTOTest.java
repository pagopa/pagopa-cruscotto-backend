package com.nexigroup.pagopa.cruscotto.service.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class KpiC1AnalyticDataDTOTest {

    @Test
    void testAllArgsConstructorAndGetters() {
        LocalDate date1 = LocalDate.of(2024, 1, 1);
        LocalDate date2 = LocalDate.of(2024, 1, 2);

        KpiC1AnalyticDataDTO dto = new KpiC1AnalyticDataDTO(
            1L,
            10L,
            20L,
            30L,
            date1,
            date2,
            5,
            100L,
            200L
        );

        assertEquals(1L, dto.getId());
        assertEquals(10L, dto.getInstanceId());
        assertEquals(20L, dto.getInstanceModuleId());
        assertEquals(30L, dto.getKpiC1DetailResultId());
        assertEquals(date1, dto.getAnalysisDate());
        assertEquals(date2, dto.getDataDate());
        assertEquals(5, dto.getInstitutionCount());
        assertEquals(100L, dto.getPositionsCount());
        assertEquals(200L, dto.getMessagesCount());
    }

    @Test
    void testNoArgsConstructorAndSetters() {
        KpiC1AnalyticDataDTO dto = new KpiC1AnalyticDataDTO();
        LocalDate date = LocalDate.now();

        dto.setId(1L);
        dto.setInstanceId(2L);
        dto.setInstanceModuleId(3L);
        dto.setKpiC1DetailResultId(4L);
        dto.setAnalysisDate(date);
        dto.setDataDate(date);
        dto.setInstitutionCount(10);
        dto.setPositionsCount(20L);
        dto.setMessagesCount(30L);

        assertEquals(1L, dto.getId());
        assertEquals(2L, dto.getInstanceId());
        assertEquals(3L, dto.getInstanceModuleId());
        assertEquals(4L, dto.getKpiC1DetailResultId());
        assertEquals(date, dto.getAnalysisDate());
        assertEquals(date, dto.getDataDate());
        assertEquals(10, dto.getInstitutionCount());
        assertEquals(20L, dto.getPositionsCount());
        assertEquals(30L, dto.getMessagesCount());
    }

    @Test
    void testEqualsAndHashCode() {
        LocalDate date = LocalDate.of(2024, 1, 1);

        KpiC1AnalyticDataDTO dto1 = new KpiC1AnalyticDataDTO(
            1L, 10L, 20L, 30L, date, date, 5, 100L, 200L);

        KpiC1AnalyticDataDTO dto2 = new KpiC1AnalyticDataDTO(
            1L, 10L, 20L, 30L, date, date, 5, 100L, 200L);

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    void testToStringNotNull() {
        KpiC1AnalyticDataDTO dto = new KpiC1AnalyticDataDTO();
        assertNotNull(dto.toString());
    }
}
