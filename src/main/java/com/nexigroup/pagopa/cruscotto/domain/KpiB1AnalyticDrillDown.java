package com.nexigroup.pagopa.cruscotto.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
 * A KpiB1AnalyticDrillDown.
 */
@Entity
@Table(name = "KPI_B1_ANALYTIC_DRILL_DOWN")
@Getter
@Setter
@DynamicUpdate
@DynamicInsert
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class KpiB1AnalyticDrillDown implements Serializable {

    private static final long serialVersionUID = -6090145346504076144L;

    @Id
    @Column(name = "CO_ID")
    @SequenceGenerator(name = "SQCRUSC8_KPIB1ANALDR", sequenceName = "SQCRUSC8_KPIB1ANALDR", allocationSize = 1)
    @GeneratedValue(generator = "SQCRUSC8_KPIB1ANALDR", strategy = GenerationType.SEQUENCE)
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
    @Size(min = 1, max = 35)
    @Column(name = "TE_PARTNER_CODE", length = 35, nullable = false)
    private String partnerCode;

    @Size(max = 100)
    @Column(name = "TE_PARTNER_NAME", length = 100)
    private String partnerName;

    @NotNull
    @Size(min = 1, max = 35)
    @Column(name = "TE_ENTITY_CODE", length = 35, nullable = false)
    private String entityCode;

    @Size(max = 100)
    @Column(name = "TE_ENTITY_NAME", length = 100)
    private String entityName;

    @NotNull
    @Column(name = "DT_EVALUATION_DATE", nullable = false)
    private LocalDate evaluationDate;

    @NotNull
    @Column(name = "CO_TRANSACTION_COUNT", nullable = false)
    private Long transactionCount;

    @Column(name = "CO_TRANSACTION_AMOUNT", precision = 19, scale = 2)
    private BigDecimal transactionAmount;

    @Column(name = "CO_AVERAGE_TRANSACTION_AMOUNT", precision = 19, scale = 2)
    private BigDecimal averageTransactionAmount;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof KpiB1AnalyticDrillDown that)) return false;

        return new EqualsBuilder().append(id, that.id).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(id).toHashCode();
    }

    @Override
    public String toString() {
        return (
            "KpiB1AnalyticDrillDown{" +
            "id=" +
            id +
            ", instance=" +
            instance +
            ", instanceModule=" +
            instanceModule +
            ", analysisDate=" +
            analysisDate +
            ", partnerCode='" +
            partnerCode + '\'' +
            ", partnerName='" +
            partnerName + '\'' +
            ", entityCode='" +
            entityCode + '\'' +
            ", entityName='" +
            entityName + '\'' +
            ", evaluationDate=" +
            evaluationDate +
            ", transactionCount=" +
            transactionCount +
            ", transactionAmount=" +
            transactionAmount +
            ", averageTransactionAmount=" +
            averageTransactionAmount +
            '}'
        );
    }
}