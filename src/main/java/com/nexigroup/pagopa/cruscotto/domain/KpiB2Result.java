package com.nexigroup.pagopa.cruscotto.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.EvaluationType;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
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
 * A KpiB2Result.
 */
@Entity
@Table(name = "KPI_B2_RESULT")
@Getter
@Setter
@DynamicUpdate
@DynamicInsert
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class KpiB2Result implements Serializable {

    private static final long serialVersionUID = -6090145346504076143L;

    @Id
    @Column(name = "CO_ID")
    @SequenceGenerator(name = "SQDASH_KPIB2RES01", sequenceName = "SQDASH_KPIB2RES01", allocationSize = 1)
    @GeneratedValue(generator = "SQDASH_KPIB2RES01", strategy = GenerationType.SEQUENCE)
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
    @Column(name = "FL_EXCLUDE_PLANNED_SHUTDOWN", nullable = false)
    private Boolean excludePlannedShutdown;

    @NotNull
    @Column(name = "FL_EXCLUDE_UNPLANNED_SHUTDOWN", nullable = false)
    private Boolean excludeUnplannedShutdown;

    @NotNull
    @Column(name = "CO_ELIGIBILITY_THRESHOLD", nullable = false)
    private Double eligibilityThreshold;

    @NotNull
    @Column(name = "CO_TOLLERANCE", nullable = false)
    private Double tollerance;

    @NotNull
    @Column(name = "CO_AVERAGE_TIME_LIMIT", nullable = false)
    private Double averageTimeLimit;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "TE_EVALUATION_TYPE", nullable = false)
    private EvaluationType evaluationType;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "TE_OUTCOME", nullable = false)
    private OutcomeStatus outcome;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof KpiB2Result that)) return false;

        return new EqualsBuilder().append(id, that.id).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(id).toHashCode();
    }

    @Override
    public String toString() {
        return (
            "KpiB2Result{" +
            "id=" +
            id +
            ", instance=" +
            instance +
            ", instanceModule=" +
            instanceModule +
            ", analysisDate=" +
            analysisDate +
            ", excludePlannedShutdown=" +
            excludePlannedShutdown +
            ", excludeUnplannedShutdown=" +
            excludeUnplannedShutdown +
            ", eligibilityThreshold=" +
            eligibilityThreshold +
            ", tollerance=" +
            tollerance +
            ", averageTimeLimit=" +
            averageTimeLimit +
            ", evaluationType=" +
            evaluationType +
            ", outcome=" +
            outcome +
            '}'
        );
    }
}
