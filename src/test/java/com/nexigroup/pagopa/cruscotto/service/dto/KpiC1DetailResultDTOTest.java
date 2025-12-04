package com.nexigroup.pagopa.cruscotto.service.dto;

import com.nexigroup.pagopa.cruscotto.domain.enumeration.EvaluationType;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import org.junit.jupiter.api.Test;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.ConstraintViolation;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class KpiC1DetailResultDTOTest {

    private final Validator validator;

    public KpiC1DetailResultDTOTest() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private KpiC1DetailResultDTO createValidDto() {
        KpiC1DetailResultDTO dto = new KpiC1DetailResultDTO();

        dto.setId(1L);
        dto.setInstanceId(100L);
        dto.setInstanceModuleId(200L);
        dto.setAnalysisDate(LocalDate.now());
        dto.setEvaluationType(EvaluationType.MESE);
        dto.setEvaluationStartDate(LocalDate.now().minusDays(10));
        dto.setEvaluationEndDate(LocalDate.now());
        dto.setTotalInstitutions(50L);
        dto.setOkTotalInstitutions(45L);
        dto.setPercentageOkInstitutions(new BigDecimal("90.0"));
        dto.setOutcome(OutcomeStatus.OK);
        dto.setKpiC1ResultId(999L);

        return dto;
    }

    @Test
    void testGettersAndSetters() {
        KpiC1DetailResultDTO dto = createValidDto();

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getInstanceId()).isEqualTo(100L);
        assertThat(dto.getInstanceModuleId()).isEqualTo(200L);
        assertThat(dto.getAnalysisDate()).isNotNull();
        assertThat(dto.getEvaluationType()).isEqualTo(EvaluationType.MESE);
        assertThat(dto.getEvaluationStartDate()).isNotNull();
        assertThat(dto.getEvaluationEndDate()).isNotNull();
        assertThat(dto.getTotalInstitutions()).isEqualTo(50L);
        assertThat(dto.getOkTotalInstitutions()).isEqualTo(45L);
        assertThat(dto.getPercentageOkInstitutions()).isEqualTo(new BigDecimal("90.0"));
        assertThat(dto.getOutcome()).isEqualTo(OutcomeStatus.OK);
        assertThat(dto.getKpiC1ResultId()).isEqualTo(999L);
    }

    @Test
    void testEqualsAndHashCode() {
        KpiC1DetailResultDTO dto1 = createValidDto();
        KpiC1DetailResultDTO dto2 = createValidDto();

        assertThat(dto1)
            .isEqualTo(dto2)
            .hasSameHashCodeAs(dto2);
    }

    @Test
    void testNotEquals() {
        KpiC1DetailResultDTO dto1 = createValidDto();
        KpiC1DetailResultDTO dto2 = createValidDto();

        dto2.setId(999L);

        assertThat(dto1).isNotEqualTo(dto2);
    }

    @Test
    void testToString() {
        KpiC1DetailResultDTO dto = createValidDto();

        String str = dto.toString();

        assertThat(str)
            .contains("KpiC1DetailResultDTO")
            .contains("instanceId=100")
            .contains("percentageOkInstitutions=90.0");
    }

    @Test
    void testValidationSuccess() {
        KpiC1DetailResultDTO dto = createValidDto();

        Set<ConstraintViolation<KpiC1DetailResultDTO>> violations = validator.validate(dto);

        assertThat(violations).isEmpty();
    }

    @Test
    void testValidationFailure() {
        KpiC1DetailResultDTO dto = new KpiC1DetailResultDTO(); // all null fields

        Set<ConstraintViolation<KpiC1DetailResultDTO>> violations = validator.validate(dto);

        // 11 mandatory @NotNull fields
        assertThat(violations).hasSize(11);
    }
}
