package com.nexigroup.pagopa.cruscotto.service.dto;

import java.time.LocalDate;

import org.springframework.lang.NonNull;

/**
 * A DTO for the {@link com.nexigroup.pagopa.cruscotto.domain.KpiB1AnalyticDrillDown} entity.
 */
public class KpiB1AnalyticDrillDownDTO {

    private Long id;

    @NonNull
    private Long kpiB1AnalyticDataId;

    @NonNull
    private String ente;

    @NonNull
    private String stazione;

    @NonNull
    private LocalDate data;

    @NonNull
    private Integer totaleTransazioni;

    public KpiB1AnalyticDrillDownDTO() {
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getKpiB1AnalyticDataId() {
        return kpiB1AnalyticDataId;
    }

    public void setKpiB1AnalyticDataId(Long kpiB1AnalyticDataId) {
        this.kpiB1AnalyticDataId = kpiB1AnalyticDataId;
    }

    public String getEnte() {
        return ente;
    }

    public void setEnte(String ente) {
        this.ente = ente;
    }

    public String getStazione() {
        return stazione;
    }

    public void setStazione(String stazione) {
        this.stazione = stazione;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
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

        KpiB1AnalyticDrillDownDTO that = (KpiB1AnalyticDrillDownDTO) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (kpiB1AnalyticDataId != null ? !kpiB1AnalyticDataId.equals(that.kpiB1AnalyticDataId) : that.kpiB1AnalyticDataId != null) return false;
        if (ente != null ? !ente.equals(that.ente) : that.ente != null) return false;
        if (stazione != null ? !stazione.equals(that.stazione) : that.stazione != null) return false;
        if (data != null ? !data.equals(that.data) : that.data != null) return false;
        return totaleTransazioni != null ? totaleTransazioni.equals(that.totaleTransazioni) : that.totaleTransazioni == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (kpiB1AnalyticDataId != null ? kpiB1AnalyticDataId.hashCode() : 0);
        result = 31 * result + (ente != null ? ente.hashCode() : 0);
        result = 31 * result + (stazione != null ? stazione.hashCode() : 0);
        result = 31 * result + (data != null ? data.hashCode() : 0);
        result = 31 * result + (totaleTransazioni != null ? totaleTransazioni.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "KpiB1AnalyticDrillDownDTO{" +
                "id=" + id +
                ", kpiB1AnalyticDataId=" + kpiB1AnalyticDataId +
                ", ente='" + ente + '\'' +
                ", stazione='" + stazione + '\'' +
                ", data=" + data +
                ", totaleTransazioni=" + totaleTransazioni +
                '}';
    }
}