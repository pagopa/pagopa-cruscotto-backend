package com.nexigroup.pagopa.cruscotto.service.dto;

import com.nexigroup.pagopa.cruscotto.domain.enumeration.EvaluationType;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class KpiB4DetailResultDTOTest {

    @Test
    void testGettersAndSetters() {
        KpiB4DetailResultDTO dto = new KpiB4DetailResultDTO();

        Long id = 1L;
        Long instanceId = 2L;
        Long instanceModuleId = 3L;
        LocalDate analysisDate = LocalDate.now();
        EvaluationType evaluationType = EvaluationType.MESE;
        LocalDate evalStart = LocalDate.now().minusDays(5);
        LocalDate evalEnd = LocalDate.now();
        Long sumTotGpd = 100L;
        Long sumTotCp = 50L;
        BigDecimal perApiCp = new BigDecimal("33.3");
        OutcomeStatus outcome = OutcomeStatus.OK;
        Long kpiB4ResultId = 10L;
        String stationCode = "ST001";
        String stationName = "Station Alpha";

        dto.setId(id);
        dto.setInstanceId(instanceId);
        dto.setInstanceModuleId(instanceModuleId);
        dto.setAnalysisDate(analysisDate);
        dto.setEvaluationType(evaluationType);
        dto.setEvaluationStartDate(evalStart);
        dto.setEvaluationEndDate(evalEnd);
        dto.setSumTotGpd(sumTotGpd);
        dto.setSumTotCp(sumTotCp);
        dto.setPerApiCp(perApiCp);
        dto.setOutcome(outcome);
        dto.setKpiB4ResultId(kpiB4ResultId);
        dto.setStationCode(stationCode);
        dto.setStationName(stationName);

        assertThat(dto.getId()).isEqualTo(id);
        assertThat(dto.getInstanceId()).isEqualTo(instanceId);
        assertThat(dto.getInstanceModuleId()).isEqualTo(instanceModuleId);
        assertThat(dto.getAnalysisDate()).isEqualTo(analysisDate);
        assertThat(dto.getEvaluationType()).isEqualTo(evaluationType);
        assertThat(dto.getEvaluationStartDate()).isEqualTo(evalStart);
        assertThat(dto.getEvaluationEndDate()).isEqualTo(evalEnd);
        assertThat(dto.getSumTotGpd()).isEqualTo(sumTotGpd);
        assertThat(dto.getSumTotCp()).isEqualTo(sumTotCp);
        assertThat(dto.getPerApiCp()).isEqualTo(perApiCp);
        assertThat(dto.getOutcome()).isEqualTo(outcome);
        assertThat(dto.getKpiB4ResultId()).isEqualTo(kpiB4ResultId);
        assertThat(dto.getStationCode()).isEqualTo(stationCode);
        assertThat(dto.getStationName()).isEqualTo(stationName);
    }

    @Test
    void testEqualsAndHashCode() {
        KpiB4DetailResultDTO dto1 = new KpiB4DetailResultDTO();
        dto1.setId(1L);

        KpiB4DetailResultDTO dto2 = new KpiB4DetailResultDTO();
        dto2.setId(1L);

        KpiB4DetailResultDTO dto3 = new KpiB4DetailResultDTO();
        dto3.setId(2L);

        KpiB4DetailResultDTO dtoNullId = new KpiB4DetailResultDTO();

        assertThat(dto1).isEqualTo(dto2);
        assertThat(dto1).hasSameHashCodeAs(dto2);

        assertThat(dto1).isNotEqualTo(dto3);
        assertThat(dto1).isNotEqualTo(dtoNullId);
        assertThat(dto1.equals(null)).isFalse();
        assertThat(dto1.equals("not a dto")).isFalse();
    }

    @Test
    void testToStringContainsFields() {
        KpiB4DetailResultDTO dto = new KpiB4DetailResultDTO();
        dto.setId(1L);
        dto.setInstanceId(2L);
        dto.setInstanceModuleId(3L);
        dto.setStationCode("CODE");
        dto.setStationName("NAME");
        dto.setEvaluationType(EvaluationType.MESE);

        String result = dto.toString();

        assertThat(result)
            .contains("id=1")
            .contains("instanceId=2")
            .contains("instanceModuleId=3")
            .contains("stationCode='CODE'")
            .contains("stationName='NAME'")
            .contains("evaluationType='MESE'");
    }
}
