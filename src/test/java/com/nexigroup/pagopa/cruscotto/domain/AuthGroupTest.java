package com.nexigroup.pagopa.cruscotto.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;

class AuthGroupTest {

    private AuthGroup authGroup;
    private AuthFunction authFunction;
    private AuthUser authUser;

    @BeforeEach
    void setUp() {
        authGroup = new AuthGroup();
        authFunction = new AuthFunction();
        authUser = new AuthUser();

        // simulate bidirectional relation
        authFunction.setAuthGroups(new HashSet<>());
    }

    @Test
    void testSettersAndGetters() {
        authGroup.setId(1L);
        authGroup.setNome("Admin Group");
        authGroup.setDescrizione("Group for admin users");
        authGroup.setLivelloVisibilita(5);

        assertThat(authGroup.getId()).isEqualTo(1L);
        assertThat(authGroup.getNome()).isEqualTo("Admin Group");
        assertThat(authGroup.getDescrizione()).isEqualTo("Group for admin users");
        assertThat(authGroup.getLivelloVisibilita()).isEqualTo(5);
    }

    @Test
    void testBuilderStyleMethods() {
        authGroup.nome("Operator").descrizione("Operator group");
        assertThat(authGroup.getNome()).isEqualTo("Operator");
        assertThat(authGroup.getDescrizione()).isEqualTo("Operator group");
    }

    @Test
    void testAddAndRemoveAuthFunction() {
        authGroup.addAuthFunction(authFunction);

        assertThat(authGroup.getAuthFunctions()).contains(authFunction);
        assertThat(authFunction.getAuthGroups()).contains(authGroup);

        authGroup.removeAuthFunction(authFunction);

        assertThat(authGroup.getAuthFunctions()).doesNotContain(authFunction);
        assertThat(authFunction.getAuthGroups()).doesNotContain(authGroup);
    }

    @Test
    void testAddAndRemoveAuthUser() {
        authGroup.addAuthUser(authUser);

        assertThat(authGroup.getAuthUsers()).contains(authUser);
        assertThat(authUser.getGroup()).isEqualTo(authGroup);

        authGroup.removeAuthUser(authUser);

        assertThat(authGroup.getAuthUsers()).doesNotContain(authUser);
        assertThat(authUser.getGroup()).isNull();
    }

    @Test
    void testEqualsAndHashCode() {
        AuthGroup g1 = new AuthGroup();
        g1.setId(1L);
        AuthGroup g2 = new AuthGroup();
        g2.setId(1L);

        assertThat(g1).isEqualTo(g2);
        assertThat(g1.hashCode()).isEqualTo(g2.hashCode());

        g2.setId(2L);
        assertThat(g1).isNotEqualTo(g2);
    }

    @Test
    void testToStringContainsFields() {
        authGroup.setId(1L);
        authGroup.setNome("TestGroup");
        authGroup.setDescrizione("Desc");
        authGroup.setLivelloVisibilita(2);

        String toString = authGroup.toString();
        assertThat(toString).contains("TestGroup");
        assertThat(toString).contains("Desc");
        assertThat(toString).contains("2");
    }
}
