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
 * A KpiB1Result.
 */
@Entity
@Table(name = "KPI_B1_RESULT")
@Getter
@Setter
@DynamicUpdate
@DynamicInsert
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class KpiB1Result implements Serializable {

    private static final long serialVersionUID = -6090145346504076144L;

    @Id
    @Column(name = "CO_ID")
    @SequenceGenerator(name = "SQCRUSC8_KPIB1RESU", sequenceName = "SQCRUSC8_KPIB1RESU", allocationSize = 1)
    @GeneratedValue(generator = "SQCRUSC8_KPIB1RESU", strategy = GenerationType.SEQUENCE)
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
    @Column(name = "CO_INSTITUTION_COUNT", nullable = false)
    private Integer institutionCount;

    @NotNull
    @Column(name = "CO_TRANSACTION_COUNT", nullable = false)
    private Integer transactionCount;

    @NotNull
    @Column(name = "CO_INSTITUTION_TOLERANCE", nullable = false)
    private BigDecimal institutionTolerance;

    @NotNull
    @Column(name = "CO_TRANSACTION_TOLERANCE", nullable = false)
    private BigDecimal transactionTolerance;

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

        if (!(o instanceof KpiB1Result that)) return false;

        return new EqualsBuilder().append(id, that.id).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(id).toHashCode();
    }

    @Override
    public String toString() {
        return (
            "KpiB1Result{" +
            "id=" +
            id +
            ", instance=" +
            instance +
            ", instanceModule=" +
            instanceModule +
            ", analysisDate=" +
            analysisDate +
            ", institutionCount=" +
            institutionCount +
            ", transactionCount=" +
            transactionCount +
            ", institutionTolerance=" +
            institutionTolerance +
            ", transactionTolerance=" +
            transactionTolerance +
            ", evaluationType=" +
            evaluationType +
            ", outcome=" +
            outcome +
            '}'
        );
    }
}