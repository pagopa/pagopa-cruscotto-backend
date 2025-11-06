package com.nexigroup.pagopa.cruscotto.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.nexigroup.pagopa.cruscotto.domain.enumeration.ModuleCode;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;

class KpiDetailResultDTOTest {

    @Test
    void testGettersAndSetters() {
        KpiDetailResultDTO dto = new KpiDetailResultDTO();

        dto.setId(1L);
        dto.setModuleCode(ModuleCode.B3); // Replace with actual enum
        dto.setInstanceId(2L);
        dto.setInstanceModuleId(3L);
        dto.setAnalysisDate(LocalDate.of(2025, 10, 30));
        dto.setOutcome(OutcomeStatus.OK); // Replace with actual enum
        dto.setKpiResultId(4L);
        dto.setAdditionalData("{\"key\":\"value\"}");
        dto.setCreatedBy("user1");
        dto.setCreatedDate(Instant.now());
        dto.setLastModifiedBy("user2");
        dto.setLastModifiedDate(Instant.now());

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getModuleCode()).isEqualTo(ModuleCode.B3);
        assertThat(dto.getInstanceId()).isEqualTo(2L);
        assertThat(dto.getInstanceModuleId()).isEqualTo(3L);
        assertThat(dto.getAnalysisDate()).isEqualTo(LocalDate.of(2025, 10, 30));
        assertThat(dto.getOutcome()).isEqualTo(OutcomeStatus.OK);
        assertThat(dto.getKpiResultId()).isEqualTo(4L);
        assertThat(dto.getAdditionalData()).isEqualTo("{\"key\":\"value\"}");
        assertThat(dto.getCreatedBy()).isEqualTo("user1");
        assertThat(dto.getLastModifiedBy()).isEqualTo("user2");
        assertThat(dto.getCreatedDate()).isNotNull();
        assertThat(dto.getLastModifiedDate()).isNotNull();
    }

    @Test
    void testEqualsAndHashCode() {
        KpiDetailResultDTO dto1 = new KpiDetailResultDTO();
        KpiDetailResultDTO dto2 = new KpiDetailResultDTO();

        dto1.setId(1L);
        dto2.setId(1L);

        assertThat(dto1).isEqualTo(dto2);
        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());

        dto2.setId(2L);
        assertThat(dto1).isNotEqualTo(dto2);
    }

    @Test
    void testToString() {
        KpiDetailResultDTO dto = new KpiDetailResultDTO();
        dto.setId(1L);
        dto.setModuleCode(ModuleCode.B3);

        String str = dto.toString();
        assertThat(str).contains("id=1", "moduleCode=B3");
    }
}
