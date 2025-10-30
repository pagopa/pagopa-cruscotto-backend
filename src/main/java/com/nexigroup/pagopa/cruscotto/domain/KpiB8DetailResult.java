package com.nexigroup.pagopa.cruscotto.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.EvaluationType;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * A KpiB8DetailResult.
 *
 * Represents aggregated results for KPI B.3 "Zero Incident" calculation at partner level.
 * This entity stores aggregated incident counts per evaluation period (monthly/total).
 * Station-specific detailed data is stored in KpiB8AnalyticData for drill-down analysis.
 */

@Entity
@Table(name = "KPI_B8_DETAIL_RESULT")
@Getter
@Setter
@DynamicUpdate
@DynamicInsert
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class KpiB8DetailResult implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "CO_ID")
    @SequenceGenerator(name = "SQCRUSC8_KPIB8DETARESU", sequenceName = "SQCRUSC8_KPIB8DETARESU", allocationSize = 1)
    @GeneratedValue(generator = "SQCRUSC8_KPIB8DETARESU", strategy = GenerationType.SEQUENCE)
    private Long id;

    @JsonIgnore
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CO_INSTANCE_ID", nullable = false, insertable = false, updatable = false)
    private Instance instance;

    @NotNull
    @Column(name = "CO_INSTANCE_ID", nullable = false)
    private Long instanceId;

    @JsonIgnore
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CO_INSTANCE_MODULE_ID", nullable = false, insertable = false, updatable = false)
    private InstanceModule instanceModule;

    @NotNull
    @Column(name = "CO_INSTANCE_MODULE_ID", nullable = false)
    private Long instanceModuleId;

    @JsonIgnore
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CO_ANAG_STATION_ID", nullable = false, insertable = false, updatable = false)
    private AnagStation anagStation;

    @NotNull
    @Column(name = "CO_ANAG_STATION_ID", nullable = false)
    private Long anagStationId;

    @NotNull
    @Column(name = "DT_ANALISYS_DATE", nullable = false)
    private LocalDate analysisDate;

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
    @Column(name = "CO_SUM_REQ_KO", nullable = false)
    private Long reqKO;

    @NotNull
    @Column(name = "CO_PER_REQ_KO", nullable = false)
    private BigDecimal perKO;


    @Enumerated(EnumType.STRING)
    @Column(name = "TE_OUTCOME")
    private OutcomeStatus outcome;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CO_KPI_B8_RESULT_ID")
    private KpiB8Result kpiB8Result;

    // JHipster needle - entity add field

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof KpiB8DetailResult)) {
            return false;
        }
        return id != null && id.equals(((KpiB8DetailResult) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "KpiB8DetailResult{" +
            "id=" + id +
            ", instance=" + instance +
            ", instanceId=" + instanceId +
            ", instanceModule=" + instanceModule +
            ", instanceModuleId=" + instanceModuleId +
            ", anagStationId=" + anagStationId +
            ", analysisDate=" + analysisDate +
            ", evaluationType=" + evaluationType +
            ", evaluationStartDate=" + evaluationStartDate +
            ", evaluationEndDate=" + evaluationEndDate +
            ", totReq=" + totReq +
            ", reqKO=" + reqKO +
            ", perKO=" + perKO +
            ", outcome=" + outcome +
            ", kpiB8Result=" + kpiB8Result +
            '}';
    }
}
