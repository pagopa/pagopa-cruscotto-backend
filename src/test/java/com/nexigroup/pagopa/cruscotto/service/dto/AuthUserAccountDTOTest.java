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
        dto.setLangKey("e"); // Too short, triggers @Size(min = 2, max = 10)

        Set<ConstraintViolation<AuthUserAccountDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "DTO should have validation violations");

        // Check specific violations
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
}
