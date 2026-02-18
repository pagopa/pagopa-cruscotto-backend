package com.nexigroup.pagopa.cruscotto.service.dto;

import com.nexigroup.pagopa.cruscotto.domain.KpiC2AnalyticData;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class KpiC2AnalyticDrillDownDTOTest {

    @Test
    void shouldSetAndGetAllFields() {
        KpiC2AnalyticDrillDownDTO dto = new KpiC2AnalyticDrillDownDTO();

        LocalDate analysisDate = LocalDate.of(2024, 1, 10);
        LocalDate evaluationDate = LocalDate.of(2024, 1, 15);
        BigDecimal percentNotification = new BigDecimal("75.50");
        KpiC2AnalyticData analyticData = new KpiC2AnalyticData();

        dto.setId(1L);
        dto.setInstanceId(10L);
        dto.setInstanceModuleId(20L);
        dto.setAnalysisDate(analysisDate);
        dto.setEvaluationDate(evaluationDate);
        dto.setInstitutionCf("ABCDEF12G34H567I");
        dto.setNumPayment(100L);
        dto.setNumNotification(75L);
        dto.setPercentNotification(percentNotification);
        dto.setKpiC2AnalyticData(analyticData);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getInstanceId()).isEqualTo(10L);
        assertThat(dto.getInstanceModuleId()).isEqualTo(20L);
        assertThat(dto.getAnalysisDate()).isEqualTo(analysisDate);
        assertThat(dto.getEvaluationDate()).isEqualTo(evaluationDate);
        assertThat(dto.getInstitutionCf()).isEqualTo("ABCDEF12G34H567I");
        assertThat(dto.getNumPayment()).isEqualTo(100L);
        assertThat(dto.getNumNotification()).isEqualTo(75L);
        assertThat(dto.getPercentNotification()).isEqualTo(percentNotification);
        assertThat(dto.getKpiC2AnalyticData()).isEqualTo(analyticData);
    }

    @Test
    void shouldHaveConstantHashCode() {
        KpiC2AnalyticDrillDownDTO dto1 = new KpiC2AnalyticDrillDownDTO();
        KpiC2AnalyticDrillDownDTO dto2 = new KpiC2AnalyticDrillDownDTO();

        assertThat(dto1.hashCode()).isEqualTo(31);
        assertThat(dto2.hashCode()).isEqualTo(31);
    }

    @Test
    void shouldContainAllFieldsInToString() {
        KpiC2AnalyticDrillDownDTO dto = new KpiC2AnalyticDrillDownDTO();

        dto.setId(1L);
        dto.setInstanceId(2L);
        dto.setInstanceModuleId(3L);
        dto.setAnalysisDate(LocalDate.of(2024, 2, 1));
        dto.setEvaluationDate(LocalDate.of(2024, 2, 2));
        dto.setInstitutionCf("CF123");
        dto.setNumPayment(50L);
        dto.setNumNotification(25L);
        dto.setPercentNotification(BigDecimal.TEN);

        String result = dto.toString();

        assertThat(result)
            .contains("id=1")
            .contains("instanceId=2")
            .contains("instanceModuleId=3")
            .contains("analysisDate=2024-02-01")
            .contains("evaluationDate=2024-02-02")
            .contains("institutionCf='CF123'")
            .contains("numPayment=50")
            .contains("numNotification=25")
            .contains("percentNotification=10");
    }
}
