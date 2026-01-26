package com.nexigroup.pagopa.cruscotto.service.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class AuthUserAccountDTOTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testGettersAndSetters() {
        AuthUserAccountDTO dto = new AuthUserAccountDTO();

        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setEmail("john.doe@example.com");
        dto.setLangKey("en");

        assertEquals("John", dto.getFirstName());
        assertEquals("Doe", dto.getLastName());
        assertEquals("john.doe@example.com", dto.getEmail());
        assertEquals("en", dto.getLangKey());
    }

    @Test
    void testValidationSuccess() {
        AuthUserAccountDTO dto = new AuthUserAccountDTO();
        dto.setFirstName("Jane");
        dto.setLastName("Smith");
        dto.setEmail("jane.smith@example.com");
        dto.setLangKey("it");

        Set<ConstraintViolation<AuthUserAccountDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "DTO should have no validation violations");
    }

    @Test
    void testValidationFailure() {
        AuthUserAccountDTO dto = new AuthUserAccountDTO();
        dto.setFirstName("ThisNameIsWayTooLongToBeValidBecauseItExceedsFiftyCharacters");
        dto.setLastName("Doe");
        dto.setEmail("invalid-email");
        dto.setLangKey("e");

        Set<ConstraintViolation<AuthUserAccountDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "DTO should have validation violations");

        boolean hasFirstNameViolation = violations.stream()
            .anyMatch(v -> v.getPropertyPath().toString().equals("firstName"));
        boolean hasEmailViolation = violations.stream()
            .anyMatch(v -> v.getPropertyPath().toString().equals("email"));
        boolean hasLangKeyViolation = violations.stream()
            .anyMatch(v -> v.getPropertyPath().toString().equals("langKey"));

        assertTrue(hasFirstNameViolation, "firstName should violate @Size");
        assertTrue(hasEmailViolation, "email should violate @Email");
        assertTrue(hasLangKeyViolation, "langKey should violate @Size");
    }

    @Test
    void testBoundaryConditions() {
        AuthUserAccountDTO dto = new AuthUserAccountDTO();
        dto.setFirstName("A".repeat(50)); // max allowed
        dto.setLastName("B".repeat(50));  // max allowed
        dto.setEmail("a@b.co");           // min length = 5
        dto.setLangKey("xx");             // min length = 2

        Set<ConstraintViolation<AuthUserAccountDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "Boundary values should be valid");
    }

    @Test
    void testNullValues() {
        AuthUserAccountDTO dto = new AuthUserAccountDTO();
        dto.setFirstName(null);
        dto.setLastName(null);
        dto.setEmail(null);
        dto.setLangKey(null);

        // No @NotNull annotations → should be valid
        Set<ConstraintViolation<AuthUserAccountDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "Null values should be allowed");
    }

    @Test
    void testEqualsAndHashCode() {
        AuthUserAccountDTO dto1 = new AuthUserAccountDTO();
        dto1.setFirstName("Alice");
        dto1.setLastName("Wonderland");
        dto1.setEmail("alice@example.com");
        dto1.setLangKey("en");

        AuthUserAccountDTO dto2 = new AuthUserAccountDTO();
        dto2.setFirstName("Alice");
        dto2.setLastName("Wonderland");
        dto2.setEmail("alice@example.com");
        dto2.setLangKey("en");

        AuthUserAccountDTO dto3 = new AuthUserAccountDTO();
        dto3.setFirstName("Bob");
        dto3.setLastName("Marley");
        dto3.setEmail("bob@example.com");
        dto3.setLangKey("en");

        assertEquals(dto1, dto2, "DTOs with same values should be equal");
        assertEquals(dto1.hashCode(), dto2.hashCode(), "Equal DTOs must have same hashCode");
        assertNotEquals(dto1, dto3, "Different DTOs should not be equal");
    }

    @Test
    void testToStringContainsAllFields() {
        AuthUserAccountDTO dto = new AuthUserAccountDTO();
        dto.setFirstName("Mario");
        dto.setLastName("Rossi");
        dto.setEmail("mario.rossi@example.com");
        dto.setLangKey("it");

        String toString = dto.toString();
        assertTrue(toString.contains("Mario"));
        assertTrue(toString.contains("Rossi"));
        assertTrue(toString.contains("mario.rossi@example.com"));
        assertTrue(toString.contains("it"));
    }
}
