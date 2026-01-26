package com.nexigroup.pagopa.cruscotto.service.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ReportGenerationRequestDTOTest {

    @Test
    void testAllArgsConstructorAndGetters() {
        List<Long> instanceIds = List.of(1L, 2L, 3L);
        ReportTypeEnum reportType = ReportTypeEnum.ANALYTICS; // replace with actual enum value
        String variant = "VAR1";
        String language = "EN";
        LocalDate startDate = LocalDate.of(2026, 1, 1);
        LocalDate endDate = LocalDate.of(2026, 1, 31);
        Map<String, Object> parameters = Map.of("param1", "value1");

        ReportGenerationRequestDTO dto = new ReportGenerationRequestDTO(
            instanceIds,
            reportType,
            variant,
            language,
            startDate,
            endDate,
            parameters
        );

        assertEquals(instanceIds, dto.getInstanceIds());
        assertEquals(reportType, dto.getReportType());
        assertEquals(variant, dto.getVariant());
        assertEquals(language, dto.getLanguage());
        assertEquals(startDate, dto.getStartDate());
        assertEquals(endDate, dto.getEndDate());
        assertEquals(parameters, dto.getParameters());
    }

    @Test
    void testNoArgsConstructorAndSetters() {
        ReportGenerationRequestDTO dto = new ReportGenerationRequestDTO();

        dto.setInstanceIds(List.of(4L, 5L));
        dto.setReportType(ReportTypeEnum.ANALYTICS); // replace with actual enum
        dto.setVariant("VAR2");
        dto.setLanguage("IT");
        dto.setStartDate(LocalDate.of(2026, 2, 1));
        dto.setEndDate(LocalDate.of(2026, 2, 28));
        dto.setParameters(Map.of("param2", "value2"));

        assertEquals(List.of(4L, 5L), dto.getInstanceIds());
        assertEquals(ReportTypeEnum.ANALYTICS, dto.getReportType());
        assertEquals("VAR2", dto.getVariant());
        assertEquals("IT", dto.getLanguage());
        assertEquals(LocalDate.of(2026, 2, 1), dto.getStartDate());
        assertEquals(LocalDate.of(2026, 2, 28), dto.getEndDate());
        assertEquals(Map.of("param2", "value2"), dto.getParameters());
    }

    @Test
    void testBuilder() {
        ReportGenerationRequestDTO dto = ReportGenerationRequestDTO.builder()
            .instanceIds(List.of(7L, 8L))
            .reportType(ReportTypeEnum.ANALYTICS) // replace with actual enum
            .variant("VAR3")
            .language("FR")
            .startDate(LocalDate.of(2026, 3, 1))
            .endDate(LocalDate.of(2026, 3, 31))
            .parameters(Map.of("param3", "value3"))
            .build();

        assertEquals(List.of(7L, 8L), dto.getInstanceIds());
        assertEquals(ReportTypeEnum.ANALYTICS, dto.getReportType());
        assertEquals("VAR3", dto.getVariant());
        assertEquals("FR", dto.getLanguage());
        assertEquals(LocalDate.of(2026, 3, 1), dto.getStartDate());
        assertEquals(LocalDate.of(2026, 3, 31), dto.getEndDate());
        assertEquals(Map.of("param3", "value3"), dto.getParameters());
    }

    @Test
    void testEqualsAndHashCode() {
        ReportGenerationRequestDTO dto1 = ReportGenerationRequestDTO.builder()
            .instanceIds(List.of(1L))
            .reportType(ReportTypeEnum.ANALYTICS)
            .build();

        ReportGenerationRequestDTO dto2 = ReportGenerationRequestDTO.builder()
            .instanceIds(List.of(1L))
            .reportType(ReportTypeEnum.ANALYTICS)
            .build();

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    void testToString() {
        ReportGenerationRequestDTO dto = new ReportGenerationRequestDTO();
        String str = dto.toString();
        assertNotNull(str);
        assertTrue(str.contains("ReportGenerationRequestDTO"));
    }
}
