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
import java.time.LocalDate;

/**
 * A KpiB3DetailResult.
 *
 * Represents aggregated results for KPI B.3 "Zero Incident" calculation at partner level.
 * This entity stores aggregated incident counts per evaluation period (monthly/total).
 * Station-specific detailed data is stored in KpiB3AnalyticData for drill-down analysis.
 */

@Entity
@Table(name = "KPI_B3_DETAIL_RESULT")
@Getter
@Setter
@DynamicUpdate
@DynamicInsert
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class KpiB8DetailResult implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "CO_ID")
    @SequenceGenerator(name = "SQCRUSC8_KPIB3DETARESU", sequenceName = "SQCRUSC8_KPIB3DETARESU", allocationSize = 1)
    @GeneratedValue(generator = "SQCRUSC8_KPIB3DETARESU", strategy = GenerationType.SEQUENCE)
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

    @Column(name = "CO_TOTAL_STANDIN")
    private Integer totalStandIn;

    @Enumerated(EnumType.STRING)
    @Column(name = "TE_OUTCOME")
    private OutcomeStatus outcome;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CO_KPI_B3_RESULT_ID")
    private KpiB3Result kpiB3Result;

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

    // prettier-ignore
    @Override
    public String toString() {
        return "KpiB3DetailResult{" +
            "id=" + id +
            ", analysisDate='" + analysisDate + "'" +
            ", evaluationType='" + evaluationType + "'" +
            ", evaluationStartDate='" + evaluationStartDate + "'" +
            ", evaluationEndDate='" + evaluationEndDate + "'" +
            ", totalStandIn=" + totalStandIn +
            ", outcome='" + outcome + "'" +
            "}";
    }
}
