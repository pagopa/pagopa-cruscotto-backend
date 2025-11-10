package com.nexigroup.pagopa.cruscotto.service.dto;

import com.nexigroup.pagopa.cruscotto.domain.enumeration.EvaluationType;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class KpiB3DetailResultDTOTest {

    @Test
    void testGettersAndSetters() {
        KpiB3DetailResultDTO dto = new KpiB3DetailResultDTO();

        Long id = 1L;
        Long instanceId = 10L;
        Long instanceModuleId = 20L;
        Long anagStationId = 30L;
        Long kpiB3ResultId = 40L;
        LocalDate analysisDate = LocalDate.of(2025, 10, 31);
        EvaluationType evaluationType = EvaluationType.MESE;
        LocalDate evaluationStartDate = LocalDate.of(2025, 10, 1);
        LocalDate evaluationEndDate = LocalDate.of(2025, 10, 15);
        Integer totalStandIn = 5;
        OutcomeStatus outcome = OutcomeStatus.OK;

        dto.setId(id);
        dto.setInstanceId(instanceId);
        dto.setInstanceModuleId(instanceModuleId);
        dto.setAnagStationId(anagStationId);
        dto.setKpiB3ResultId(kpiB3ResultId);
        dto.setAnalysisDate(analysisDate);
        dto.setEvaluationType(evaluationType);
        dto.setEvaluationStartDate(evaluationStartDate);
        dto.setEvaluationEndDate(evaluationEndDate);
        dto.setTotalStandIn(totalStandIn);
        dto.setOutcome(outcome);

        assertThat(dto.getId()).isEqualTo(id);
        assertThat(dto.getInstanceId()).isEqualTo(instanceId);
        assertThat(dto.getInstanceModuleId()).isEqualTo(instanceModuleId);
        assertThat(dto.getAnagStationId()).isEqualTo(anagStationId);
        assertThat(dto.getKpiB3ResultId()).isEqualTo(kpiB3ResultId);
        assertThat(dto.getAnalysisDate()).isEqualTo(analysisDate);
        assertThat(dto.getEvaluationType()).isEqualTo(evaluationType);
        assertThat(dto.getEvaluationStartDate()).isEqualTo(evaluationStartDate);
        assertThat(dto.getEvaluationEndDate()).isEqualTo(evaluationEndDate);
        assertThat(dto.getTotalStandIn()).isEqualTo(totalStandIn);
        assertThat(dto.getOutcome()).isEqualTo(outcome);
    }

    @Test
    void testEqualsAndHashCode() {
        KpiB3DetailResultDTO dto1 = new KpiB3DetailResultDTO();
        dto1.setId(1L);

        KpiB3DetailResultDTO dto2 = new KpiB3DetailResultDTO();
        dto2.setId(1L);

        KpiB3DetailResultDTO dto3 = new KpiB3DetailResultDTO();
        dto3.setId(2L);

        assertThat(dto1).isEqualTo(dto2);
        assertThat(dto1).hasSameHashCodeAs(dto2);
        assertThat(dto1).isNotEqualTo(dto3);
    }

    @Test
    void testToStringContainsFields() {
        KpiB3DetailResultDTO dto = new KpiB3DetailResultDTO();
        dto.setId(99L);
        dto.setOutcome(OutcomeStatus.KO);
        String str = dto.toString();

        assertThat(str).contains("id=99");
        assertThat(str).contains("outcome=KO");
    }
}
