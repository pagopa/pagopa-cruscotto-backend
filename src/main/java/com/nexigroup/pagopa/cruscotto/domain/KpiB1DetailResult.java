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
import jakarta.validation.constraints.Size;
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
    @Size(min = 1, max = 35)
    @Column(name = "TE_PARTNER_CODE", length = 35, nullable = false)
    private String partnerCode;

    @NotNull
    @Column(name = "CO_ENTITY_COUNT", nullable = false)
    private Integer entityCount;

    @NotNull
    @Column(name = "CO_TRANSACTION_COUNT", nullable = false)
    private Long transactionCount;

    @NotNull
    @Column(name = "FL_MEETS_ENTITY_THRESHOLD", nullable = false)
    private Boolean meetsEntityThreshold;

    @NotNull
    @Column(name = "FL_MEETS_TRANSACTION_THRESHOLD", nullable = false)
    private Boolean meetsTransactionThreshold;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "TE_OUTCOME", nullable = false)
    private OutcomeStatus outcome;

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
            ", partnerCode='" +
            partnerCode + '\'' +
            ", entityCount=" +
            entityCount +
            ", transactionCount=" +
            transactionCount +
            ", meetsEntityThreshold=" +
            meetsEntityThreshold +
            ", meetsTransactionThreshold=" +
            meetsTransactionThreshold +
            ", outcome=" +
            outcome +
            ", kpiB1Result=" +
            kpiB1Result +
            '}'
        );
    }
}