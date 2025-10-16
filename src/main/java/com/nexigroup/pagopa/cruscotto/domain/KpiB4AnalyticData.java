package com.nexigroup.pagopa.cruscotto.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
 * A KpiB4AnalyticData.
 * 
 * Represents daily API usage data for KPI B.4 "API Integration" calculation.
 * This entity stores GPD/ACA vs paCreate request counts per day for analysis.
 */

@Entity
@Table(name = "KPI_B4_ANALYTIC_DATA")
@Getter
@Setter
@DynamicUpdate
@DynamicInsert
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class KpiB4AnalyticData implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "CO_ID")
    @SequenceGenerator(name = "SQCRUSC8_KPIB4ANALDATA", sequenceName = "SQCRUSC8_KPIB4ANALDATA", allocationSize = 1)
    @GeneratedValue(generator = "SQCRUSC8_KPIB4ANALDATA", strategy = GenerationType.SEQUENCE)
    private Long id;

    @NotNull
    @Column(name = "TE_INSTANCE_ID", nullable = false)
    private Long instanceId;

    @JsonIgnore
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TE_INSTANCE_ID", nullable = false, insertable = false, updatable = false)
    private Instance instance;

    @NotNull
    @Column(name = "DT_ANALYSIS_DATE", nullable = false)
    private LocalDate analysisDate;

    @NotNull
    @Column(name = "DT_EVALUATION_DATE", nullable = false)
    private LocalDate evaluationDate;

    @Column(name = "CO_NUM_REQUEST_GPD")
    private Long numRequestGpd;

    @Column(name = "CO_NUM_REQUEST_CP")
    private Long numRequestCp;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CO_KPI_B4_DETAIL_RESULT_ID")
    private KpiB4DetailResult kpiB4DetailResult;

    // JHipster needle - entity add field

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof KpiB4AnalyticData)) {
            return false;
        }
        return id != null && id.equals(((KpiB4AnalyticData) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "KpiB4AnalyticData{" +
            "id=" + id +
            ", instanceId=" + instanceId +
            ", analysisDate='" + analysisDate + "'" +
            ", evaluationDate='" + evaluationDate + "'" +
            ", numRequestGpd=" + numRequestGpd +
            ", numRequestCp=" + numRequestCp +
            "}";
    }
}