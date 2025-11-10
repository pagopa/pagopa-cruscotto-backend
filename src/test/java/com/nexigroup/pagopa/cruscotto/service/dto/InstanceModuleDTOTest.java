package com.nexigroup.pagopa.cruscotto.service.dto;

import com.nexigroup.pagopa.cruscotto.domain.enumeration.AnalysisOutcome;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.AnalysisType;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.ModuleStatus;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class InstanceModuleDTOTest {

    @Test
    void testGettersAndSetters() {
        InstanceModuleDTO dto = new InstanceModuleDTO();

        dto.setId(1L);
        dto.setInstanceId(2L);
        dto.setModuleId(3L);
        dto.setModuleCode("MOD001");
        dto.setAnalysisType(AnalysisType.AUTOMATICA);
        dto.setAllowManualOutcome(true);
        dto.setAutomaticOutcome(AnalysisOutcome.OK);
        Instant now = Instant.now();
        dto.setAutomaticOutcomeDate(now);
        dto.setManualOutcome(AnalysisOutcome.KO);
        dto.setStatus(ModuleStatus.ATTIVO);
        dto.setAssignedUserId(100L);
        dto.setManualOutcomeDate(now);
        dto.setAssignedUserFirstName("John");
        dto.setAssignedUserLastName("Doe");

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getInstanceId()).isEqualTo(2L);
        assertThat(dto.getModuleId()).isEqualTo(3L);
        assertThat(dto.getModuleCode()).isEqualTo("MOD001");
        assertThat(dto.getAnalysisType()).isEqualTo(AnalysisType.AUTOMATICA);
        assertThat(dto.getAllowManualOutcome()).isTrue();
        assertThat(dto.getAutomaticOutcome()).isEqualTo(AnalysisOutcome.OK);
        assertThat(dto.getAutomaticOutcomeDate()).isEqualTo(now);
        assertThat(dto.getManualOutcome()).isEqualTo(AnalysisOutcome.KO);
        assertThat(dto.getStatus()).isEqualTo(ModuleStatus.ATTIVO);
        assertThat(dto.getAssignedUserId()).isEqualTo(100L);
        assertThat(dto.getManualOutcomeDate()).isEqualTo(now);
        assertThat(dto.getAssignedUserFirstName()).isEqualTo("John");
        assertThat(dto.getAssignedUserLastName()).isEqualTo("Doe");
    }

    @Test
    void testEqualsAndHashCode() {
        InstanceModuleDTO dto1 = new InstanceModuleDTO();
        dto1.setId(1L);

        InstanceModuleDTO dto2 = new InstanceModuleDTO();
        dto2.setId(1L);

        assertThat(dto1).isEqualTo(dto2);
        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());

        dto2.setId(2L);
        assertThat(dto1).isNotEqualTo(dto2);
    }

    @Test
    void testToString() {
        InstanceModuleDTO dto = new InstanceModuleDTO();
        dto.setId(1L);
        dto.setModuleCode("MOD001");

        String toString = dto.toString();

        assertThat(toString).contains("id=1");
        assertThat(toString).contains("moduleCode=MOD001");
    }
}
