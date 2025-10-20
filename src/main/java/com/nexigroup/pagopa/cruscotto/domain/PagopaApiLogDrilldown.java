package com.nexigroup.pagopa.cruscotto.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

/**
 * A PagopaApiLogDrilldown.
 *
 * Stores API log data for KPI B.4 drilldown analysis.
 * This table maintains a snapshot of PAGOPA_API_LOG data at the time of KPI B.4 analysis execution,
 * providing detailed breakdown of API requests by partner, station, and fiscal code.
 */
@Entity
@Table(name = "PAGOPA_API_LOG_DRILLDOWN")
@Getter
@Setter
@DynamicUpdate
@DynamicInsert
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class PagopaApiLogDrilldown implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "CO_ID")
    @SequenceGenerator(name = "SQCRUSC8_PAGOPAAPILOGDRILL", sequenceName = "SQCRUSC8_PAGOPAAPILOGDRILL", allocationSize = 1)
    @GeneratedValue(generator = "SQCRUSC8_PAGOPAAPILOGDRILL", strategy = GenerationType.SEQUENCE)
    private Long id;

    @JsonIgnore
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CO_INSTANCE_ID", nullable = false)
    private Instance instance;

    @JsonIgnore
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CO_INSTANCE_MODULE_ID", nullable = false)
    private InstanceModule instanceModule;

    @JsonIgnore
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CO_STATION_ID", nullable = false)
    private AnagStation station;

    @JsonIgnore
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CO_KPI_B4_ANALYTIC_DATA_ID", nullable = false)
    private KpiB4AnalyticData kpiB4AnalyticData;

    @JsonIgnore
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CO_KPI_B8_ANALYTIC_DATA_ID", nullable = false)
    private KpiB8AnalyticData kpiB8AnalyticData;


    @NotNull
    @Column(name = "DT_ANALYSIS_DATE", nullable = false)
    private LocalDate analysisDate;

    @NotNull
    @Column(name = "DT_DATA_DATE", nullable = false)
    private LocalDate dataDate;

    @NotNull
    @Column(name = "TE_PARTNER_FISCAL_CODE", length = 35, nullable = false)
    private String partnerFiscalCode;

    @NotNull
    @Column(name = "TE_STATION_CODE", length = 35, nullable = false)
    private String stationCode;

    @NotNull
    @Column(name = "TE_FISCAL_CODE", length = 35, nullable = false)
    private String fiscalCode;

    @NotNull
    @Column(name = "TE_API", length = 35, nullable = false)
    private String api;

    @NotNull
    @Column(name = "CO_TOTAL_REQUESTS", nullable = false)
    private Integer totalRequests;

    @NotNull
    @Column(name = "CO_OK_REQUESTS", nullable = false)
    private Integer okRequests;

    @NotNull
    @Column(name = "CO_KO_REQUESTS", nullable = false)
    private Integer koRequests;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof PagopaApiLogDrilldown that)) return false;

        return new EqualsBuilder().append(id, that.id).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(id).toHashCode();
    }

    @Override
    public String toString() {
        return "PagopaApiLogDrilldown{" +
            "id=" + id +
            ", analysisDate=" + analysisDate +
            ", dataDate=" + dataDate +
            ", partnerFiscalCode='" + partnerFiscalCode + '\'' +
            ", stationCode='" + stationCode + '\'' +
            ", fiscalCode='" + fiscalCode + '\'' +
            ", api='" + api + '\'' +
            ", totalRequests=" + totalRequests +
            ", okRequests=" + okRequests +
            ", koRequests=" + koRequests +
            '}';
    }
}
