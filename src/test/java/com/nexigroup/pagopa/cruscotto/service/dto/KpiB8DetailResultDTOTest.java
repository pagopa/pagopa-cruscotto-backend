package com.nexigroup.pagopa.cruscotto.service.dto;

import com.nexigroup.pagopa.cruscotto.domain.enumeration.EvaluationType;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class KpiB8DetailResultDTOTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private KpiB8DetailResultDTO createValidDTO() {
        KpiB8DetailResultDTO dto = new KpiB8DetailResultDTO();
        dto.setId(1L);
        dto.setInstanceId(2L);
        dto.setInstanceModuleId(3L);
        dto.setAnalysisDate(LocalDate.of(2025, 10, 30));
        dto.setEvaluationType(EvaluationType.MESE);
        dto.setEvaluationStartDate(LocalDate.of(2025, 10, 1));
        dto.setEvaluationEndDate(LocalDate.of(2025, 10, 31));
        dto.setTotReq(100L);
        dto.setReqKO(5L);
        dto.setPerKO(BigDecimal.valueOf(5.0));
        dto.setOutcome(OutcomeStatus.OK);
        dto.setKpiB8ResultId(10L);
        return dto;
    }

    @Test
    void testGettersAndSetters() {
        KpiB8DetailResultDTO dto = createValidDTO();

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getInstanceId()).isEqualTo(2L);
        assertThat(dto.getInstanceModuleId()).isEqualTo(3L);
        assertThat(dto.getAnalysisDate()).isEqualTo(LocalDate.of(2025, 10, 30));
        assertThat(dto.getEvaluationType()).isEqualTo(EvaluationType.MESE);
        assertThat(dto.getEvaluationStartDate()).isEqualTo(LocalDate.of(2025, 10, 1));
        assertThat(dto.getEvaluationEndDate()).isEqualTo(LocalDate.of(2025, 10, 31));
        assertThat(dto.getTotReq()).isEqualTo(100L);
        assertThat(dto.getReqKO()).isEqualTo(5L);
        assertThat(dto.getPerKO()).isEqualTo(BigDecimal.valueOf(5.0));
        assertThat(dto.getOutcome()).isEqualTo(OutcomeStatus.OK);
        assertThat(dto.getKpiB8ResultId()).isEqualTo(10L);
    }

    @Test
    void testValidDTOHasNoViolations() {
        KpiB8DetailResultDTO dto = createValidDTO();
        Set<ConstraintViolation<KpiB8DetailResultDTO>> violations = validator.validate(dto);
        assertThat(violations).isEmpty();
    }

    @Test
    void testMissingRequiredFieldsShouldCauseViolations() {
        KpiB8DetailResultDTO dto = new KpiB8DetailResultDTO();
        Set<ConstraintViolation<KpiB8DetailResultDTO>> violations = validator.validate(dto);

        // Should trigger 11 @NotNull violations (including perKO)
        assertThat(violations).hasSize(10);
        assertThat(violations.stream().map(ConstraintViolation::getPropertyPath))
            .extracting(Object::toString)
            .contains("id", "instanceId", "instanceModuleId",
                "analysisDate", "evaluationType", "evaluationStartDate",
                "evaluationEndDate", "totReq", "reqKO", "perKO");
    }

    @Test
    void testEqualsAndHashCode() {
        KpiB8DetailResultDTO dto1 = createValidDTO();
        KpiB8DetailResultDTO dto2 = createValidDTO();
        KpiB8DetailResultDTO dto3 = createValidDTO();
        dto3.setId(999L);

        assertThat(dto1).isEqualTo(dto2);
        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
        assertThat(dto1).isNotEqualTo(dto3);
    }

    @Test
    void testEqualsWithDifferentTypesAndNulls() {
        KpiB8DetailResultDTO dto = createValidDTO();
        assertThat(dto).isNotEqualTo(null);
        assertThat(dto).isNotEqualTo("some string");
    }

    @Test
    void testToStringShouldContainFieldNames() {
        KpiB8DetailResultDTO dto = createValidDTO();
        String toString = dto.toString();
        assertThat(toString)
            .contains("id=1")
            .contains("instanceId=2")
            .contains("evaluationType=MESE")
            .contains("outcome=OK");
    }

    @Test
    void testBigDecimalPrecisionAndEquality() {
        KpiB8DetailResultDTO dto = createValidDTO();
        dto.setPerKO(new BigDecimal("5.00"));
        assertThat(dto.getPerKO()).isEqualByComparingTo(new BigDecimal("5.0"));
    }

    @Test
    void testCanSetOutcomeToNullWithoutViolations() {
        KpiB8DetailResultDTO dto = createValidDTO();
        dto.setOutcome(null);
        Set<ConstraintViolation<KpiB8DetailResultDTO>> violations = validator.validate(dto);
        assertThat(violations).isEmpty();
    }

    @Test
    void testSettersWorkIndividually() {
        KpiB8DetailResultDTO dto = new KpiB8DetailResultDTO();
        dto.setId(1L);
        dto.setInstanceId(2L);
        dto.setInstanceModuleId(3L);
        dto.setAnalysisDate(LocalDate.now());
        dto.setEvaluationType(EvaluationType.MESE);
        dto.setEvaluationStartDate(LocalDate.now().minusDays(1));
        dto.setEvaluationEndDate(LocalDate.now());
        dto.setTotReq(100L);
        dto.setReqKO(5L);
        dto.setPerKO(BigDecimal.ONE);
        dto.setOutcome(OutcomeStatus.OK);
        dto.setKpiB8ResultId(9L);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getKpiB8ResultId()).isEqualTo(9L);
    }
}
