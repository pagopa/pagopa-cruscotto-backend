package com.nexigroup.pagopa.cruscotto.service.dto;

import static org.junit.jupiter.api.Assertions.*;

import java.io.*;

import org.junit.jupiter.api.Test;

import com.nexigroup.pagopa.cruscotto.service.validation.ValidationGroups;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.util.Set;

class PartnerIdentificationDTOTest {

    private final Validator validator;

    PartnerIdentificationDTOTest() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testGettersAndSetters() {
        PartnerIdentificationDTO dto = new PartnerIdentificationDTO();
        dto.setId(1L);
        dto.setFiscalCode("ABCDEF12G34H567I");
        dto.setName("Test Partner");

        assertEquals(1L, dto.getId());
        assertEquals("ABCDEF12G34H567I", dto.getFiscalCode());
        assertEquals("Test Partner", dto.getName());
    }

    @Test
    void testSerialization() throws Exception {
        PartnerIdentificationDTO dto = new PartnerIdentificationDTO();
        dto.setId(10L);
        dto.setFiscalCode("SERIAL123");
        dto.setName("Serializable Partner");

        // Serialize object
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(byteOut);
        out.writeObject(dto);
        out.close();

        // Deserialize object
        ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(byteOut.toByteArray()));
        PartnerIdentificationDTO deserialized = (PartnerIdentificationDTO) in.readObject();

        assertNotNull(deserialized);
        assertEquals(dto.getId(), deserialized.getId());
        assertEquals(dto.getFiscalCode(), deserialized.getFiscalCode());
        assertEquals(dto.getName(), deserialized.getName());
    }

    @Test
    void testValidationFailsWhenFiscalCodeIsNull() {
        PartnerIdentificationDTO dto = new PartnerIdentificationDTO();
        dto.setName("Valid Name");

        Set<ConstraintViolation<PartnerIdentificationDTO>> violations =
            validator.validate(dto, ValidationGroups.RegistryJob.class);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
            .anyMatch(v -> v.getPropertyPath().toString().equals("fiscalCode")));
    }

    @Test
    void testValidationFailsWhenNameIsNull() {
        PartnerIdentificationDTO dto = new PartnerIdentificationDTO();
        dto.setFiscalCode("VALID123");

        Set<ConstraintViolation<PartnerIdentificationDTO>> violations =
            validator.validate(dto, ValidationGroups.RegistryJob.class);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
            .anyMatch(v -> v.getPropertyPath().toString().equals("name")));
    }

    @Test
    void testValidationPassesWhenAllFieldsPresent() {
        PartnerIdentificationDTO dto = new PartnerIdentificationDTO();
        dto.setFiscalCode("VALID123");
        dto.setName("Valid Name");

        Set<ConstraintViolation<PartnerIdentificationDTO>> violations =
            validator.validate(dto, ValidationGroups.RegistryJob.class);

        assertTrue(violations.isEmpty());
    }
}
