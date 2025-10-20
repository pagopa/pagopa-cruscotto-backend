package com.nexigroup.pagopa.cruscotto.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * A KpiB8AnalyticData.
 *
 * Represents individual Stand-In event data for KPI B.3 "Zero Incident" calculation.
 * This entity stores detailed event information for drill-down analysis and debugging.
 */

@Entity
@Table(name = "KPI_B8_ANALYTIC_DATA")
@Getter
@Setter
@DynamicUpdate
@DynamicInsert
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class KpiB8AnalyticData implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "CO_ID")
    @SequenceGenerator(name = "SQCRUSC8_KPIB8ANALDATA", sequenceName = "SQCRUSC8_KPIB8ANALDATA", allocationSize = 1)
    @GeneratedValue(generator = "SQCRUSC8_KPIB8ANALDATA", strategy = GenerationType.SEQUENCE)
    private Long id;

    @NotNull
    @Column(name = "CO_INSTANCE_ID", nullable = false)
    private Long instanceId;

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
    @Column(name = "CO_INSTANCE_MODULE_ID", nullable = false)
    private Long instanceModuleId;


    @NotNull
    @Column(name = "DT_ANALISYS_DATE", nullable = false)
    private LocalDate analysisDate;

    @NotNull
    @Column(name = "DT_EVALUATION_DATE", nullable = false)
    private LocalDate evaluationDate;

    @NotNull
    @Column(name = "CO_TOT_REQ", nullable = false)
    private Long totReq;

    @NotNull
    @Column(name = "CO_REQ_KO", nullable = false)
    private Long reqKO;

    @JsonIgnore
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CO_KPI_B8_DETAIL_RESULT_ID", nullable = false)
    private KpiB8DetailResult kpiB8DetailResult;

    // JHipster needle - entity add field

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof KpiB8AnalyticData)) {
            return false;
        }
        return id != null && id.equals(((KpiB8AnalyticData) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore


    @Override
    public String toString() {
        return "KpiB8AnalyticData{" +
            "id=" + id +
            ", instanceId=" + instanceId +
            ", instance=" + instance +
            ", instanceModule=" + instanceModule +
            ", instanceModuleId=" + instanceModuleId +
            ", analysisDate=" + analysisDate +
            ", evaluationDate=" + evaluationDate +
            ", totReq=" + totReq +
            ", reqKO=" + reqKO +
            ", kpiB8DetailResult=" + kpiB8DetailResult +
            '}';
    }
}
