package com.nexigroup.pagopa.cruscotto.service.dto;

import static org.junit.jupiter.api.Assertions.*;

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

class KpiA2ResultDTOTest {

    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testGettersAndSetters() {
        KpiA2ResultDTO dto = new KpiA2ResultDTO();
        dto.setId(1L);
        dto.setInstanceId(10L);
        dto.setInstanceModuleId(20L);
        dto.setAnalysisDate(LocalDate.of(2025, 1, 1));
        dto.setTolerance(0.05);
        dto.setOutcome(OutcomeStatus.OK);

        assertEquals(1L, dto.getId());
        assertEquals(10L, dto.getInstanceId());
        assertEquals(20L, dto.getInstanceModuleId());
        assertEquals(LocalDate.of(2025, 1, 1), dto.getAnalysisDate());
        assertEquals(0.05, dto.getTolerance());
        assertEquals(OutcomeStatus.OK, dto.getOutcome());
    }

    @Test
    void testToStringContainsAllFields() {
        KpiA2ResultDTO dto = new KpiA2ResultDTO();
        dto.setId(1L);
        dto.setInstanceId(10L);
        dto.setInstanceModuleId(20L);
        dto.setAnalysisDate(LocalDate.of(2025, 1, 1));
        dto.setTolerance(0.05);
        dto.setOutcome(OutcomeStatus.KO);

        String result = dto.toString();

        assertTrue(result.contains("id=1"));
        assertTrue(result.contains("instanceId=10"));
        assertTrue(result.contains("instanceModuleId=20"));
        assertTrue(result.contains("analysisDate=2025-01-01"));
        assertTrue(result.contains("tolerance=0.05"));
        assertTrue(result.contains("outcome=KO"));
    }

    @Test
    void testValidationFailsWhenFieldsNull() {
        KpiA2ResultDTO dto = new KpiA2ResultDTO(); // all fields null
        Set<ConstraintViolation<KpiA2ResultDTO>> violations =
            validator.validate(dto, ValidationGroups.KpiA2Job.class);

        assertFalse(violations.isEmpty());
        // Expect all 5 @NotNull fields to be violated
        assertTrue(violations.size() >= 5);
    }

    @Test
    void testValidationPassesWhenAllFieldsPresent() {
        KpiA2ResultDTO dto = new KpiA2ResultDTO();
        dto.setInstanceId(1L);
        dto.setInstanceModuleId(2L);
        dto.setAnalysisDate(LocalDate.now());
        dto.setTolerance(0.1);
        dto.setOutcome(OutcomeStatus.OK);

        Set<ConstraintViolation<KpiA2ResultDTO>> violations =
            validator.validate(dto, ValidationGroups.KpiA2Job.class);

        assertTrue(violations.isEmpty());
    }
}
