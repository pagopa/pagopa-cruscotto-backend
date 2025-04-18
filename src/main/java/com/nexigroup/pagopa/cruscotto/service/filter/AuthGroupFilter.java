package com.nexigroup.pagopa.cruscotto.service.filter;

import jakarta.validation.constraints.Size;
import java.io.Serializable;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class AuthGroupFilter implements Serializable {

    @Size(max = 50)
    private String nome;

    @Size(max = 200)
    private String descrizione;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("nome", nome).append("descrizione", descrizione).toString();
    }
}
