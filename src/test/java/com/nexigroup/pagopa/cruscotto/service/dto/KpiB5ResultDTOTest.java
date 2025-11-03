package com.nexigroup.pagopa.cruscotto.service.dto;

import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class KpiB5ResultDTOTest {

    @Test
    void testGettersAndSetters() {
        KpiB5ResultDTO dto = new KpiB5ResultDTO();

        Long id = 1L;
        Long instanceId = 100L;
        Long instanceModuleId = 200L;
        LocalDate analysisDate = LocalDate.of(2024, 5, 20);
        BigDecimal eligibilityThreshold = new BigDecimal("0.95");
        BigDecimal tolerance = new BigDecimal("0.05");
        OutcomeStatus outcome = OutcomeStatus.OK;

        dto.setId(id);
        dto.setInstanceId(instanceId);
        dto.setInstanceModuleId(instanceModuleId);
        dto.setAnalysisDate(analysisDate);
        dto.setEligibilityThreshold(eligibilityThreshold);
        dto.setTolerance(tolerance);
        dto.setOutcome(outcome);

        assertThat(dto.getId()).isEqualTo(id);
        assertThat(dto.getInstanceId()).isEqualTo(instanceId);
        assertThat(dto.getInstanceModuleId()).isEqualTo(instanceModuleId);
        assertThat(dto.getAnalysisDate()).isEqualTo(analysisDate);
        assertThat(dto.getEligibilityThreshold()).isEqualTo(eligibilityThreshold);
        assertThat(dto.getTolerance()).isEqualTo(tolerance);
        assertThat(dto.getOutcome()).isEqualTo(outcome);
    }

    @Test
    void testEqualsAndHashCode_sameId_shouldBeEqual() {
        KpiB5ResultDTO dto1 = new KpiB5ResultDTO();
        dto1.setId(1L);

        KpiB5ResultDTO dto2 = new KpiB5ResultDTO();
        dto2.setId(1L);

        assertThat(dto1)
            .isEqualTo(dto2)
            .hasSameHashCodeAs(dto2);
    }

    @Test
    void testEquals_differentId_shouldNotBeEqual() {
        KpiB5ResultDTO dto1 = new KpiB5ResultDTO();
        dto1.setId(1L);

        KpiB5ResultDTO dto2 = new KpiB5ResultDTO();
        dto2.setId(2L);

        assertThat(dto1).isNotEqualTo(dto2);
    }

    @Test
    void testEquals_nullId_shouldNotBeEqual() {
        KpiB5ResultDTO dto1 = new KpiB5ResultDTO();
        KpiB5ResultDTO dto2 = new KpiB5ResultDTO();
        dto2.setId(1L);

        assertThat(dto1).isNotEqualTo(dto2);
    }

    @Test
    void testEquals_sameObject_shouldBeEqual() {
        KpiB5ResultDTO dto = new KpiB5ResultDTO();
        dto.setId(1L);

        assertThat(dto).isEqualTo(dto);
    }

    @Test
    void testEquals_differentClass_shouldNotBeEqual() {
        KpiB5ResultDTO dto = new KpiB5ResultDTO();
        dto.setId(1L);

        assertThat(dto).isNotEqualTo("string");
    }

    @Test
    void testToString_containsAllFields() {
        KpiB5ResultDTO dto = new KpiB5ResultDTO();
        dto.setId(1L);
        dto.setInstanceId(100L);
        dto.setInstanceModuleId(200L);
        dto.setAnalysisDate(LocalDate.of(2024, 5, 20));
        dto.setEligibilityThreshold(new BigDecimal("0.95"));
        dto.setTolerance(new BigDecimal("0.05"));
        dto.setOutcome(OutcomeStatus.OK);

        String result = dto.toString();

        assertThat(result)
            .contains("id=1")
            .contains("instanceId=100")
            .contains("instanceModuleId=200")
            .contains("analysisDate=2024-05-20")
            .contains("eligibilityThreshold=0.95")
            .contains("tolerance=0.05")
            .contains("outcome=OK");
    }
}
