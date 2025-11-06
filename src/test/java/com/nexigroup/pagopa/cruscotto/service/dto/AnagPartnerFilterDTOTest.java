package com.nexigroup.pagopa.cruscotto.service.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class AnagPartnerFilterDTOTest {

    private Validator validator;

    @BeforeEach
    void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValidDatesAndFields() {
        AnagPartnerFilterDTO dto = new AnagPartnerFilterDTO();
        dto.setPartnerId(1L);
        dto.setAnalyzed(true);
        dto.setQualified(false);
        dto.setShowNotActive(true);
        dto.setAnalysisPeriodStartDate("2025-01-01");
        dto.setAnalysisPeriodEndDate("2025-01-10");
        dto.setLastAnalysisDate("2025-01-05");

        Set<ConstraintViolation<AnagPartnerFilterDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "Expected no validation violations");
    }

    @Test
    void testInvalidDateFormat() {
        AnagPartnerFilterDTO dto = new AnagPartnerFilterDTO();
        // Supponendo che il pattern valido sia yyyy-MM-dd
        dto.setAnalysisPeriodStartDate("2025/01/01"); // invalid format
        dto.setAnalysisPeriodEndDate("2025-01-10");
        dto.setLastAnalysisDate("2025-01-05");

        Set<ConstraintViolation<AnagPartnerFilterDTO>> violations = validator.validate(dto);

        // Stampa le violazioni per capire
        violations.forEach(v -> System.out.println(v.getPropertyPath() + ": " + v.getMessage()));

        assertFalse(violations.isEmpty(), "Expected validation violations due to wrong date format");
    }

    @Test
    void testStartDateAfterEndDate() {
        AnagPartnerFilterDTO dto = new AnagPartnerFilterDTO();
        dto.setAnalysisPeriodStartDate("2025-01-20");
        dto.setAnalysisPeriodEndDate("2025-01-10");
        dto.setLastAnalysisDate("2025-01-05");

        Set<ConstraintViolation<AnagPartnerFilterDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Expected violations due to start date after end date");
    }

    @Test
    void testStartDateEqualsEndDateValid() {
        AnagPartnerFilterDTO dto = new AnagPartnerFilterDTO();
        dto.setAnalysisPeriodStartDate("2025-01-10");
        dto.setAnalysisPeriodEndDate("2025-01-10");
        dto.setLastAnalysisDate("2025-01-05");

        Set<ConstraintViolation<AnagPartnerFilterDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "No violations expected because equalsIsValid=true");
    }

    @Test
    void testLombokGettersAndSetters() {
        AnagPartnerFilterDTO dto = new AnagPartnerFilterDTO();
        dto.setPartnerId(123L);
        dto.setAnalyzed(true);
        dto.setQualified(false);
        dto.setShowNotActive(true);

        assertEquals(123L, dto.getPartnerId());
        assertTrue(dto.getAnalyzed());
        assertFalse(dto.getQualified());
        assertTrue(dto.getShowNotActive());
    }

    @Test
    void testNullOptionalFields() {
        AnagPartnerFilterDTO dto = new AnagPartnerFilterDTO(); // all fields null
        Set<ConstraintViolation<AnagPartnerFilterDTO>> violations = validator.validate(dto);
        // Only validation on @ValidDate/Range may fail if null is not allowed
        // Assuming @ValidDate allows null, we should have no violations
        assertTrue(violations.isEmpty(), "Expected no violations when optional fields are null");
    }
}
