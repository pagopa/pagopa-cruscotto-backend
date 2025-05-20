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
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

/**
 * A KpiA1AnalyticData.
 */

@Entity
@Table(name = "KPI_A1_ANALYTIC_DATA")
@Getter
@Setter
@DynamicUpdate
@DynamicInsert
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class KpiA1AnalyticData implements Serializable {

    private static final long serialVersionUID = 7810237102590802078L;

    @Id
    @Column(name = "CO_ID")
    @SequenceGenerator(name = "SQDASH_KPIA1ANADATA01", sequenceName = "SQDASH_KPIA1ANADATA01", allocationSize = 1)
    @GeneratedValue(generator = "SQDASH_KPIA1ANADATA01", strategy = GenerationType.SEQUENCE)
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

    @NotNull
    @Column(name = "DT_ANALISYS_DATE", nullable = false)
    private LocalDate analysisDate;

    @JsonIgnore
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CO_STATION_ID", nullable = false)
    private AnagStation station;

    @Size(min = 1, max = 255)
    @NotNull
    @Column(name = "TE_METHOD", length = 255, nullable = false)
    private String method;

    @NotNull
    @Column(name = "DT_EVALUATION_DATE", nullable = false)
    private LocalDate evaluationDate;

    @NotNull
    @Column(name = "CO_TOT_REQ", nullable = false)
    private Long totReq;

    @NotNull
    @Column(name = "CO_REQ_OK", nullable = false)
    private Long reqOk;

    @NotNull
    @Column(name = "CO_REQ_TIMEOUT_REAL", nullable = false)
    private Long reqTimeoutReal;

    @NotNull
    @Column(name = "CO_REQ_TIMEOUT_VALID", nullable = false)
    private Long reqTimeoutValid;

    @JsonIgnore
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CO_KPI_A1_DETAIL_RESULT_ID", nullable = false)
    private KpiA1Result kpiA1DetailResult;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof KpiA1AnalyticData that)) return false;

        return new EqualsBuilder().append(id, that.id).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(id).toHashCode();
    }

    @Override
    public String toString() {
        return "KpiA1AnalyticData{" +
            "id=" + id +
            ", instance=" + instance +
            ", instanceModule=" + instanceModule +
            ", analysisDate=" + analysisDate +
            ", station=" + station +
            ", method='" + method + '\'' +
            ", evaluationDate=" + evaluationDate +
            ", totReq=" + totReq +
            ", reqOk=" + reqOk +
            ", reqTimeoutReal=" + reqTimeoutReal +
            ", reqTimeoutValid=" + reqTimeoutValid +
            ", kpiA1DetailResult=" + kpiA1DetailResult +
            '}';
    }
}
