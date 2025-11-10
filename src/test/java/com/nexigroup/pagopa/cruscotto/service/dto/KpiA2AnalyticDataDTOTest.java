package com.nexigroup.pagopa.cruscotto.service.dto;

import com.nexigroup.pagopa.cruscotto.service.validation.ValidationGroups;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class KpiA2AnalyticDataDTOTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testGetterAndSetter() {
        KpiA2AnalyticDataDTO dto = new KpiA2AnalyticDataDTO();

        dto.setId(1L);
        dto.setInstanceId(100L);
        dto.setInstanceModuleId(200L);
        dto.setAnalysisDate(LocalDate.of(2025, 10, 31));
        dto.setEvaluationDate(LocalDate.of(2025, 11, 1));
        dto.setTotPayments(500L);
        dto.setTotIncorrectPayments(5L);
        dto.setKpiA2DetailResultId(999L);

        assertEquals(1L, dto.getId());
        assertEquals(100L, dto.getInstanceId());
        assertEquals(200L, dto.getInstanceModuleId());
        assertEquals(LocalDate.of(2025, 10, 31), dto.getAnalysisDate());
        assertEquals(LocalDate.of(2025, 11, 1), dto.getEvaluationDate());
        assertEquals(500L, dto.getTotPayments());
        assertEquals(5L, dto.getTotIncorrectPayments());
        assertEquals(999L, dto.getKpiA2DetailResultId());

        // ToString check (optional)
        assertTrue(dto.toString().contains("id=1"));
    }

    @Test
    void testNotNullValidation() {
        KpiA2AnalyticDataDTO dto = new KpiA2AnalyticDataDTO();

        // All required fields are null -> should fail validation
        Set<ConstraintViolation<KpiA2AnalyticDataDTO>> violations =
            validator.validate(dto, ValidationGroups.KpiA2Job.class);
        assertEquals(6, violations.size()); // instanceId, instanceModuleId, analysisDate, evaluationDate, totPayments, totIncorrectPayments

        // Fill required fields
        dto.setInstanceId(100L);
        dto.setInstanceModuleId(200L);
        dto.setAnalysisDate(LocalDate.of(2025, 10, 31));
        dto.setEvaluationDate(LocalDate.of(2025, 11, 1));
        dto.setTotPayments(500L);
        dto.setTotIncorrectPayments(5L);

        violations = validator.validate(dto, ValidationGroups.KpiA2Job.class);
        assertTrue(violations.isEmpty());
    }
}
