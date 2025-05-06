package com.nexigroup.pagopa.cruscotto.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.EvaluationType;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
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
 * A KpiB2DetailResult.
 */
@Entity
@Table(name = "KPI_B2_DETAIL_RESULT")
@Getter
@Setter
@DynamicUpdate
@DynamicInsert
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class KpiB2DetailResult implements Serializable {

    private static final long serialVersionUID = -6090145346504076143L;

    @Id
    @Column(name = "CO_ID")
    @SequenceGenerator(name = "SQDASH_KPIB2DETRES01", sequenceName = "SQDASH_KPIB2DETRES01", allocationSize = 1)
    @GeneratedValue(generator = "SQDASH_KPIB2DETRES01", strategy = GenerationType.SEQUENCE)
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
    @Enumerated(EnumType.STRING)
    @Column(name = "TE_EVALUATION_TYPE", nullable = false)
    private EvaluationType evaluationType;

    @NotNull
    @Column(name = "DT_EVALUATION_START_DATE", nullable = false)
    private LocalDate evaluationStartDate;

    @NotNull
    @Column(name = "DT_EVALUATION_END_DATE", nullable = false)
    private LocalDate evaluationEndDate;

    @NotNull
    @Column(name = "CO_TOT_REQ", nullable = false)
    private Long totReq;

    @NotNull
    @Column(name = "CO_AVG_TIME", nullable = false)
    private Double avgTime;

    @NotNull
    @Column(name = "CO_OVER_TIME_LIMIT", nullable = false)
    private Double overTimeLimit;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "TE_OUTCOME", nullable = false)
    private OutcomeStatus outcome;

    @JsonIgnore
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CO_KPI_B2_RESULT_ID", nullable = false)
    private KpiB2Result kpiB2Result;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof KpiB2DetailResult that)) return false;

        return new EqualsBuilder().append(id, that.id).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(id).toHashCode();
    }

    @Override
    public String toString() {
        return (
            "KpiB2DetailResult{" +
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
            ", evaluationType=" +
            evaluationType +
            ", evaluationStartDate=" +
            evaluationStartDate +
            ", evaluationEndDate=" +
            evaluationEndDate +
            ", totReq=" +
            totReq +
            ", avgTime=" +
            avgTime +
            ", overTimeLimit=" +
            overTimeLimit +
            ", outcome=" +
            outcome +
            ", kpiB2Result=" +
            kpiB2Result +
            '}'
        );
    }
}
