package com.nexigroup.pagopa.cruscotto.domain;

import org.junit.jupiter.api.Test;

import java.io.*;

import static org.assertj.core.api.Assertions.assertThat;

class AuthFunctionAuthPermissionIdTest {

    @Test
    void testGettersAndSetters() {
        AuthFunctionAuthPermissionId id = new AuthFunctionAuthPermissionId();

        id.setFunzione(123L);
        id.setPermesso(456L);

        assertThat(id.getFunzione()).isEqualTo(123L);
        assertThat(id.getPermesso()).isEqualTo(456L);
    }

    @Test
    void testSerialization() throws IOException, ClassNotFoundException {
        AuthFunctionAuthPermissionId id = new AuthFunctionAuthPermissionId();
        id.setFunzione(123L);
        id.setPermesso(456L);

        // Serialize
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(id);
        oos.close();

        // Deserialize
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bais);
        AuthFunctionAuthPermissionId deserialized = (AuthFunctionAuthPermissionId) ois.readObject();

        assertThat(deserialized.getFunzione()).isEqualTo(123L);
        assertThat(deserialized.getPermesso()).isEqualTo(456L);
    }
}
