package com.nexigroup.pagopa.cruscotto.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Set;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class KpiB1AnalyticDrillDownTest {

    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testGettersAndSetters() {
        KpiB1AnalyticDrillDown entity = new KpiB1AnalyticDrillDown();
        KpiB1AnalyticData parent = new KpiB1AnalyticData();

        entity.setId(1L);
        entity.setKpiB1AnalyticData(parent);
        entity.setDataDate(LocalDate.of(2024, 1, 1));
        entity.setPartnerFiscalCode("PARTNER123");
        entity.setInstitutionFiscalCode("INST456");
        entity.setTransactionCount(10);
        entity.setStationCode("STATION789");

        assertThat(entity.getId()).isEqualTo(1L);
        assertThat(entity.getKpiB1AnalyticData()).isEqualTo(parent);
        assertThat(entity.getDataDate()).isEqualTo(LocalDate.of(2024, 1, 1));
        assertThat(entity.getPartnerFiscalCode()).isEqualTo("PARTNER123");
        assertThat(entity.getInstitutionFiscalCode()).isEqualTo("INST456");
        assertThat(entity.getTransactionCount()).isEqualTo(10);
        assertThat(entity.getStationCode()).isEqualTo("STATION789");
    }

    @Test
    void testEqualsAndHashCode() {
        KpiB1AnalyticDrillDown e1 = new KpiB1AnalyticDrillDown();
        e1.setId(1L);

        KpiB1AnalyticDrillDown e2 = new KpiB1AnalyticDrillDown();
        e2.setId(1L);

        KpiB1AnalyticDrillDown e3 = new KpiB1AnalyticDrillDown();
        e3.setId(2L);

        assertThat(e1).isEqualTo(e2);
        assertThat(e1.hashCode()).isEqualTo(e2.hashCode());
        assertThat(e1).isNotEqualTo(e3);
        assertThat(e1).isNotEqualTo(null);
        assertThat(e1).isNotEqualTo(new Object());
    }

    @Test
    void testToString() {
        KpiB1AnalyticDrillDown entity = new KpiB1AnalyticDrillDown();
        entity.setId(100L);
        entity.setDataDate(LocalDate.of(2024, 2, 1));
        entity.setPartnerFiscalCode("PF123");
        entity.setInstitutionFiscalCode("IF456");
        entity.setTransactionCount(5);
        entity.setStationCode("ST001");

        String output = entity.toString();

        assertThat(output).contains("id=100");
        assertThat(output).contains("dataDate=2024-02-01");
        assertThat(output).contains("partnerFiscalCode='PF123'");
        assertThat(output).contains("institutionFiscalCode='IF456'");
        assertThat(output).contains("transactionCount=5");
        assertThat(output).contains("stationCode='ST001'");
    }

    @Test
    void testValidationNotNullFields() {
        KpiB1AnalyticDrillDown entity = new KpiB1AnalyticDrillDown();
        Set<ConstraintViolation<KpiB1AnalyticDrillDown>> violations = validator.validate(entity);

        assertThat(violations).extracting(ConstraintViolation::getPropertyPath).anyMatch(v ->
            v.toString().equals("kpiB1AnalyticData")
        );
        assertThat(violations).extracting(ConstraintViolation::getPropertyPath).anyMatch(v ->
            v.toString().equals("dataDate")
        );
        assertThat(violations).extracting(ConstraintViolation::getPropertyPath).anyMatch(v ->
            v.toString().equals("partnerFiscalCode")
        );
        assertThat(violations).extracting(ConstraintViolation::getPropertyPath).anyMatch(v ->
            v.toString().equals("institutionFiscalCode")
        );
        assertThat(violations).extracting(ConstraintViolation::getPropertyPath).anyMatch(v ->
            v.toString().equals("transactionCount")
        );
    }

    @Test
    void testValidationFieldSize() {
        KpiB1AnalyticDrillDown entity = new KpiB1AnalyticDrillDown();
        entity.setKpiB1AnalyticData(new KpiB1AnalyticData());
        entity.setDataDate(LocalDate.now());
        entity.setTransactionCount(1);

        entity.setPartnerFiscalCode("A".repeat(40)); // too long
        entity.setInstitutionFiscalCode("B".repeat(40)); // too long

        Set<ConstraintViolation<KpiB1AnalyticDrillDown>> violations = validator.validate(entity);

        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("partnerFiscalCode"));
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("institutionFiscalCode"));
    }
}
