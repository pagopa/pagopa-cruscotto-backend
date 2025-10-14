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
import java.math.BigDecimal;
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
 * A KpiB1DetailResult.
 */
@Entity
@Table(name = "KPI_B1_DETAIL_RESULT")
@Getter
@Setter
@DynamicUpdate
@DynamicInsert
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class KpiB1DetailResult implements Serializable {

    private static final long serialVersionUID = -6090145346504076144L;

    @Id
    @Column(name = "CO_ID")
    @SequenceGenerator(name = "SQCRUSC8_KPIB1DETARESU", sequenceName = "SQCRUSC8_KPIB1DETARESU", allocationSize = 1)
    @GeneratedValue(generator = "SQCRUSC8_KPIB1DETARESU", strategy = GenerationType.SEQUENCE)
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

    @NotNull
    @Column(name = "CO_TOTAL_INSTITUTIONS", nullable = false)
    private Integer totalInstitutions;

    @NotNull
    @Column(name = "CO_INSTITUTION_DIFFERENCE", nullable = false)
    private Integer institutionDifference;

    @NotNull
    @Column(name = "VA_INSTITUTION_DIFFERENCE_PERCENTAGE", precision = 19, scale = 2, nullable = false)
    private BigDecimal institutionDifferencePercentage;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "TE_INSTITUTION_OUTCOME", nullable = false)
    private OutcomeStatus institutionOutcome;

    @NotNull
    @Column(name = "CO_TOTAL_TRANSACTIONS", nullable = false)
    private Integer totalTransactions;

    @NotNull
    @Column(name = "CO_TRANSACTION_DIFFERENCE", nullable = false)
    private Integer transactionDifference;

    @NotNull
    @Column(name = "VA_TRANSACTION_DIFFERENCE_PERCENTAGE", precision = 19, scale = 2, nullable = false)
    private BigDecimal transactionDifferencePercentage;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "TE_TRANSACTION_OUTCOME", nullable = false)
    private OutcomeStatus transactionOutcome;

    @JsonIgnore
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CO_KPI_B1_RESULT_ID", nullable = false)
    private KpiB1Result kpiB1Result;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof KpiB1DetailResult that)) return false;

        return new EqualsBuilder().append(id, that.id).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(id).toHashCode();
    }

    @Override
    public String toString() {
        return (
            "KpiB1DetailResult{" +
            "id=" +
            id +
            ", instance=" +
            instance +
            ", instanceModule=" +
            instanceModule +
            ", analysisDate=" +
            analysisDate +
            ", evaluationType=" +
            evaluationType +
            ", evaluationStartDate=" +
            evaluationStartDate +
            ", evaluationEndDate=" +
            evaluationEndDate +
            ", totalInstitutions=" +
            totalInstitutions +
            ", institutionDifference=" +
            institutionDifference +
            ", institutionDifferencePercentage=" +
            institutionDifferencePercentage +
            ", institutionOutcome=" +
            institutionOutcome +
            ", totalTransactions=" +
            totalTransactions +
            ", transactionDifference=" +
            transactionDifference +
            ", transactionDifferencePercentage=" +
            transactionDifferencePercentage +
            ", transactionOutcome=" +
            transactionOutcome +
            ", kpiB1Result=" +
            kpiB1Result +
            '}'
        );
    }
}