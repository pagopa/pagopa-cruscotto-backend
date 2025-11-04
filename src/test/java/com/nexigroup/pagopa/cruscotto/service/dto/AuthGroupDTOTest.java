package com.nexigroup.pagopa.cruscotto.service.dto;

import static org.junit.jupiter.api.Assertions.*;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.HashSet;

public class AuthGroupDTOTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testGettersAndSetters() {
        AuthGroupDTO dto = new AuthGroupDTO();

        Long id = 1L;
        String nome = "Admin";
        String descrizione = "Administrator group with all permissions";
        Integer livelloVisibilita = 5;
        Set<AuthFunctionDTO> functions = new HashSet<>();

        dto.setId(id);
        dto.setNome(nome);
        dto.setDescrizione(descrizione);
        dto.setLivelloVisibilita(livelloVisibilita);
        dto.setAuthFunctions(functions);

        assertEquals(id, dto.getId());
        assertEquals(nome, dto.getNome());
        assertEquals(descrizione, dto.getDescrizione());
        assertEquals(livelloVisibilita, dto.getLivelloVisibilita());
        assertEquals(functions, dto.getAuthFunctions());
    }

    @Test
    void testEqualsAndHashCode() {
        AuthGroupDTO dto1 = new AuthGroupDTO();
        AuthGroupDTO dto2 = new AuthGroupDTO();

        dto1.setId(1L);
        dto1.setNome("Admin");
        dto1.setDescrizione("Desc");
        dto1.setLivelloVisibilita(1);

        dto2.setId(1L);
        dto2.setNome("Admin");
        dto2.setDescrizione("Desc");
        dto2.setLivelloVisibilita(1);

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());

        dto2.setId(2L);
        assertNotEquals(dto1, dto2);
    }

    @Test
    void testToString() {
        AuthGroupDTO dto = new AuthGroupDTO();
        dto.setId(1L);
        dto.setNome("Test");
        dto.setDescrizione("Desc");
        String result = dto.toString();

        assertTrue(result.contains("Test"));
        assertTrue(result.contains("Desc"));
    }

    @Test
    void testValidationNotEmptyAndSize() {
        AuthGroupDTO dto = new AuthGroupDTO();
        dto.setNome(""); // invalid (empty)
        dto.setDescrizione(""); // invalid (empty)

        Set<ConstraintViolation<AuthGroupDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());

        // Now fix fields and test again
        dto.setNome("ValidName");
        dto.setDescrizione("Valid description within size limit");
        violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testNomeAndDescrizioneSizeLimits() {
        AuthGroupDTO dto = new AuthGroupDTO();
        dto.setNome("a".repeat(51)); // invalid (too long)
        dto.setDescrizione("a".repeat(201)); // invalid (too long)

        Set<ConstraintViolation<AuthGroupDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }
}
