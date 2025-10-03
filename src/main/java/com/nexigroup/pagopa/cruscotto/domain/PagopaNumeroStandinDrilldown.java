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
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

/**
 * A PagopaNumeroStandinDrilldown.
 * 
 * Stores Stand-In data snapshot for drilldown analysis at the time of KPI B.3 analysis execution.
 * This table maintains a historical snapshot of PAGOPA_NUMERO_STANDIN data to ensure 
 * consistent drilldown results even if the original data changes.
 */
@Entity
@Table(name = "PAGOPA_NUMERO_STANDIN_DRILLDOWN")
@Getter
@Setter
@DynamicUpdate
@DynamicInsert
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class PagopaNumeroStandinDrilldown implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "CO_ID")
    @SequenceGenerator(name = "SQCRUSC8_PAGOPASTANDINDRILLDOWN", sequenceName = "SQCRUSC8_PAGOPASTANDINDRILLDOWN", allocationSize = 1)
    @GeneratedValue(generator = "SQCRUSC8_PAGOPASTANDINDRILLDOWN", strategy = GenerationType.SEQUENCE)
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
    @JoinColumn(name = "CO_KPI_B3_ANALYTIC_DATA_ID", nullable = false)
    private KpiB3AnalyticData kpiB3AnalyticData;

    @NotNull
    @Column(name = "DT_ANALYSIS_DATE", nullable = false)
    private LocalDate analysisDate;

    // Original PagopaNumeroStandin data fields (snapshot)
    @NotNull
    @Size(max = 255)
    @Column(name = "DE_STATION_CODE", length = 255, nullable = false)
    private String stationCode;

    @NotNull
    @Column(name = "DT_INTERVAL_START", nullable = false)
    private LocalDateTime intervalStart;

    @NotNull
    @Column(name = "DT_INTERVAL_END", nullable = false)
    private LocalDateTime intervalEnd;

    @NotNull
    @Column(name = "CO_STAND_IN_COUNT", nullable = false)
    private Integer standInCount;

    @Size(max = 50)
    @Column(name = "DE_EVENT_TYPE", length = 50)
    private String eventType;

    @NotNull
    @Column(name = "DT_DATA_DATE", nullable = false)
    private LocalDateTime dataDate;

    @NotNull
    @Column(name = "DT_DATA_ORA_EVENTO", nullable = false)
    private LocalDateTime dataOraEvento;

    @Column(name = "DT_LOAD_TIMESTAMP")
    private LocalDateTime loadTimestamp;

    // Original PagopaNumeroStandin ID reference for traceability
    @Column(name = "CO_ORIGINAL_STANDIN_ID")
    private Long originalStandinId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof PagopaNumeroStandinDrilldown that)) return false;

        return new EqualsBuilder().append(id, that.id).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(id).toHashCode();
    }

    @Override
    public String toString() {
        return "PagopaNumeroStandinDrilldown{" +
            "id=" + id +
            ", analysisDate=" + analysisDate +
            ", stationCode='" + stationCode + '\'' +
            ", intervalStart=" + intervalStart +
            ", intervalEnd=" + intervalEnd +
            ", standInCount=" + standInCount +
            ", eventType='" + eventType + '\'' +
            ", dataDate=" + dataDate +
            ", dataOraEvento=" + dataOraEvento +
            ", originalStandinId=" + originalStandinId +
            '}';
    }
}