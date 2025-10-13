package com.nexigroup.pagopa.cruscotto.service.dto;

import java.time.LocalDate;

import org.springframework.lang.NonNull;

/**
 * A DTO for the {@link com.nexigroup.pagopa.cruscotto.domain.PagopaTransaction} entity.
 */
public class PagopaTransactionDTO {

    private Long id;

    @NonNull
    private String cfPartner;

    @NonNull
    private String cfInstitution;

    @NonNull
    private LocalDate date;

    @NonNull
    private String station;

    @NonNull
    private Integer transactionTotal;

    public PagopaTransactionDTO() {
    }

    public PagopaTransactionDTO(String cfPartner, String cfInstitution, LocalDate date, String station, Integer transactionTotal) {
        this.cfPartner = cfPartner;
        this.cfInstitution = cfInstitution;
        this.date = date;
        this.station = station;
        this.transactionTotal = transactionTotal;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCfPartner() {
        return cfPartner;
    }

    public void setCfPartner(String partner) {
        this.cfPartner = partner;
    }

    public String getCfInstitution() {
        return cfInstitution;
    }

    public void setCfInstitution(String ente) {
        this.cfInstitution = ente;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate data) {
        this.date = data;
    }

    public String getStation() {
        return station;
    }

    public void setStation(String stazione) {
        this.station = stazione;
    }

    public Integer getTransactionTotal() {
        return transactionTotal;
    }

    public void setTransactionTotal(Integer totaleTransazioni) {
        this.transactionTotal = totaleTransazioni;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PagopaTransactionDTO that = (PagopaTransactionDTO) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (cfPartner != null ? !cfPartner.equals(that.cfPartner) : that.cfPartner != null) return false;
        if (cfInstitution != null ? !cfInstitution.equals(that.cfInstitution) : that.cfInstitution != null) return false;
        if (date != null ? !date.equals(that.date) : that.date != null) return false;
        if (station != null ? !station.equals(that.station) : that.station != null) return false;
        return transactionTotal != null ? transactionTotal.equals(that.transactionTotal) : that.transactionTotal == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (cfPartner != null ? cfPartner.hashCode() : 0);
        result = 31 * result + (cfInstitution != null ? cfInstitution.hashCode() : 0);
        result = 31 * result + (date != null ? date.hashCode() : 0);
        result = 31 * result + (station != null ? station.hashCode() : 0);
        result = 31 * result + (transactionTotal != null ? transactionTotal.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "PagopaTransazioniDTO{" +
                "id=" + id +
                ", cfPartner='" + cfPartner + '\'' +
                ", cfInstitution='" + cfInstitution + '\'' +
                ", date=" + date +
                ", station='" + station + '\'' +
                ", transactionTotal=" + transactionTotal +
                '}';
    }
}