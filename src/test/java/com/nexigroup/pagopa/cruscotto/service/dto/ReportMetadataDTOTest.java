package com.nexigroup.pagopa.cruscotto.service.dto;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import org.junit.jupiter.api.Test;

class ReportMetadataDTOTest {

    @Test
    void shouldCreateObjectUsingBuilder() {
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);

        ReportMetadataDTO.Configuration configuration =
            ReportMetadataDTO.Configuration.builder()
                .includeSummary(true)
                .includeKoKpisDetail(false)
                .includeAllKpisDetail(true)
                .includeDrilldownExcel(false)
                .build();

        ReportMetadataDTO dto =
            ReportMetadataDTO.builder()
                .id(1L)
                .instanceId(10L)
                .instanceName("Test Instance")
                .configuration(configuration)
                .status(ReportStatusEnum.COMPLETED)
                .language("EN")
                .startDate(LocalDate.of(2024, 1, 1))
                .endDate(LocalDate.of(2024, 1, 31))
                .requestedDate(now)
                .generationStartDate(now)
                .completionDate(now)
                .requestedBy("tester")
                .downloadUrl("http://example.com/report.zip")
                .fileSizeBytes(12345L)
                .fileName("report.zip")
                .packageContents(List.of("file1.csv", "file2.csv"))
                .errorMessage(null)
                .build();

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("Test Instance", dto.getInstanceName());
        assertEquals("EN", dto.getLanguage());
        assertEquals(2, dto.getPackageContents().size());
        assertTrue(dto.getConfiguration().getIncludeSummary());
    }

    @Test
    void shouldSupportNoArgsConstructorAndSetters() {
        ReportMetadataDTO dto = new ReportMetadataDTO();

        dto.setId(2L);
        dto.setInstanceName("Manual Instance");
        dto.setLanguage("IT");

        assertEquals(2L, dto.getId());
        assertEquals("Manual Instance", dto.getInstanceName());
        assertEquals("IT", dto.getLanguage());
    }

    @Test
    void equalsAndHashCodeShouldWorkCorrectly() {
        ReportMetadataDTO dto1 =
            ReportMetadataDTO.builder()
                .id(1L)
                .instanceName("Instance")
                .build();

        ReportMetadataDTO dto2 =
            ReportMetadataDTO.builder()
                .id(1L)
                .instanceName("Instance")
                .build();

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    void equalsShouldReturnFalseForDifferentObjects() {
        ReportMetadataDTO dto1 =
            ReportMetadataDTO.builder()
                .id(1L)
                .build();

        ReportMetadataDTO dto2 =
            ReportMetadataDTO.builder()
                .id(2L)
                .build();

        assertNotEquals(dto1, dto2);
    }

    @Test
    void toStringShouldNotBeNull() {
        ReportMetadataDTO dto =
            ReportMetadataDTO.builder()
                .id(1L)
                .instanceName("Test")
                .build();

        String toString = dto.toString();

        assertNotNull(toString);
        assertTrue(toString.contains("Test"));
    }

    @Test
    void configurationEqualsAndHashCodeShouldWork() {
        ReportMetadataDTO.Configuration c1 =
            ReportMetadataDTO.Configuration.builder()
                .includeSummary(true)
                .includeKoKpisDetail(false)
                .includeAllKpisDetail(true)
                .includeDrilldownExcel(false)
                .build();

        ReportMetadataDTO.Configuration c2 =
            ReportMetadataDTO.Configuration.builder()
                .includeSummary(true)
                .includeKoKpisDetail(false)
                .includeAllKpisDetail(true)
                .includeDrilldownExcel(false)
                .build();

        assertEquals(c1, c2);
        assertEquals(c1.hashCode(), c2.hashCode());
    }

    @Test
    void configurationSettersAndGettersShouldWork() {
        ReportMetadataDTO.Configuration configuration =
            new ReportMetadataDTO.Configuration();

        configuration.setIncludeSummary(true);
        configuration.setIncludeKoKpisDetail(true);
        configuration.setIncludeAllKpisDetail(false);
        configuration.setIncludeDrilldownExcel(true);

        assertTrue(configuration.getIncludeSummary());
        assertTrue(configuration.getIncludeKoKpisDetail());
        assertFalse(configuration.getIncludeAllKpisDetail());
        assertTrue(configuration.getIncludeDrilldownExcel());
    }
}
