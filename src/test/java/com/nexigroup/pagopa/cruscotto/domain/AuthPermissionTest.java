package com.nexigroup.pagopa.cruscotto.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class AuthPermissionTest {

    private AuthPermission permission;
    private AuthFunction function;

    @BeforeEach
    void setUp() {
        permission = new AuthPermission();
        function = new AuthFunction();
        function.setId(1L);
    }

    @Test
    void testGettersAndSetters() {
        permission.setId(100L);
        permission.setNome("READ_PERMISSION");
        permission.setModulo("USER_MODULE");

        assertThat(permission.getId()).isEqualTo(100L);
        assertThat(permission.getNome()).isEqualTo("READ_PERMISSION");
        assertThat(permission.getModulo()).isEqualTo("USER_MODULE");
    }

    @Test
    void testBuilderStyleMethods() {
        AuthPermission built = new AuthPermission()
            .nome("WRITE_PERMISSION")
            .modulo("ADMIN");

        assertThat(built.getNome()).isEqualTo("WRITE_PERMISSION");
        assertThat(built.getModulo()).isEqualTo("ADMIN");
    }

    @Test
    void testAddAuthFunction() {
        permission.addAuthFunction(function);

        assertThat(permission.getAuthFunctions()).contains(function);
        assertThat(function.getAuthPermissions()).contains(permission);
    }

    @Test
    void testRemoveAuthFunction() {
        permission.addAuthFunction(function);
        permission.removeAuthFunction(function);

        assertThat(permission.getAuthFunctions()).doesNotContain(function);
        assertThat(function.getAuthPermissions()).doesNotContain(permission);
    }

    @Test
    void testSetAuthFunctions() {
        Set<AuthFunction> functions = new HashSet<>();
        functions.add(function);

        permission.setAuthFunctions(functions);
        assertThat(permission.getAuthFunctions()).contains(function);
    }

    @Test
    void testEqualsAndHashCode() {
        AuthPermission p1 = new AuthPermission();
        p1.setId(1L);
        AuthPermission p2 = new AuthPermission();
        p2.setId(1L);

        assertThat(p1).isEqualTo(p2);
        assertThat(p1.hashCode()).isEqualTo(p2.hashCode());

        p2.setId(2L);
        assertThat(p1).isNotEqualTo(p2);
    }

    @Test
    void testEqualsWithNullId() {
        AuthPermission p1 = new AuthPermission();
        AuthPermission p2 = new AuthPermission();
        p2.setId(1L);

        assertThat(p1).isNotEqualTo(p2);
    }

    @Test
    void testToString() {
        permission.setId(5L);
        permission.setNome("DELETE_PERMISSION");
        permission.setModulo("ADMIN");

        String str = permission.toString();
        assertThat(str).contains("DELETE_PERMISSION").contains("ADMIN").contains("5");
    }
}
