package com.nexigroup.pagopa.cruscotto.service.dto;

import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class ReportListItemDTOTest {

    @Test
    void testAllArgsConstructorAndGetters() {
        OffsetDateTime now = OffsetDateTime.now();
        ReportListItemDTO dto = new ReportListItemDTO(
            1L,
            "Instance1",
            ReportTypeEnum.ANALYTICS,  // replace with actual enum values
            ReportStatusEnum.PENDING,  // replace with actual enum values
            now,
            12345L,
            "report.csv"
        );

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getInstanceName()).isEqualTo("Instance1");
        assertThat(dto.getReportType()).isEqualTo(ReportTypeEnum.ANALYTICS);
        assertThat(dto.getStatus()).isEqualTo(ReportStatusEnum.PENDING);
        assertThat(dto.getRequestedDate()).isEqualTo(now);
        assertThat(dto.getFileSizeBytes()).isEqualTo(12345L);
        assertThat(dto.getFileName()).isEqualTo("report.csv");
    }

    @Test
    void testNoArgsConstructorAndSetters() {
        OffsetDateTime now = OffsetDateTime.now();
        ReportListItemDTO dto = new ReportListItemDTO();
        dto.setId(2L);
        dto.setInstanceName("Instance2");
        dto.setReportType(ReportTypeEnum.DETAIL); // replace with actual enum values
        dto.setStatus(ReportStatusEnum.COMPLETED);    // replace with actual enum values
        dto.setRequestedDate(now);
        dto.setFileSizeBytes(54321L);
        dto.setFileName("report2.csv");

        assertThat(dto.getId()).isEqualTo(2L);
        assertThat(dto.getInstanceName()).isEqualTo("Instance2");
        assertThat(dto.getReportType()).isEqualTo(ReportTypeEnum.DETAIL);
        assertThat(dto.getStatus()).isEqualTo(ReportStatusEnum.COMPLETED);
        assertThat(dto.getRequestedDate()).isEqualTo(now);
        assertThat(dto.getFileSizeBytes()).isEqualTo(54321L);
        assertThat(dto.getFileName()).isEqualTo("report2.csv");
    }

    @Test
    void testBuilder() {
        OffsetDateTime now = OffsetDateTime.now();
        ReportListItemDTO dto = ReportListItemDTO.builder()
            .id(3L)
            .instanceName("Instance3")
            .reportType(ReportTypeEnum.ANALYTICS)
            .status(ReportStatusEnum.PENDING)
            .requestedDate(now)
            .fileSizeBytes(1000L)
            .fileName("report3.csv")
            .build();

        assertThat(dto.getId()).isEqualTo(3L);
        assertThat(dto.getInstanceName()).isEqualTo("Instance3");
        assertThat(dto.getReportType()).isEqualTo(ReportTypeEnum.ANALYTICS);
        assertThat(dto.getStatus()).isEqualTo(ReportStatusEnum.PENDING);
        assertThat(dto.getRequestedDate()).isEqualTo(now);
        assertThat(dto.getFileSizeBytes()).isEqualTo(1000L);
        assertThat(dto.getFileName()).isEqualTo("report3.csv");
    }

    @Test
    void testEqualsAndHashCode() {
        OffsetDateTime now = OffsetDateTime.now();
        ReportListItemDTO dto1 = ReportListItemDTO.builder()
            .id(1L)
            .instanceName("Instance")
            .reportType(ReportTypeEnum.ANALYTICS)
            .status(ReportStatusEnum.PENDING)
            .requestedDate(now)
            .fileSizeBytes(100L)
            .fileName("report.csv")
            .build();

        ReportListItemDTO dto2 = ReportListItemDTO.builder()
            .id(1L)
            .instanceName("Instance")
            .reportType(ReportTypeEnum.ANALYTICS)
            .status(ReportStatusEnum.PENDING)
            .requestedDate(now)
            .fileSizeBytes(100L)
            .fileName("report.csv")
            .build();

        assertThat(dto1).isEqualTo(dto2);
        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
    }

    @Test
    void testToString() {
        ReportListItemDTO dto = new ReportListItemDTO();
        String str = dto.toString();
        assertThat(str).contains("id", "instanceName", "reportType", "status", "requestedDate", "fileSizeBytes", "fileName");
    }
}
