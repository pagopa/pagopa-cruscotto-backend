package com.nexigroup.pagopa.cruscotto.service.filter;

import jakarta.validation.constraints.Size;
import java.io.Serializable;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class AuthPermissionFilter implements Serializable {

    @Size(max = 100)
    private String nome;

    @Size(max = 50)
    private String modulo;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getModulo() {
        return modulo;
    }

    public void setModulo(String modulo) {
        this.modulo = modulo;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("nome", nome).append("modulo", modulo).toString();
    }
}
