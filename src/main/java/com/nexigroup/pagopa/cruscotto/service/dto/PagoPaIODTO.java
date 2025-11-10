package com.nexigroup.pagopa.cruscotto.service.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A DTO for the {@link com.nexigroup.pagopa.cruscotto.domain.PagopaIO} entity.
 */
public class PagoPaIODTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    @Size(max = 35)
    private String cfPartner;

    @NotNull
    @Size(max = 35)
    private String ente;

    @NotNull
    private LocalDate data;

    @NotNull
    private Integer numeroPosizioni;

    @NotNull
    private Integer numeroMessaggi;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCfPartner() {
        return cfPartner;
    }

    public void setCfPartner(String cfPartner) {
        this.cfPartner = cfPartner;
    }

    public String getEnte() {
        return ente;
    }

    public void setEnte(String ente) {
        this.ente = ente;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public Integer getNumeroPosizioni() {
        return numeroPosizioni;
    }

    public void setNumeroPosizioni(Integer numeroPosizioni) {
        this.numeroPosizioni = numeroPosizioni;
    }

    public Integer getNumeroMessaggi() {
        return numeroMessaggi;
    }

    public void setNumeroMessaggi(Integer numeroMessaggi) {
        this.numeroMessaggi = numeroMessaggi;
    }

    /**
     * Calculate the percentage of messages vs positions
     */
    public double getPercentualeMessaggi() {
        // Allineamento alla logica del dominio (PagopaIO.getPercentualeMessaggi):
        // - Se numero posizioni == 0 e numero messaggi == 0 => 0% (assenza di attività)
        // - Se numero posizioni == 0 ma ci sono messaggi => 100% (messaggi senza limite posizioni)
        // - Altrimenti (messaggi / posizioni) * 100
        if (numeroPosizioni == null || numeroPosizioni == 0) {
            if (numeroMessaggi == null || numeroMessaggi == 0) {
                return 0.0;
            }
            return 100.0;
        }
        if (numeroMessaggi == null) {
            return 0.0;
        }
        return (double) numeroMessaggi / numeroPosizioni * 100.0;
    }

    /**
     * Check if this record meets the tolerance threshold
     */
    public boolean meetsToleranceThreshold(double toleranceThreshold) {
        return getPercentualeMessaggi() >= toleranceThreshold;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PagoPaIODTO)) {
            return false;
        }

        PagoPaIODTO PagoPaIODTO = (PagoPaIODTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, PagoPaIODTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PagoPaIODTO{" +
            "id=" + getId() +
            ", cfPartner='" + getCfPartner() + "'" +
            ", ente='" + getEnte() + "'" +
            ", data='" + getData() + "'" +
            ", numeroPosizioni=" + getNumeroPosizioni() +
            ", numeroMessaggi=" + getNumeroMessaggi() +
            ", percentualeMessaggi=" + getPercentualeMessaggi() +
            "}";
    }
}
