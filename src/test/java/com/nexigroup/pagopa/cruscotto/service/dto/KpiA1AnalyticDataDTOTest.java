package com.nexigroup.pagopa.cruscotto.service.dto;

import com.nexigroup.pagopa.cruscotto.service.validation.ValidationGroups;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class KpiA1AnalyticDataDTOTest {

    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testGettersAndSetters() {
        KpiA1AnalyticDataDTO dto = new KpiA1AnalyticDataDTO();

        dto.setId(1L);
        dto.setInstanceId(10L);
        dto.setInstanceModuleId(20L);
        dto.setAnalysisDate(LocalDate.of(2025, 1, 1));
        dto.setStationId(100L);
        dto.setStationName("Station A");
        dto.setMethod("POST");
        dto.setEvaluationDate(LocalDate.of(2025, 1, 2));
        dto.setTotReq(1000L);
        dto.setReqOk(950L);
        dto.setReqTimeoutReal(30L);
        dto.setReqTimeoutValid(20L);
        dto.setKpiA1DetailResultId(200L);

        assertEquals(1L, dto.getId());
        assertEquals(10L, dto.getInstanceId());
        assertEquals(20L, dto.getInstanceModuleId());
        assertEquals(LocalDate.of(2025, 1, 1), dto.getAnalysisDate());
        assertEquals(100L, dto.getStationId());
        assertEquals("Station A", dto.getStationName());
        assertEquals("POST", dto.getMethod());
        assertEquals(LocalDate.of(2025, 1, 2), dto.getEvaluationDate());
        assertEquals(1000L, dto.getTotReq());
        assertEquals(950L, dto.getReqOk());
        assertEquals(30L, dto.getReqTimeoutReal());
        assertEquals(20L, dto.getReqTimeoutValid());
        assertEquals(200L, dto.getKpiA1DetailResultId());
    }

    @Test
    void testToStringIncludesKeyFields() {
        KpiA1AnalyticDataDTO dto = new KpiA1AnalyticDataDTO();
        dto.setStationName("Station Test");
        dto.setMethod("GET");
        String result = dto.toString();

        assertTrue(result.contains("Station Test"));
        assertTrue(result.contains("GET"));
    }

    @Test
    void testValidationFailsWhenRequiredFieldsMissing() {
        KpiA1AnalyticDataDTO dto = new KpiA1AnalyticDataDTO();

        Set<ConstraintViolation<KpiA1AnalyticDataDTO>> violations =
            validator.validate(dto, ValidationGroups.KpiA1Job.class);

        // Should fail on all @NotNull fields for KpiA1Job group
        assertFalse(violations.isEmpty());
        assertTrue(violations.size() >= 8); // conservative check
    }

    @Test
    void testValidationPassesWhenAllRequiredFieldsPresent() {
        KpiA1AnalyticDataDTO dto = new KpiA1AnalyticDataDTO();
        dto.setInstanceId(1L);
        dto.setInstanceModuleId(2L);
        dto.setAnalysisDate(LocalDate.now());
        dto.setStationId(3L);
        dto.setMethod("POST");
        dto.setEvaluationDate(LocalDate.now());
        dto.setTotReq(100L);
        dto.setReqOk(90L);
        dto.setReqTimeoutReal(5L);
        dto.setReqTimeoutValid(2L);

        Set<ConstraintViolation<KpiA1AnalyticDataDTO>> violations =
            validator.validate(dto, ValidationGroups.KpiA1Job.class);

        assertTrue(violations.isEmpty(), "All @NotNull fields should be satisfied");
    }
}
