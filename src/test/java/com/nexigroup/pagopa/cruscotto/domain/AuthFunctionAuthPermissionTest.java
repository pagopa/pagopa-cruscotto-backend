package com.nexigroup.pagopa.cruscotto.domain;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class AuthFunctionAuthPermissionTest {

    @Test
    void testGettersAndSetters() {
        AuthFunction function = new AuthFunction();
        AuthPermission permission = new AuthPermission();

        AuthFunctionAuthPermission entity = new AuthFunctionAuthPermission();
        entity.setFunzione(function);
        entity.setPermesso(permission);

        assertThat(entity.getFunzione()).isEqualTo(function);
        assertThat(entity.getPermesso()).isEqualTo(permission);
    }

    @Test
    void testEqualsAndHashCode_sameObject() {
        AuthFunctionAuthPermission entity = new AuthFunctionAuthPermission();
        assertThat(entity)
            .isEqualTo(entity)
            .hasSameHashCodeAs(entity);
    }

    @Test
    void testEqualsAndHashCode_equalObjects() {
        AuthFunction function = new AuthFunction();
        AuthPermission permission = new AuthPermission();

        AuthFunctionAuthPermission e1 = new AuthFunctionAuthPermission();
        e1.setFunzione(function);
        e1.setPermesso(permission);

        AuthFunctionAuthPermission e2 = new AuthFunctionAuthPermission();
        e2.setFunzione(function);
        e2.setPermesso(permission);

        assertThat(e1)
            .isEqualTo(e2)
            .hasSameHashCodeAs(e2);
    }

    @Test
    void testEqualsAndHashCode_differentObjects() {
        AuthFunction f1 = new AuthFunction();
        AuthFunction f2 = new AuthFunction();
        AuthPermission p1 = new AuthPermission();
        AuthPermission p2 = new AuthPermission();

        AuthFunctionAuthPermission e1 = new AuthFunctionAuthPermission();
        e1.setFunzione(f1);
        e1.setPermesso(p1);

        AuthFunctionAuthPermission e2 = new AuthFunctionAuthPermission();
        e2.setFunzione(f2);
        e2.setPermesso(p2);

        assertThat(e1).isNotEqualTo(e2);
        // hash codes MAY or MAY NOT be equal — we only care about equals() here
    }

    @Test
    void testEquals_nullAndDifferentClass() {
        AuthFunctionAuthPermission entity = new AuthFunctionAuthPermission();
        assertThat(entity.equals(null)).isFalse();
        assertThat(entity.equals("some string")).isFalse();
    }
}
