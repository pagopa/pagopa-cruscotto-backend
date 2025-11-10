package com.nexigroup.pagopa.cruscotto.service.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class KpiB8AnalyticDataDTOTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testGetterAndSetter() {
        KpiB8AnalyticDataDTO dto = new KpiB8AnalyticDataDTO();
        LocalDate now = LocalDate.now();

        dto.setId(1L);
        dto.setInstanceId(10L);
        dto.setInstanceModuleId(20L);
        dto.setAnagStationId(30L);
        dto.setAnalysisDate(now);
        dto.setEvaluationDate(now);
        dto.setTotReq(100L);
        dto.setReqKO(5L);
        dto.setKpiB8DetailResultId(50L);
        dto.setAnalysisDatePeriod("2025-Q4");

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getInstanceId()).isEqualTo(10L);
        assertThat(dto.getInstanceModuleId()).isEqualTo(20L);
        assertThat(dto.getAnagStationId()).isEqualTo(30L);
        assertThat(dto.getAnalysisDate()).isEqualTo(now);
        assertThat(dto.getEvaluationDate()).isEqualTo(now);
        assertThat(dto.getTotReq()).isEqualTo(100L);
        assertThat(dto.getReqKO()).isEqualTo(5L);
        assertThat(dto.getKpiB8DetailResultId()).isEqualTo(50L);
        assertThat(dto.getAnalysisDatePeriod()).isEqualTo("2025-Q4");
    }

    @Test
    void testNotNullConstraints() {
        KpiB8AnalyticDataDTO dto = new KpiB8AnalyticDataDTO();

        Set<ConstraintViolation<KpiB8AnalyticDataDTO>> violations = validator.validate(dto);

        // There should be violations for all @NotNull fields
        assertThat(violations).hasSize(7);
        assertThat(violations)
            .extracting("propertyPath")
            .extracting(Object::toString)
            .containsExactlyInAnyOrder(
                "instanceId",
                "instanceModuleId",
                "anagStationId",
                "analysisDate",
                "evaluationDate",
                "totReq",
                "reqKO"
            );
    }
}
