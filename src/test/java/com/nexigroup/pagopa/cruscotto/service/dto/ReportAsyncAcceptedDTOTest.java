package com.nexigroup.pagopa.cruscotto.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.OffsetDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

class ReportAsyncAcceptedDTOTest {

    @Test
    void shouldCreateWithNoArgsConstructor() {
        ReportAsyncAcceptedDTO dto = new ReportAsyncAcceptedDTO();

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isNull();
        assertThat(dto.getReportType()).isNull();
        assertThat(dto.getStatus()).isNull();
    }

    @Test
    void shouldCreateWithAllArgsConstructor() {
        OffsetDateTime now = OffsetDateTime.now();
        List<String> contents = List.of("file1.csv", "file2.csv");

        ReportAsyncAcceptedDTO dto = new ReportAsyncAcceptedDTO(
            1L,
            null,
            null,
            now,
            now.plusMinutes(5),
            "http://download",
            1234L,
            "report.zip",
            contents
        );

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getRequestedDate()).isEqualTo(now);
        assertThat(dto.getFileName()).isEqualTo("report.zip");
        assertThat(dto.getPackageContents()).containsExactly("file1.csv", "file2.csv");
    }

    @Test
    void shouldSetAndGetFields() {
        ReportAsyncAcceptedDTO dto = new ReportAsyncAcceptedDTO();
        OffsetDateTime now = OffsetDateTime.now();

        dto.setId(10L);
        dto.setRequestedDate(now);
        dto.setDownloadUrl("url");
        dto.setFileSizeBytes(999L);

        assertThat(dto.getId()).isEqualTo(10L);
        assertThat(dto.getRequestedDate()).isEqualTo(now);
        assertThat(dto.getDownloadUrl()).isEqualTo("url");
        assertThat(dto.getFileSizeBytes()).isEqualTo(999L);
    }

    @Test
    void shouldBuildUsingBuilder() {
        OffsetDateTime now = OffsetDateTime.now();

        ReportAsyncAcceptedDTO dto = ReportAsyncAcceptedDTO.builder()
            .id(5L)
            .requestedDate(now)
            .fileName("report.pdf")
            .packageContents(List.of("a", "b"))
            .build();

        assertThat(dto.getId()).isEqualTo(5L);
        assertThat(dto.getFileName()).isEqualTo("report.pdf");
        assertThat(dto.getPackageContents()).hasSize(2);
    }

    @Test
    void shouldBeEqualAndHaveSameHashCode() {
        OffsetDateTime now = OffsetDateTime.now();

        ReportAsyncAcceptedDTO dto1 = ReportAsyncAcceptedDTO.builder()
            .id(1L)
            .requestedDate(now)
            .fileName("file")
            .build();

        ReportAsyncAcceptedDTO dto2 = ReportAsyncAcceptedDTO.builder()
            .id(1L)
            .requestedDate(now)
            .fileName("file")
            .build();

        assertThat(dto1)
            .isEqualTo(dto2)
            .hasSameHashCodeAs(dto2);
    }

    @Test
    void shouldHaveMeaningfulToString() {
        ReportAsyncAcceptedDTO dto = ReportAsyncAcceptedDTO.builder()
            .id(99L)
            .fileName("test.txt")
            .build();

        String toString = dto.toString();

        assertThat(toString).contains("ReportAsyncAcceptedDTO");
        assertThat(toString).contains("id=99");
        assertThat(toString).contains("fileName=test.txt");
    }
}
