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
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class KpiA1DetailResultDTOTest {

    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testGetterAndSetter() {
        KpiA1DetailResultDTO dto = new KpiA1DetailResultDTO();
        dto.setId(1L);
        dto.setInstanceId(10L);
        dto.setInstanceModuleId(20L);
        dto.setAnalysisDate(LocalDate.of(2025, 10, 31));
        dto.setEvaluationType(EvaluationType.MESE);
        dto.setEvaluationStartDate(LocalDate.of(2025, 10, 1));
        dto.setEvaluationEndDate(LocalDate.of(2025, 10, 31));
        dto.setTotReq(100L);
        dto.setReqTimeout(5L);
        dto.setTimeoutPercentage(5.0);
        dto.setOutcome(OutcomeStatus.OK);
        dto.setKpiA1ResultId(99L);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getInstanceId()).isEqualTo(10L);
        assertThat(dto.getInstanceModuleId()).isEqualTo(20L);
        assertThat(dto.getAnalysisDate()).isEqualTo(LocalDate.of(2025, 10, 31));
        assertThat(dto.getEvaluationType()).isEqualTo(EvaluationType.MESE);
        assertThat(dto.getEvaluationStartDate()).isEqualTo(LocalDate.of(2025, 10, 1));
        assertThat(dto.getEvaluationEndDate()).isEqualTo(LocalDate.of(2025, 10, 31));
        assertThat(dto.getTotReq()).isEqualTo(100L);
        assertThat(dto.getReqTimeout()).isEqualTo(5L);
        assertThat(dto.getTimeoutPercentage()).isEqualTo(5.0);
        assertThat(dto.getOutcome()).isEqualTo(OutcomeStatus.OK);
        assertThat(dto.getKpiA1ResultId()).isEqualTo(99L);

        // Check that toString contains some key fields
        String toString = dto.toString();
        assertThat(toString).contains("id=1", "instanceId=10", "totReq=100");
    }

    @Test
    void testValidationNotNullConstraints() {
        KpiA1DetailResultDTO dto = new KpiA1DetailResultDTO();

        // Validate with KpiA1Job group
        Set<ConstraintViolation<KpiA1DetailResultDTO>> violations = validator.validate(dto, ValidationGroups.KpiA1Job.class);

        // Collect the property names that failed @NotNull
        Set<String> violationProperties = violations.stream()
            .map(ConstraintViolation::getPropertyPath)
            .map(Object::toString)
            .collect(Collectors.toSet());

        // Ensure all expected fields are violated
        assertThat(violationProperties).containsExactlyInAnyOrder(
            "instanceId",
            "instanceModuleId",
            "analysisDate",
            "evaluationType",
            "evaluationStartDate",
            "evaluationEndDate",
            "totReq",
            "reqTimeout",
            "timeoutPercentage",
            "outcome"
        );

        // Fill all required fields
        dto.setInstanceId(1L);
        dto.setInstanceModuleId(2L);
        dto.setAnalysisDate(LocalDate.now());
        dto.setEvaluationType(EvaluationType.MESE);
        dto.setEvaluationStartDate(LocalDate.now());
        dto.setEvaluationEndDate(LocalDate.now().plusDays(1));
        dto.setTotReq(100L);
        dto.setReqTimeout(10L);
        dto.setTimeoutPercentage(10.0);
        dto.setOutcome(OutcomeStatus.OK);

        // Validate again
        violations = validator.validate(dto, ValidationGroups.KpiA1Job.class);
        assertThat(violations).isEmpty();
    }
}
