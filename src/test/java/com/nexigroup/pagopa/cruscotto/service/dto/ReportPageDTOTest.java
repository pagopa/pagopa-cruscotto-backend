package com.nexigroup.pagopa.cruscotto.service.dto;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ReportPageDTOTest {

    @Test
    void testReportPageDTOLombokMethods() {
        // Create Pageable instance using builder
        ReportPageDTO.Pageable pageable = ReportPageDTO.Pageable.builder()
            .pageNumber(1)
            .pageSize(20)
            .sort("asc")
            .build();

        // Verify builder values
        assertThat(pageable.getPageNumber()).isEqualTo(1);
        assertThat(pageable.getPageSize()).isEqualTo(20);
        assertThat(pageable.getSort()).isEqualTo("asc");

        // Create ReportPageDTO using builder
        ReportPageDTO reportPageDTO = ReportPageDTO.builder()
            .content(List.of(new ReportListItemDTO())) // assuming default constructor exists
            .pageable(pageable)
            .totalElements(100)
            .totalPages(5)
            .build();

        // Verify values
        assertThat(reportPageDTO.getContent()).hasSize(1);
        assertThat(reportPageDTO.getPageable()).isEqualTo(pageable);
        assertThat(reportPageDTO.getTotalElements()).isEqualTo(100);
        assertThat(reportPageDTO.getTotalPages()).isEqualTo(5);

        // Test setters
        ReportPageDTO anotherDTO = new ReportPageDTO();
        anotherDTO.setContent(List.of(new ReportListItemDTO(), new ReportListItemDTO()));
        anotherDTO.setPageable(pageable);
        anotherDTO.setTotalElements(200);
        anotherDTO.setTotalPages(10);

        assertThat(anotherDTO.getContent()).hasSize(2);
        assertThat(anotherDTO.getPageable()).isEqualTo(pageable);
        assertThat(anotherDTO.getTotalElements()).isEqualTo(200);
        assertThat(anotherDTO.getTotalPages()).isEqualTo(10);
    }

    @Test
    void testPageableEqualsAndHashCode() {
        ReportPageDTO.Pageable p1 = new ReportPageDTO.Pageable(1, 10, "desc");
        ReportPageDTO.Pageable p2 = new ReportPageDTO.Pageable(1, 10, "desc");
        ReportPageDTO.Pageable p3 = new ReportPageDTO.Pageable(2, 5, "asc");

        // Equals
        assertThat(p1).isEqualTo(p2);
        assertThat(p1).isNotEqualTo(p3);

        // HashCode
        assertThat(p1.hashCode()).isEqualTo(p2.hashCode());
        assertThat(p1.hashCode()).isNotEqualTo(p3.hashCode());
    }

    @Test
    void testReportPageDTOEqualsAndHashCode() {
        ReportPageDTO report1 = ReportPageDTO.builder()
            .content(List.of(new ReportListItemDTO()))
            .pageable(new ReportPageDTO.Pageable(0, 10, "asc"))
            .totalElements(1)
            .totalPages(1)
            .build();

        ReportPageDTO report2 = ReportPageDTO.builder()
            .content(List.of(new ReportListItemDTO()))
            .pageable(new ReportPageDTO.Pageable(0, 10, "asc"))
            .totalElements(1)
            .totalPages(1)
            .build();

        ReportPageDTO report3 = ReportPageDTO.builder()
            .content(List.of())
            .pageable(new ReportPageDTO.Pageable(1, 5, "desc"))
            .totalElements(0)
            .totalPages(0)
            .build();

        assertThat(report1).isEqualTo(report2);
        assertThat(report1).isNotEqualTo(report3);

        assertThat(report1.hashCode()).isEqualTo(report2.hashCode());
        assertThat(report1.hashCode()).isNotEqualTo(report3.hashCode());
    }

    @Test
    void testToString() {
        ReportPageDTO.Pageable pageable = new ReportPageDTO.Pageable(1, 10, "asc");
        ReportPageDTO reportPageDTO = ReportPageDTO.builder()
            .content(List.of(new ReportListItemDTO()))
            .pageable(pageable)
            .totalElements(50)
            .totalPages(5)
            .build();

        String toString = reportPageDTO.toString();
        assertThat(toString).contains("totalElements=50");
        assertThat(toString).contains("totalPages=5");
        assertThat(toString).contains("pageNumber=1");
    }
}
