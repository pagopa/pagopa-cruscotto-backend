package com.nexigroup.pagopa.cruscotto.service.dto;

import java.time.LocalDate;

import org.springframework.lang.NonNull;

/**
 * A DTO for the {@link com.nexigroup.pagopa.cruscotto.domain.PagopaTransazioni} entity.
 */
public class PagopaTransazioniDTO {

    private Long id;

    @NonNull
    private String partner;

    @NonNull
    private String ente;

    @NonNull
    private LocalDate data;

    @NonNull
    private String stazione;

    @NonNull
    private Integer totaleTransazioni;

    public PagopaTransazioniDTO() {
    }

    public PagopaTransazioniDTO(String partner, String ente, LocalDate data, String stazione, Integer totaleTransazioni) {
        this.partner = partner;
        this.ente = ente;
        this.data = data;
        this.stazione = stazione;
        this.totaleTransazioni = totaleTransazioni;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPartner() {
        return partner;
    }

    public void setPartner(String partner) {
        this.partner = partner;
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

    public String getStazione() {
        return stazione;
    }

    public void setStazione(String stazione) {
        this.stazione = stazione;
    }

    public Integer getTotaleTransazioni() {
        return totaleTransazioni;
    }

    public void setTotaleTransazioni(Integer totaleTransazioni) {
        this.totaleTransazioni = totaleTransazioni;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PagopaTransazioniDTO that = (PagopaTransazioniDTO) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (partner != null ? !partner.equals(that.partner) : that.partner != null) return false;
        if (ente != null ? !ente.equals(that.ente) : that.ente != null) return false;
        if (data != null ? !data.equals(that.data) : that.data != null) return false;
        if (stazione != null ? !stazione.equals(that.stazione) : that.stazione != null) return false;
        return totaleTransazioni != null ? totaleTransazioni.equals(that.totaleTransazioni) : that.totaleTransazioni == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (partner != null ? partner.hashCode() : 0);
        result = 31 * result + (ente != null ? ente.hashCode() : 0);
        result = 31 * result + (data != null ? data.hashCode() : 0);
        result = 31 * result + (stazione != null ? stazione.hashCode() : 0);
        result = 31 * result + (totaleTransazioni != null ? totaleTransazioni.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "PagopaTransazioniDTO{" +
                "id=" + id +
                ", partner='" + partner + '\'' +
                ", ente='" + ente + '\'' +
                ", data=" + data +
                ", stazione='" + stazione + '\'' +
                ", totaleTransazioni=" + totaleTransazioni +
                '}';
    }
}