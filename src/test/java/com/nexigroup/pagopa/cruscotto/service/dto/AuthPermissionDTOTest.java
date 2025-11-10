package com.nexigroup.pagopa.cruscotto.service.dto;

import org.junit.jupiter.api.Test;

import java.io.*;
import static org.assertj.core.api.Assertions.assertThat;

class AuthPermissionDTOTest {

    @Test
    void testGettersAndSetters() {
        AuthPermissionDTO dto = new AuthPermissionDTO();
        dto.setId(1L);
        dto.setNome("Test Permission");
        dto.setModulo("Test Module");

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getNome()).isEqualTo("Test Permission");
        assertThat(dto.getModulo()).isEqualTo("Test Module");
        assertThat(dto.getType()).isEqualTo("permesso"); // default value
    }

    @Test
    void testEqualsAndHashCode() {
        AuthPermissionDTO dto1 = new AuthPermissionDTO();
        dto1.setId(1L);
        dto1.setNome("Test Permission");
        dto1.setModulo("Test Module");

        AuthPermissionDTO dto2 = new AuthPermissionDTO();
        dto2.setId(1L);
        dto2.setNome("Test Permission");
        dto2.setModulo("Test Module");

        assertThat(dto1).isEqualTo(dto2);
        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());

        dto2.setId(2L);
        assertThat(dto1).isNotEqualTo(dto2);
    }

    @Test
    void testToString() {
        AuthPermissionDTO dto = new AuthPermissionDTO();
        dto.setId(1L);
        dto.setNome("Test Permission");
        dto.setModulo("Test Module");

        String toString = dto.toString();
        assertThat(toString).contains("id=1");
        assertThat(toString).contains("nome=Test Permission");
        assertThat(toString).contains("modulo=Test Module");
        assertThat(toString).contains("type=permesso");
    }

    @Test
    void testSerialization() throws IOException, ClassNotFoundException {
        AuthPermissionDTO dto = new AuthPermissionDTO();
        dto.setId(1L);
        dto.setNome("Test Permission");
        dto.setModulo("Test Module");

        // Serialize
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(dto);
        }

        // Deserialize
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        AuthPermissionDTO deserialized;
        try (ObjectInputStream ois = new ObjectInputStream(bais)) {
            deserialized = (AuthPermissionDTO) ois.readObject();
        }

        assertThat(deserialized).isEqualTo(dto);
    }
}
