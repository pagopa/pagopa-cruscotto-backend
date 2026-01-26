package com.nexigroup.pagopa.cruscotto.domain;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Set;

class PagopaNumeroStandinTest {

    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private PagopaNumeroStandin buildEntity() {
        PagopaNumeroStandin entity = new PagopaNumeroStandin();
        entity.setId(1L);
        entity.setStationCode("ST123");
        entity.setIntervalStart(LocalDateTime.of(2025, 1, 1, 10, 0));
        entity.setIntervalEnd(LocalDateTime.of(2025, 1, 1, 10, 15));
        entity.setStandInCount(5);
        entity.setEventType("PAYMENT");
        entity.setDataDate(LocalDateTime.of(2025, 1, 1, 0, 0));
        entity.setLoadTimestamp(LocalDateTime.now());
        return entity;
    }

    @Test
    void testGettersAndSetters() {
        PagopaNumeroStandin entity = buildEntity();

        assertThat(entity.getId()).isEqualTo(1L);
        assertThat(entity.getStationCode()).isEqualTo("ST123");
        assertThat(entity.getStandInCount()).isEqualTo(5);
        assertThat(entity.getEventType()).isEqualTo("PAYMENT");
    }

    @Test
    void testEqualsAndHashCode() {
        PagopaNumeroStandin e1 = buildEntity();
        PagopaNumeroStandin e2 = buildEntity();

        // Same ID → equals true
        assertThat(e1).isEqualTo(e2);
        assertThat(e1.hashCode()).isEqualTo(e2.hashCode());

        // Different ID → not equal
        e2.setId(2L);
        assertThat(e1).isNotEqualTo(e2);
    }

    @Test
    void testToStringContainsFields() {
        PagopaNumeroStandin entity = buildEntity();
        String str = entity.toString();

        assertThat(str).contains("PagopaNumeroStandin");
        assertThat(str).contains("stationCode='ST123'");
        assertThat(str).contains("standInCount=5");
    }

    @Test
    void testValidationConstraints() {
        PagopaNumeroStandin entity = new PagopaNumeroStandin();
        entity.setId(1L);
        // Missing required fields

        Set<ConstraintViolation<PagopaNumeroStandin>> violations = validator.validate(entity);

        assertThat(violations).isNotEmpty();
        assertThat(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("stationCode"))).isTrue();
        assertThat(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("intervalStart"))).isTrue();
        assertThat(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("intervalEnd"))).isTrue();
        assertThat(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("standInCount"))).isTrue();
        assertThat(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("dataDate"))).isTrue();
    }

    @Test
    void testStationCodeSizeConstraint() {
        PagopaNumeroStandin entity = buildEntity();
        entity.setStationCode("X".repeat(300)); // exceeds 255

        Set<ConstraintViolation<PagopaNumeroStandin>> violations = validator.validate(entity);
        assertThat(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("stationCode"))).isTrue();
    }

    @Test
    void testEventTypeSizeConstraint() {
        PagopaNumeroStandin entity = buildEntity();
        entity.setEventType("Y".repeat(100)); // exceeds 50

        Set<ConstraintViolation<PagopaNumeroStandin>> violations = validator.validate(entity);
        assertThat(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("eventType"))).isTrue();
    }
}
