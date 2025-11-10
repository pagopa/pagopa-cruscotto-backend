package com.nexigroup.pagopa.cruscotto.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;

import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class KpiB4AnalyticDataDTOTest {

    private KpiB4AnalyticDataDTO dto;

    @BeforeEach
    void setUp() {
        dto = new KpiB4AnalyticDataDTO();
        dto.setId(1L);
        dto.setInstanceId(10L);
        dto.setKpiB4DetailResultId(100L);
        dto.setAnalysisDate(LocalDate.of(2025, 1, 15));
        dto.setEvaluationDate(LocalDate.of(2025, 1, 16));
        dto.setNumRequestGpd(200L);
        dto.setNumRequestCp(300L);
        dto.setAnalysisDatePeriod("2025-W03");
    }

    @Test
    void testGettersAndSetters() {
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getInstanceId()).isEqualTo(10L);
        assertThat(dto.getKpiB4DetailResultId()).isEqualTo(100L);
        assertThat(dto.getAnalysisDate()).isEqualTo(LocalDate.of(2025, 1, 15));
        assertThat(dto.getEvaluationDate()).isEqualTo(LocalDate.of(2025, 1, 16));
        assertThat(dto.getNumRequestGpd()).isEqualTo(200L);
        assertThat(dto.getNumRequestCp()).isEqualTo(300L);
        assertThat(dto.getAnalysisDatePeriod()).isEqualTo("2025-W03");
    }

    @Test
    void testEqualsAndHashCode() {
        KpiB4AnalyticDataDTO dto2 = new KpiB4AnalyticDataDTO();
        dto2.setId(1L);

        KpiB4AnalyticDataDTO dto3 = new KpiB4AnalyticDataDTO();
        dto3.setId(2L);

        assertThat(dto).isEqualTo(dto2);
        assertThat(dto.hashCode()).isEqualTo(dto2.hashCode());
        assertThat(dto).isNotEqualTo(dto3);
        assertThat(dto).isNotEqualTo(null);
        assertThat(dto).isNotEqualTo(new Object());
    }

    @Test
    void testEqualsWithNullId() {
        KpiB4AnalyticDataDTO dto1 = new KpiB4AnalyticDataDTO();
        KpiB4AnalyticDataDTO dto2 = new KpiB4AnalyticDataDTO();

        assertThat(dto1).isNotEqualTo(dto2);
    }

    @Test
    void testToStringContainsAllFields() {
        String result = dto.toString();
        assertThat(result)
            .contains("id=1")
            .contains("instanceId=10")
            .contains("kpiB4DetailResultId=100")
            .contains("analysisDate='2025-01-15'")
            .contains("evaluationDate='2025-01-16'")
            .contains("numRequestGpd=200")
            .contains("numRequestCp=300")
            .contains("analysisDatePeriod='2025-W03'");
    }

    @Test
    void testJsonSerialization() throws Exception {
        ObjectMapper mapper = new ObjectMapper()
            .findAndRegisterModules()
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        String json = mapper.writeValueAsString(dto);

        assertThat(json).contains("\"dataDate\":\"2025-01-16\"");
        assertThat(json).contains("\"totalGPD\":200");
        assertThat(json).contains("\"totalCP\":300");
    }

}
