package com.nexigroup.pagopa.cruscotto.service.dto;

import com.nexigroup.pagopa.cruscotto.domain.enumeration.EvaluationType;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import com.nexigroup.pagopa.cruscotto.service.validation.ValidationGroups;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class KpiB2DetailResultDTOTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testGettersAndSetters() {
        KpiB2DetailResultDTO dto = new KpiB2DetailResultDTO();

        dto.setId(1L);
        dto.setInstanceId(10L);
        dto.setInstanceModuleId(20L);
        dto.setAnalysisDate(LocalDate.of(2025, 10, 31));
        dto.setEvaluationType(EvaluationType.MESE);
        dto.setEvaluationStartDate(LocalDate.of(2025, 10, 1));
        dto.setEvaluationEndDate(LocalDate.of(2025, 10, 31));
        dto.setTotReq(100L);
        dto.setAvgTime(12.5);
        dto.setOverTimeLimit(1.5);
        dto.setOutcome(OutcomeStatus.OK);
        dto.setKpiB2ResultId(5L);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getInstanceId()).isEqualTo(10L);
        assertThat(dto.getInstanceModuleId()).isEqualTo(20L);
        assertThat(dto.getAnalysisDate()).isEqualTo(LocalDate.of(2025, 10, 31));
        assertThat(dto.getEvaluationType()).isEqualTo(EvaluationType.MESE);
        assertThat(dto.getEvaluationStartDate()).isEqualTo(LocalDate.of(2025, 10, 1));
        assertThat(dto.getEvaluationEndDate()).isEqualTo(LocalDate.of(2025, 10, 31));
        assertThat(dto.getTotReq()).isEqualTo(100L);
        assertThat(dto.getAvgTime()).isEqualTo(12.5);
        assertThat(dto.getOverTimeLimit()).isEqualTo(1.5);
        assertThat(dto.getOutcome()).isEqualTo(OutcomeStatus.OK);
        assertThat(dto.getKpiB2ResultId()).isEqualTo(5L);

        // toString test
        assertThat(dto.toString()).contains("id=1", "instanceId=10", "totReq=100");
    }

    @Test
    void testValidationNotNullConstraints() {
        KpiB2DetailResultDTO dto = new KpiB2DetailResultDTO();

        Set<ConstraintViolation<KpiB2DetailResultDTO>> violations = validator.validate(dto, ValidationGroups.KpiB2Job.class);

        // Corrected: Should have 10 violations for @NotNull fields
        assertThat(violations).hasSize(10);

        // Optionally check specific fields
        assertThat(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("instanceId"))).isTrue();
        assertThat(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("evaluationType"))).isTrue();
    }

    @Test
    void testValidationPassesWithAllFieldsSet() {
        KpiB2DetailResultDTO dto = new KpiB2DetailResultDTO();
        dto.setInstanceId(1L);
        dto.setInstanceModuleId(1L);
        dto.setAnalysisDate(LocalDate.now());
        dto.setEvaluationType(EvaluationType.MESE);
        dto.setEvaluationStartDate(LocalDate.now());
        dto.setEvaluationEndDate(LocalDate.now());
        dto.setTotReq(10L);
        dto.setAvgTime(5.0);
        dto.setOverTimeLimit(0.5);
        dto.setOutcome(OutcomeStatus.OK);

        Set<ConstraintViolation<KpiB2DetailResultDTO>> violations = validator.validate(dto, ValidationGroups.KpiB2Job.class);

        assertThat(violations).isEmpty();
    }
}
