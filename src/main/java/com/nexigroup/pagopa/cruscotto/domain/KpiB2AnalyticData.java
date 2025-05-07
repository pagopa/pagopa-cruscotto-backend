package com.nexigroup.pagopa.cruscotto.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.time.Instant;
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
 * A KpiB2AnalyticData.
 */
@Entity
@Table(name = "KPI_B2_ANALYTIC_DATA")
@Getter
@Setter
@DynamicUpdate
@DynamicInsert
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class KpiB2AnalyticData implements Serializable {

    private static final long serialVersionUID = 5141899331669875400L;

    @Id
    @Column(name = "CO_ID")
    @SequenceGenerator(name = "SQDASH_KPIB2ANADATA01", sequenceName = "SQDASH_KPIB2ANADATA01", allocationSize = 1)
    @GeneratedValue(generator = "SQDASH_KPIB2ANADATA01", strategy = GenerationType.SEQUENCE)
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
    private Instant analysisDate;

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
    @Column(name = "CO_REQ_TIMEOUT", nullable = false)
    private Long reqTimeout;

    @NotNull
    @Column(name = "CO_AVG_TIME", nullable = false)
    private Double avgTime;

    @NotNull
    @Column(name = "MONTH_WEIGHT", nullable = false)
    private Double monthWeight;

    @NotNull
    @Column(name = "TOTAL_WEIGHT", nullable = false)
    private Double totalWeight;

    @NotNull
    @Column(name = "MONTH_WEIGHT_OVERTIME", nullable = false)
    private Double monthWeightOvertime;

    @NotNull
    @Column(name = "TOTAL_WEIGHT_OVERTIME", nullable = false)
    private Double totalWeightOvertime;

    @JsonIgnore
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CO_KPI_B2_DETAIL_RESULT_ID", nullable = false)
    private KpiB2Result kpiB2DetailResult;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof KpiB2AnalyticData that)) return false;

        return new EqualsBuilder().append(id, that.id).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(id).toHashCode();
    }

    @Override
    public String toString() {
        return (
            "KpiB2AnalyticData{" +
            "id=" +
            id +
            ", instance=" +
            instance +
            ", instanceModule=" +
            instanceModule +
            ", analysisDate=" +
            analysisDate +
            ", station=" +
            station +
            ", method='" +
            method +
            '\'' +
            ", evaluationDate=" +
            evaluationDate +
            ", totReq=" +
            totReq +
            ", reqOk=" +
            reqOk +
            ", reqTimeout=" +
            reqTimeout +
            ", avgTime=" +
            avgTime +
            ", monthWeight=" +
            monthWeight +
            ", totalWeight=" +
            totalWeight +
            ", monthWeightOvertime=" +
            monthWeightOvertime +
            ", totalWeightOvertime=" +
            totalWeightOvertime +
            ", kpiB2DetailResult=" +
            kpiB2DetailResult +
            '}'
        );
    }
}
