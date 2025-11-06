package com.nexigroup.pagopa.cruscotto.service.dto;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class AuthFunctionDTOTest {

    @Test
    void testGettersAndSetters() {
        AuthFunctionDTO dto = new AuthFunctionDTO();

        dto.setId(1L);
        dto.setNome("TestName");
        dto.setModulo("TestModule");
        dto.setDescrizione("TestDescription");
        dto.setGroupId(10L);
        dto.setSelected(true);

        Set<AuthPermissionDTO> permissions = new HashSet<>();
        AuthPermissionDTO perm = new AuthPermissionDTO();
        permissions.add(perm);
        dto.setAuthPermissions(permissions);

        // Assertions
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getNome()).isEqualTo("TestName");
        assertThat(dto.getModulo()).isEqualTo("TestModule");
        assertThat(dto.getDescrizione()).isEqualTo("TestDescription");
        assertThat(dto.getGroupId()).isEqualTo(10L);
        assertThat(dto.getSelected()).isTrue();
        assertThat(dto.getAuthPermissions()).containsExactly(perm);

        // Default values
        assertThat(dto.getType()).isEqualTo("funzione");
    }

    @Test
    void testEqualsAndHashCode() {
        AuthFunctionDTO dto1 = new AuthFunctionDTO();
        AuthFunctionDTO dto2 = new AuthFunctionDTO();

        dto1.setId(1L);
        dto2.setId(1L);

        assertThat(dto1).isEqualTo(dto2);
        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());

        dto2.setId(2L);
        assertThat(dto1).isNotEqualTo(dto2);
    }

    @Test
    void testToString() {
        AuthFunctionDTO dto = new AuthFunctionDTO();
        dto.setId(1L);
        dto.setNome("TestName");
        dto.setModulo("TestModule");
        dto.setDescrizione("TestDescription");

        String str = dto.toString();
        assertThat(str).contains("1");
        assertThat(str).contains("TestName");
        assertThat(str).contains("TestModule");
        assertThat(str).contains("TestDescription");
    }
}
