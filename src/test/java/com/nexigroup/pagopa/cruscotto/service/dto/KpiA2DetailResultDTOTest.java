package com.nexigroup.pagopa.cruscotto.service.dto;

import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class KpiA2DetailResultDTOTest {

    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testGettersAndSetters() {
        KpiA2DetailResultDTO dto = new KpiA2DetailResultDTO();

        dto.setId(1L);
        dto.setInstanceId(10L);
        dto.setInstanceModuleId(20L);
        dto.setAnalysisDate(LocalDate.of(2025, 1, 1));
        dto.setEvaluationStartDate(LocalDate.of(2025, 1, 2));
        dto.setEvaluationEndDate(LocalDate.of(2025, 1, 3));
        dto.setTotPayments(100L);
        dto.setTotIncorrectPayments(5L);
        dto.setErrorPercentage(5.0);
        dto.setOutcome(OutcomeStatus.OK);
        dto.setKpiA2ResultId(99L);

        assertEquals(1L, dto.getId());
        assertEquals(10L, dto.getInstanceId());
        assertEquals(20L, dto.getInstanceModuleId());
        assertEquals(LocalDate.of(2025, 1, 1), dto.getAnalysisDate());
        assertEquals(LocalDate.of(2025, 1, 2), dto.getEvaluationStartDate());
        assertEquals(LocalDate.of(2025, 1, 3), dto.getEvaluationEndDate());
        assertEquals(100L, dto.getTotPayments());
        assertEquals(5L, dto.getTotIncorrectPayments());
        assertEquals(5.0, dto.getErrorPercentage());
        assertEquals(OutcomeStatus.OK, dto.getOutcome());
        assertEquals(99L, dto.getKpiA2ResultId());
    }

    @Test
    void testToString() {
        KpiA2DetailResultDTO dto = new KpiA2DetailResultDTO();
        dto.setId(1L);
        dto.setInstanceId(10L);
        dto.setInstanceModuleId(20L);
        dto.setAnalysisDate(LocalDate.of(2025, 1, 1));
        dto.setEvaluationStartDate(LocalDate.of(2025, 1, 2));
        dto.setEvaluationEndDate(LocalDate.of(2025, 1, 3));
        dto.setTotPayments(100L);
        dto.setTotIncorrectPayments(5L);
        dto.setErrorPercentage(5.0);
        dto.setOutcome(OutcomeStatus.OK);
        dto.setKpiA2ResultId(99L);

        String str = dto.toString();

        assertTrue(str.contains("KpiA2DetailResultDTO"));
        assertTrue(str.contains("id=1"));
        assertTrue(str.contains("instanceId=10"));
        assertTrue(str.contains("totPayments=100"));
        assertTrue(str.contains("errorPercentage=5.0"));
        assertTrue(str.contains("outcome=OK"));
    }

    @Test
    void testValidationConstraints() {
        // Create DTO missing required fields
        KpiA2DetailResultDTO dto = new KpiA2DetailResultDTO();

        // Should produce constraint violations for all @NotNull fields
        Set<ConstraintViolation<KpiA2DetailResultDTO>> violations =
            validator.validate(dto, com.nexigroup.pagopa.cruscotto.service.validation.ValidationGroups.KpiA2Job.class);

        assertFalse(violations.isEmpty());
        assertTrue(
            violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("instanceId"))
        );
        assertTrue(
            violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("outcome"))
        );
    }
}
