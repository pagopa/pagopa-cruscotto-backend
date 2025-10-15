package com.nexigroup.pagopa.cruscotto.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.EvaluationType;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

/**
 * A KpiB4DetailResult.
 * 
 * Represents detailed results for KPI B.4 "Zero Incident" calculation at station level.
 * This entity stores incident counts and events per station for drill-down analysis.
 */

@Entity
@Table(name = "KPI_B4_DETAIL_RESULT")
@Getter
@Setter
@DynamicUpdate
@DynamicInsert
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class KpiB4DetailResult implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "CO_ID")
    @SequenceGenerator(name = "SQCRUSC8_KPIB4DETARESU", sequenceName = "SQCRUSC8_KPIB4DETARESU", allocationSize = 1)
    @GeneratedValue(generator = "SQCRUSC8_KPIB4DETARESU", strategy = GenerationType.SEQUENCE)
    private Long id;

    @NotNull
    @Column(name = "CO_INSTANCE_ID", nullable = false)
    private Long instanceId;

    @NotNull
    @Column(name = "CO_INSTANCE_MODULE_ID", nullable = false)
    private Long instanceModuleId;

    @NotNull
    @Column(name = "CO_ANAG_STATION_ID", nullable = false)
    private Long anagStationId;

    @JsonIgnore
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CO_INSTANCE_ID", nullable = false, insertable = false, updatable = false)
    private Instance instance;

    @JsonIgnore
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CO_INSTANCE_MODULE_ID", nullable = false, insertable = false, updatable = false)
    private InstanceModule instanceModule;

    @JsonIgnore
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CO_ANAG_STATION_ID", nullable = false, insertable = false, updatable = false)
    private AnagStation anagStation;

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
    @JoinColumn(name = "CO_KPI_B4_RESULT_ID")
    private KpiB4Result kpiB4Result;

    // JHipster needle - entity add field

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof KpiB4DetailResult)) {
            return false;
        }
        return id != null && id.equals(((KpiB4DetailResult) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "KpiB4DetailResult{" +
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