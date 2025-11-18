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
import static org.assertj.core.api.Assertions.assertThatCode;

class KpiB8AnalyticDataDTOTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private KpiB8AnalyticDataDTO createValidDTO() {
        KpiB8AnalyticDataDTO dto = new KpiB8AnalyticDataDTO();
        dto.setId(1L);
        dto.setInstanceId(10L);
        dto.setInstanceModuleId(20L);
        dto.setAnagStationId(30L);
        dto.setAnalysisDate(LocalDate.of(2025, 11, 12));
        dto.setEvaluationDate(LocalDate.of(2025, 11, 13));
        dto.setTotReq(100L);
        dto.setReqKO(5L);
        dto.setKpiB8DetailResultId(50L);
        dto.setAnalysisDatePeriod("2025-Q4");
        return dto;
    }

    @Test
    void testGetterAndSetter() {
        KpiB8AnalyticDataDTO dto = createValidDTO();
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getInstanceId()).isEqualTo(10L);
        assertThat(dto.getInstanceModuleId()).isEqualTo(20L);
        assertThat(dto.getAnagStationId()).isEqualTo(30L);
        assertThat(dto.getAnalysisDate()).isEqualTo(LocalDate.of(2025, 11, 12));
        assertThat(dto.getEvaluationDate()).isEqualTo(LocalDate.of(2025, 11, 13));
        assertThat(dto.getTotReq()).isEqualTo(100L);
        assertThat(dto.getReqKO()).isEqualTo(5L);
        assertThat(dto.getKpiB8DetailResultId()).isEqualTo(50L);
        assertThat(dto.getAnalysisDatePeriod()).isEqualTo("2025-Q4");
    }

    @Test
    void testNotNullConstraints() {
        KpiB8AnalyticDataDTO dto = new KpiB8AnalyticDataDTO();

        Set<ConstraintViolation<KpiB8AnalyticDataDTO>> violations = validator.validate(dto);

        assertThat(violations).hasSize(7);
        assertThat(violations)
            .extracting(v -> v.getPropertyPath().toString())
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

    @Test
    void testValidDTOHasNoViolations() {
        KpiB8AnalyticDataDTO dto = createValidDTO();
        Set<ConstraintViolation<KpiB8AnalyticDataDTO>> violations = validator.validate(dto);
        assertThat(violations).isEmpty();
    }

    @Test
    void testEqualsAndHashCode() {
        KpiB8AnalyticDataDTO dto1 = createValidDTO();
        KpiB8AnalyticDataDTO dto2 = createValidDTO();
        KpiB8AnalyticDataDTO dto3 = createValidDTO();
        dto3.setId(2L); // different ID

        // Reflexive
        assertThat(dto1).isEqualTo(dto1);
        // Symmetric
        assertThat(dto1).isEqualTo(dto2);
        assertThat(dto2).isEqualTo(dto1);
        // Transitive
        assertThat(dto1).isEqualTo(dto2).isNotEqualTo(dto3);
        // Consistent
        assertThat(dto1.equals(dto2)).isTrue();
        // Not equal to null or different class
        assertThat(dto1.equals(null)).isFalse();
        assertThat(dto1.equals("string")).isFalse();

        // HashCode consistency
        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
        assertThat(dto1.hashCode()).isNotEqualTo(dto3.hashCode());
    }

    @Test
    void testToStringContainsAllFields() {
        KpiB8AnalyticDataDTO dto = createValidDTO();
        String str = dto.toString();
        assertThat(str)
            .contains("id=1")
            .contains("instanceId=10")
            .contains("instanceModuleId=20")
            .contains("anagStationId=30")
            .contains("analysisDate=2025-11-12")
            .contains("evaluationDate=2025-11-13")
            .contains("totReq=100")
            .contains("reqKO=5")
            .contains("kpiB8DetailResultId=50")
            .contains("analysisDatePeriod=2025-Q4");
    }

    @Test
    void testOptionalFieldsCanBeNull() {
        KpiB8AnalyticDataDTO dto = createValidDTO();
        dto.setKpiB8DetailResultId(null);
        dto.setAnalysisDatePeriod(null);
        assertThatCode(() -> validator.validate(dto)).doesNotThrowAnyException();
    }

    @Test
    void testDifferentDatesStillValid() {
        KpiB8AnalyticDataDTO dto = createValidDTO();
        dto.setAnalysisDate(LocalDate.of(2020, 1, 1));
        dto.setEvaluationDate(LocalDate.of(2030, 1, 1));
        Set<ConstraintViolation<KpiB8AnalyticDataDTO>> violations = validator.validate(dto);
        assertThat(violations).isEmpty();
    }
}
