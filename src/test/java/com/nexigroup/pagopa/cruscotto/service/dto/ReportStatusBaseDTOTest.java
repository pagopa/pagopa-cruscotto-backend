package com.nexigroup.pagopa.cruscotto.service.dto;

import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class ReportStatusBaseDTOTest {

    @Test
    void testNoArgsConstructorAndSetters() {
        ReportStatusBaseDTO dto = new ReportStatusBaseDTO();
        dto.setId(1L);
        dto.setStatus(ReportStatusEnum.COMPLETED);
        dto.setProgress(50);
        dto.setStatusMessage("In progress");
        dto.setEstimatedTimeRemaining(120);
        dto.setRetryCount(2);
        OffsetDateTime now = OffsetDateTime.now();
        dto.setLastRetryDate(now);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getStatus()).isEqualTo(ReportStatusEnum.COMPLETED);
        assertThat(dto.getProgress()).isEqualTo(50);
        assertThat(dto.getStatusMessage()).isEqualTo("In progress");
        assertThat(dto.getEstimatedTimeRemaining()).isEqualTo(120);
        assertThat(dto.getRetryCount()).isEqualTo(2);
        assertThat(dto.getLastRetryDate()).isEqualTo(now);
    }

    @Test
    void testAllArgsConstructor() {
        OffsetDateTime now = OffsetDateTime.now();
        ReportStatusBaseDTO dto = new ReportStatusBaseDTO(
            1L,
            ReportStatusEnum.FAILED,
            75,
            "Failed during processing",
            0,
            1,
            now
        );

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getStatus()).isEqualTo(ReportStatusEnum.FAILED);
        assertThat(dto.getProgress()).isEqualTo(75);
        assertThat(dto.getStatusMessage()).isEqualTo("Failed during processing");
        assertThat(dto.getEstimatedTimeRemaining()).isEqualTo(0);
        assertThat(dto.getRetryCount()).isEqualTo(1);
        assertThat(dto.getLastRetryDate()).isEqualTo(now);
    }

    @Test
    void testBuilder() {
        OffsetDateTime now = OffsetDateTime.now();
        ReportStatusBaseDTO dto = ReportStatusBaseDTO.builder()
            .id(2L)
            .status(ReportStatusEnum.IN_PROGRESS)
            .progress(30)
            .statusMessage("Still running")
            .estimatedTimeRemaining(60)
            .retryCount(0)
            .lastRetryDate(now)
            .build();

        assertThat(dto.getId()).isEqualTo(2L);
        assertThat(dto.getStatus()).isEqualTo(ReportStatusEnum.IN_PROGRESS);
        assertThat(dto.getProgress()).isEqualTo(30);
        assertThat(dto.getStatusMessage()).isEqualTo("Still running");
        assertThat(dto.getEstimatedTimeRemaining()).isEqualTo(60);
        assertThat(dto.getRetryCount()).isEqualTo(0);
        assertThat(dto.getLastRetryDate()).isEqualTo(now);
    }

    @Test
    void testEqualsAndHashCode() {
        OffsetDateTime now = OffsetDateTime.now();
        ReportStatusBaseDTO dto1 = new ReportStatusBaseDTO(1L, ReportStatusEnum.COMPLETED, 100, "Done", 0, 0, now);
        ReportStatusBaseDTO dto2 = new ReportStatusBaseDTO(1L, ReportStatusEnum.COMPLETED, 100, "Done", 0, 0, now);
        ReportStatusBaseDTO dto3 = new ReportStatusBaseDTO(2L, ReportStatusEnum.FAILED, 50, "Error", 30, 1, now);

        assertThat(dto1).isEqualTo(dto2);
        assertThat(dto1).isNotEqualTo(dto3);
        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
        assertThat(dto1.hashCode()).isNotEqualTo(dto3.hashCode());
    }

    @Test
    void testToString() {
        ReportStatusBaseDTO dto = new ReportStatusBaseDTO();
        String toString = dto.toString();
        assertThat(toString).contains("id=", "status=", "progress=", "statusMessage=", "estimatedTimeRemaining=", "retryCount=", "lastRetryDate=");
    }
}
