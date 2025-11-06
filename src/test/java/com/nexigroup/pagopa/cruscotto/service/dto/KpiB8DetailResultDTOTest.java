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
        validator = factory.getValidator(); // No cast needed
    }

    @Test
    void testGettersAndSetters() {
        KpiB8DetailResultDTO dto = new KpiB8DetailResultDTO();

        dto.setId(1L);
        dto.setInstanceId(2L);
        dto.setInstanceModuleId(3L);
        dto.setAnagStationId(4L);
        dto.setAnalysisDate(LocalDate.of(2025, 10, 30));
        dto.setEvaluationType(EvaluationType.MESE);
        dto.setEvaluationStartDate(LocalDate.of(2025, 10, 1));
        dto.setEvaluationEndDate(LocalDate.of(2025, 10, 31));
        dto.setTotReq(100L);
        dto.setReqKO(5L);
        dto.setPerKO(BigDecimal.valueOf(5.0));
        dto.setOutcome(OutcomeStatus.OK);
        dto.setKpiB8ResultId(10L);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getInstanceId()).isEqualTo(2L);
        assertThat(dto.getInstanceModuleId()).isEqualTo(3L);
        assertThat(dto.getAnagStationId()).isEqualTo(4L);
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
        KpiB8DetailResultDTO dto = new KpiB8DetailResultDTO();
        dto.setId(1L);
        dto.setInstanceId(2L);
        dto.setInstanceModuleId(3L);
        dto.setAnagStationId(4L);
        dto.setAnalysisDate(LocalDate.now());
        dto.setEvaluationType(EvaluationType.MESE);
        dto.setEvaluationStartDate(LocalDate.now().minusDays(5));
        dto.setEvaluationEndDate(LocalDate.now());
        dto.setTotReq(100L);
        dto.setReqKO(10L);
        dto.setPerKO(BigDecimal.valueOf(10.0));

        Set<ConstraintViolation<KpiB8DetailResultDTO>> violations = validator.validate(dto);
        assertThat(violations).isEmpty();
    }
}
