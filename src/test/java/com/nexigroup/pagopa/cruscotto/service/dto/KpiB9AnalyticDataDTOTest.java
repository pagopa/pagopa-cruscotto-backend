package com.nexigroup.pagopa.cruscotto.service.dto;

import static org.junit.jupiter.api.Assertions.*;

import com.nexigroup.pagopa.cruscotto.service.validation.ValidationGroups;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

class KpiB9AnalyticDataDTOTest {

    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testGettersAndSetters() {
        KpiB9AnalyticDataDTO dto = new KpiB9AnalyticDataDTO();
        LocalDate today = LocalDate.now();

        dto.setId(1L);
        dto.setInstanceId(10L);
        dto.setInstanceModuleId(20L);
        dto.setAnalysisDate(today);
        dto.setStationId(30L);
        dto.setEvaluationDate(today);
        dto.setTotRes(100L);
        dto.setResOk(90L);
        dto.setResKoReal(5L);
        dto.setResKoValid(5L);
        dto.setKpiB9DetailResultId(999L);
        dto.setStationName("TestStation");

        assertEquals(1L, dto.getId());
        assertEquals(10L, dto.getInstanceId());
        assertEquals(20L, dto.getInstanceModuleId());
        assertEquals(today, dto.getAnalysisDate());
        assertEquals(30L, dto.getStationId());
        assertEquals(today, dto.getEvaluationDate());
        assertEquals(100L, dto.getTotRes());
        assertEquals(90L, dto.getResOk());
        assertEquals(5L, dto.getResKoReal());
        assertEquals(5L, dto.getResKoValid());
        assertEquals(999L, dto.getKpiB9DetailResultId());
        assertEquals("TestStation", dto.getStationName());
    }

    @Test
    void testToStringContainsExpectedFields() {
        KpiB9AnalyticDataDTO dto = new KpiB9AnalyticDataDTO();
        dto.setId(1L);
        dto.setInstanceId(10L);
        dto.setInstanceModuleId(20L);
        dto.setAnalysisDate(LocalDate.of(2025, 10, 31));
        dto.setStationId(30L);
        dto.setEvaluationDate(LocalDate.of(2025, 10, 31));
        dto.setTotRes(100L);
        dto.setResOk(90L);
        dto.setResKoReal(5L);
        dto.setResKoValid(5L);
        dto.setKpiB9DetailResultId(999L);

        String result = dto.toString();

        assertTrue(result.contains("KpiB9AnalyticDataDTO"));
        assertTrue(result.contains("id=1"));
        assertTrue(result.contains("instanceId=10"));
        assertTrue(result.contains("resOk=90"));
        assertTrue(result.contains("kpiB9DetailResultId=999"));
    }

    @Test
    void testValidationFailsWhenFieldsAreNull() {
        KpiB9AnalyticDataDTO dto = new KpiB9AnalyticDataDTO();
        // All fields are null by default

        Set<ConstraintViolation<KpiB9AnalyticDataDTO>> violations =
            validator.validate(dto, ValidationGroups.KpiB9Job.class);

        // Ensure validation fails
        assertFalse(violations.isEmpty(), "Validation should fail when required fields are null");

        // Print violations for debugging
        violations.forEach(v ->
            System.out.println(v.getPropertyPath() + ": " + v.getMessage())
        );

        // List of required fields based on your output
        List<String> expectedFields = List.of(
            "totRes",
            "resKoValid",
            "analysisDate",
            "stationId",
            "instanceModuleId",
            "resOk",
            "resKoReal",
            "instanceId",
            "evaluationDate"
        );

        List<String> actualFields = violations.stream()
            .map(v -> v.getPropertyPath().toString())
            .toList();

        // Assert that all required fields fail validation
        assertTrue(actualFields.containsAll(expectedFields),
            "All required fields should fail validation when null");

        // Optionally, assert the total count
        assertEquals(expectedFields.size(), violations.size(),
            "Number of violations should match expected required fields");
    }

    @Test
    void testValidationPassesWhenAllRequiredFieldsSet() {
        KpiB9AnalyticDataDTO dto = new KpiB9AnalyticDataDTO();
        dto.setInstanceId(1L);
        dto.setInstanceModuleId(2L);
        dto.setAnalysisDate(LocalDate.now());
        dto.setStationId(3L);
        dto.setEvaluationDate(LocalDate.now());
        dto.setTotRes(100L);
        dto.setResOk(90L);
        dto.setResKoReal(5L);
        dto.setResKoValid(5L);

        Set<ConstraintViolation<KpiB9AnalyticDataDTO>> violations = validator.validate(dto, ValidationGroups.KpiB9Job.class);
        assertTrue(violations.isEmpty(), "Validation should pass when all required fields are set");
    }
}
