package com.nexigroup.pagopa.cruscotto.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.nexigroup.pagopa.cruscotto.domain.enumeration.ModuleCode;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import java.time.Instant;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class KpiResultDTOTest {

    @Test
    void testGettersAndSetters() {
        KpiResultDTO dto = new KpiResultDTO();

        Long id = 1L;
        ModuleCode moduleCode = ModuleCode.B2; // replace with a real enum constant
        Long instanceId = 10L;
        Long instanceModuleId = 20L;
        LocalDate analysisDate = LocalDate.now();
        OutcomeStatus outcome = OutcomeStatus.OK; // replace with your actual enum
        String additionalData = "{\"key\":\"value\"}";
        String createdBy = "admin";
        Instant createdDate = Instant.now();
        String lastModifiedBy = "editor";
        Instant lastModifiedDate = Instant.now();

        dto.setId(id);
        dto.setModuleCode(moduleCode);
        dto.setInstanceId(instanceId);
        dto.setInstanceModuleId(instanceModuleId);
        dto.setAnalysisDate(analysisDate);
        dto.setOutcome(outcome);
        dto.setAdditionalData(additionalData);
        dto.setCreatedBy(createdBy);
        dto.setCreatedDate(createdDate);
        dto.setLastModifiedBy(lastModifiedBy);
        dto.setLastModifiedDate(lastModifiedDate);

        assertThat(dto.getId()).isEqualTo(id);
        assertThat(dto.getModuleCode()).isEqualTo(moduleCode);
        assertThat(dto.getInstanceId()).isEqualTo(instanceId);
        assertThat(dto.getInstanceModuleId()).isEqualTo(instanceModuleId);
        assertThat(dto.getAnalysisDate()).isEqualTo(analysisDate);
        assertThat(dto.getOutcome()).isEqualTo(outcome);
        assertThat(dto.getAdditionalData()).isEqualTo(additionalData);
        assertThat(dto.getCreatedBy()).isEqualTo(createdBy);
        assertThat(dto.getCreatedDate()).isEqualTo(createdDate);
        assertThat(dto.getLastModifiedBy()).isEqualTo(lastModifiedBy);
        assertThat(dto.getLastModifiedDate()).isEqualTo(lastModifiedDate);
    }

    @Test
    void testEqualsAndHashCode() {
        KpiResultDTO dto1 = new KpiResultDTO();
        dto1.setId(1L);
        dto1.setModuleCode(ModuleCode.B2);

        KpiResultDTO dto2 = new KpiResultDTO();
        dto2.setId(1L);
        dto2.setModuleCode(ModuleCode.B2);

        KpiResultDTO dto3 = new KpiResultDTO();
        dto3.setId(2L);
        dto3.setModuleCode(ModuleCode.B3); // or any other enum constant

        assertThat(dto1).isEqualTo(dto2);
        assertThat(dto1).hasSameHashCodeAs(dto2);
        assertThat(dto1).isNotEqualTo(dto3);
    }

    @Test
    void testToStringContainsKeyFields() {
        KpiResultDTO dto = new KpiResultDTO();
        dto.setId(42L);
        dto.setModuleCode(ModuleCode.B2);
        dto.setOutcome(OutcomeStatus.OK);

        String toString = dto.toString();
        assertThat(toString).contains("42");
        assertThat(toString).contains("B2");
        assertThat(toString).contains("OK");
    }

    @Test
    void testImplementsKpiResultInterface() {
        KpiResultDTO dto = new KpiResultDTO();
        assertThat(dto).isInstanceOf(com.nexigroup.pagopa.cruscotto.kpi.framework.KpiResult.class);
    }
}
