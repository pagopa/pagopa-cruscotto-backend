package com.nexigroup.pagopa.cruscotto.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.EvaluationType;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
import lombok.Getter;
import lombok.Setter;

/**
 * A KpiA1DetailResult.
 */

@Entity
@Table(name = "KPI_A1_DETAIL_RESULT")
@Getter
@Setter
@DynamicUpdate
@DynamicInsert
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class KpiA1DetailResult implements Serializable {

	private static final long serialVersionUID = 1569798052441179251L;

	@Id
    @Column(name = "CO_ID")
    @SequenceGenerator(name = "SQDASH_KPIA1DETRES01", sequenceName = "SQDASH_KPIA1DETRES01", allocationSize = 1)
    @GeneratedValue(generator = "SQDASH_KPIA1DETRES01", strategy = GenerationType.SEQUENCE)
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
    @Column(name = "CO_REQ_TIMEOUT", nullable = false)
    private Long reqTimeout;

    @NotNull
    @Column(name = "CO_TIMEOUT_PERCENTAGE", nullable = false)
    private Double timeoutPercentage;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "TE_OUTCOME", nullable = false)
    private OutcomeStatus outcome;

    @JsonIgnore
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CO_KPI_A1_RESULT_ID", nullable = false)
    private KpiA1Result kpiA1Result;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof KpiA1DetailResult that)) return false;

        return new EqualsBuilder().append(id, that.id).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(id).toHashCode();
    }

	@Override
	public String toString() {
		return "KpiA1DetailResult [id=" + id + ", instance=" + instance + ", instanceModule=" + instanceModule
				+ ", analysisDate=" + analysisDate + ", station=" + station + ", method=" + method + ", evaluationType="
				+ evaluationType + ", evaluationStartDate=" + evaluationStartDate + ", evaluationEndDate="
				+ evaluationEndDate + ", totReq=" + totReq + ", reqTimeout=" + reqTimeout + ", timeoutPercentage="
				+ timeoutPercentage + ", outcome=" + outcome + ", kpiA1Result=" + kpiA1Result + "]";
	}

   
}
