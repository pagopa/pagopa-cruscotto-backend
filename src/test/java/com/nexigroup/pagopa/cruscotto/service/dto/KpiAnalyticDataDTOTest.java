package com.nexigroup.pagopa.cruscotto.service.dto;

import com.nexigroup.pagopa.cruscotto.domain.enumeration.ModuleCode;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class KpiAnalyticDataDTOTest {

    @Test
    void testGetterAndSetter() {
        KpiAnalyticDataDTO dto = new KpiAnalyticDataDTO();

        dto.setId(1L);
        dto.setModuleCode(ModuleCode.B3); // Replace with a real enum value
        dto.setInstanceId(2L);
        dto.setInstanceModuleId(3L);
        dto.setKpiDetailResultId(4L);
        dto.setAnalysisDate(LocalDate.of(2025, 10, 30));
        dto.setDataDate(LocalDate.of(2025, 10, 29));
        dto.setAnalyticData("{\"key\":\"value\"}");
        dto.setCreatedBy("tester");
        dto.setCreatedDate(Instant.now());
        dto.setLastModifiedBy("modifier");
        dto.setLastModifiedDate(Instant.now());

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getModuleCode()).isEqualTo(ModuleCode.B3);
        assertThat(dto.getInstanceId()).isEqualTo(2L);
        assertThat(dto.getInstanceModuleId()).isEqualTo(3L);
        assertThat(dto.getKpiDetailResultId()).isEqualTo(4L);
        assertThat(dto.getAnalysisDate()).isEqualTo(LocalDate.of(2025, 10, 30));
        assertThat(dto.getDataDate()).isEqualTo(LocalDate.of(2025, 10, 29));
        assertThat(dto.getAnalyticData()).isEqualTo("{\"key\":\"value\"}");
        assertThat(dto.getCreatedBy()).isEqualTo("tester");
        assertThat(dto.getCreatedDate()).isNotNull();
        assertThat(dto.getLastModifiedBy()).isEqualTo("modifier");
        assertThat(dto.getLastModifiedDate()).isNotNull();
    }

    @Test
    void testEqualsAndHashCode() {
        KpiAnalyticDataDTO dto1 = new KpiAnalyticDataDTO();
        KpiAnalyticDataDTO dto2 = new KpiAnalyticDataDTO();

        dto1.setId(1L);
        dto2.setId(1L);

        assertThat(dto1).isEqualTo(dto2);
        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());

        dto2.setId(2L);
        assertThat(dto1).isNotEqualTo(dto2);
    }

    @Test
    void testNotNullValidation() {
        KpiAnalyticDataDTO dto = new KpiAnalyticDataDTO();
        // Only moduleCode is annotated @NotNull
        dto.setModuleCode(null);

        // Using javax validation for testing @NotNull
        jakarta.validation.ValidatorFactory factory = jakarta.validation.Validation.buildDefaultValidatorFactory();
        jakarta.validation.Validator validator = factory.getValidator();

        var violations = validator.validate(dto);
        assertThat(violations).isNotEmpty();
        assertThat(violations.stream().anyMatch(v -> "moduleCode".equals(v.getPropertyPath().toString()))).isTrue();
    }
}
