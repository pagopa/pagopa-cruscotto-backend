package com.nexigroup.pagopa.cruscotto.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

class AuthFunctionTest {

    private AuthFunction authFunction;

    @BeforeEach
    void setUp() {
        authFunction = new AuthFunction();
    }

    @Test
    void testGettersAndSetters() {
        authFunction.setId(1L);
        authFunction.setNome("TestNome");
        authFunction.setModulo("TestModulo");
        authFunction.setDescrizione("TestDescrizione");

        assertEquals(1L, authFunction.getId());
        assertEquals("TestNome", authFunction.getNome());
        assertEquals("TestModulo", authFunction.getModulo());
        assertEquals("TestDescrizione", authFunction.getDescrizione());
    }

    @Test
    void testBuilderMethods() {
        authFunction
            .nome("BuilderNome")
            .modulo("BuilderModulo")
            .descrizione("BuilderDescrizione");

        assertEquals("BuilderNome", authFunction.getNome());
        assertEquals("BuilderModulo", authFunction.getModulo());
        assertEquals("BuilderDescrizione", authFunction.getDescrizione());
    }

    @Test
    void testAuthPermissions() {
        AuthPermission perm1 = new AuthPermission();
        perm1.setId(10L);

        AuthPermission perm2 = new AuthPermission();
        perm2.setId(20L);

        // Add permission
        authFunction.addAuthPermission(perm1);
        assertTrue(authFunction.getAuthPermissions().contains(perm1));
        assertTrue(perm1.getAuthFunctions().contains(authFunction));

        // Add another permission
        authFunction.addAuthPermission(perm2);
        assertEquals(2, authFunction.getAuthPermissions().size());

        // Remove permission
        authFunction.removeAuthPermission(perm1);
        assertFalse(authFunction.getAuthPermissions().contains(perm1));
        assertFalse(perm1.getAuthFunctions().contains(authFunction));

        // Replace whole set
        HashSet<AuthPermission> newSet = new HashSet<>();
        newSet.add(perm2);
        authFunction.setAuthPermissions(newSet);
        assertEquals(1, authFunction.getAuthPermissions().size());
    }

    @Test
    void testAuthGroups() {
        AuthGroup group1 = new AuthGroup();
        group1.setId(100L);

        AuthGroup group2 = new AuthGroup();
        group2.setId(200L);

        // Add group
        authFunction.addAuthGroup(group1);
        assertTrue(authFunction.getAuthGroups().contains(group1));
        assertTrue(group1.getAuthFunctions().contains(authFunction));

        // Add another
        authFunction.addAuthGroup(group2);
        assertEquals(2, authFunction.getAuthGroups().size());

        // Remove group
        authFunction.removeAuthGroup(group1);
        assertFalse(authFunction.getAuthGroups().contains(group1));
        assertFalse(group1.getAuthFunctions().contains(authFunction));

        // Replace whole set
        HashSet<AuthGroup> newGroups = new HashSet<>();
        newGroups.add(group2);
        authFunction.setAuthGroups(newGroups);
        assertEquals(1, authFunction.getAuthGroups().size());
    }

    @Test
    void testEqualsAndHashCode() {
        AuthFunction func1 = new AuthFunction();
        func1.setId(1L);

        AuthFunction func2 = new AuthFunction();
        func2.setId(1L);

        AuthFunction func3 = new AuthFunction();
        func3.setId(2L);

        assertEquals(func1, func2);
        assertNotEquals(func1, func3);
        assertNotEquals(func1, null);
        assertNotEquals(func1, new Object());
        assertEquals(func1.hashCode(), func2.hashCode());
    }

    @Test
    void testToStringContainsFields() {
        authFunction.setId(5L);
        authFunction.setNome("MyFunction");
        authFunction.setModulo("MyModule");
        authFunction.setDescrizione("Description");

        String result = authFunction.toString();
        assertTrue(result.contains("MyFunction"));
        assertTrue(result.contains("MyModule"));
        assertTrue(result.contains("Description"));
    }
}
