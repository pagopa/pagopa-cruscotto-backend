package com.nexigroup.pagopa.cruscotto.service.dto;

import com.nexigroup.pagopa.cruscotto.domain.enumeration.AnalysisOutcome;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.InstanceStatus;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class InstanceDTOTest {

    @Test
    void testGettersAndSetters() {
        InstanceDTO dto = new InstanceDTO();

        dto.setId(1L);
        assertThat(dto.getId()).isEqualTo(1L);

        dto.setInstanceIdentification("INST-001");
        assertThat(dto.getInstanceIdentification()).isEqualTo("INST-001");

        dto.setPartnerId(10L);
        assertThat(dto.getPartnerId()).isEqualTo(10L);

        dto.setPartnerFiscalCode("ABC123");
        assertThat(dto.getPartnerFiscalCode()).isEqualTo("ABC123");

        dto.setPartnerName("Partner X");
        assertThat(dto.getPartnerName()).isEqualTo("Partner X");

        LocalDate predictedDateAnalysis = LocalDate.now();
        dto.setPredictedDateAnalysis(predictedDateAnalysis);
        assertThat(dto.getPredictedDateAnalysis()).isEqualTo(predictedDateAnalysis);

        Instant applicationDate = Instant.now();
        dto.setApplicationDate(applicationDate);
        assertThat(dto.getApplicationDate()).isEqualTo(applicationDate);

        dto.setAssignedUserId(99L);
        assertThat(dto.getAssignedUserId()).isEqualTo(99L);

        dto.setAssignedFirstName("John");
        assertThat(dto.getAssignedFirstName()).isEqualTo("John");

        dto.setAssignedLastName("Doe");
        assertThat(dto.getAssignedLastName()).isEqualTo("Doe");

        LocalDate analysisStart = LocalDate.now().minusDays(7);
        LocalDate analysisEnd = LocalDate.now();
        dto.setAnalysisPeriodStartDate(analysisStart);
        dto.setAnalysisPeriodEndDate(analysisEnd);
        assertThat(dto.getAnalysisPeriodStartDate()).isEqualTo(analysisStart);
        assertThat(dto.getAnalysisPeriodEndDate()).isEqualTo(analysisEnd);

        dto.setStatus(InstanceStatus.BOZZA);
        assertThat(dto.getStatus()).isEqualTo(InstanceStatus.BOZZA);

        Instant lastAnalysisDate = Instant.now();
        dto.setLastAnalysisDate(lastAnalysisDate);
        assertThat(dto.getLastAnalysisDate()).isEqualTo(lastAnalysisDate);

        dto.setLastAnalysisOutcome(AnalysisOutcome.OK);
        assertThat(dto.getLastAnalysisOutcome()).isEqualTo(AnalysisOutcome.OK);

        Set<InstanceModuleDTO> modules = new HashSet<>();
        dto.setInstanceModules(modules);
        assertThat(dto.getInstanceModules()).isEqualTo(modules);

        dto.setChangePartnerQualified(Boolean.TRUE);
        assertThat(dto.getChangePartnerQualified()).isTrue();
    }

    @Test
    void testEqualsAndHashCode() {
        InstanceDTO dto1 = new InstanceDTO();
        dto1.setId(1L);

        InstanceDTO dto2 = new InstanceDTO();
        dto2.setId(1L);

        assertThat(dto1).isEqualTo(dto2);
        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());

        dto2.setId(2L);
        assertThat(dto1).isNotEqualTo(dto2);
    }

    @Test
    void testToString() {
        InstanceDTO dto = new InstanceDTO();
        dto.setId(1L);
        dto.setInstanceIdentification("INST-001");

        String toString = dto.toString();
        assertThat(toString).contains("InstanceDTO", "id=1", "instanceIdentification='INST-001'");
    }
}
