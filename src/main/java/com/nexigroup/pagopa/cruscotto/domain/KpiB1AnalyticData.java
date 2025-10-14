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
 * A KpiB1AnalyticData.
 */
@Entity
@Table(name = "KPI_B1_ANALYTIC_DATA")
@Getter
@Setter
@DynamicUpdate
@DynamicInsert
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class KpiB1AnalyticData implements Serializable {

    private static final long serialVersionUID = -6090145346504076144L;

    @Id
    @Column(name = "CO_ID")
    @SequenceGenerator(name = "SQCRUSC8_KPIB1ANALDATA", sequenceName = "SQCRUSC8_KPIB1ANALDATA", allocationSize = 1)
    @GeneratedValue(generator = "SQCRUSC8_KPIB1ANALDATA", strategy = GenerationType.SEQUENCE)
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
    @Column(name = "DT_DATA_DATE", nullable = false)
    private LocalDate dataDate;

    @NotNull
    @Column(name = "CO_INSTITUTION_COUNT", nullable = false)
    private Integer institutionCount;

    @NotNull
    @Column(name = "CO_TRANSACTION_COUNT", nullable = false)
    private Integer transactionCount;

    @JsonIgnore
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CO_KPI_B1_DETAIL_RESULT_ID", nullable = false)
    private KpiB1DetailResult kpiB1DetailResult;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof KpiB1AnalyticData that)) return false;

        return new EqualsBuilder().append(id, that.id).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(id).toHashCode();
    }

    @Override
    public String toString() {
        return (
            "KpiB1AnalyticData{" +
            "id=" +
            id +
            ", instance=" +
            instance +
            ", instanceModule=" +
            instanceModule +
            ", analysisDate=" +
            analysisDate +
            ", dataDate=" +
            dataDate +
            ", institutionCount=" +
            institutionCount +
            ", transactionCount=" +
            transactionCount +
            ", kpiB1DetailResult=" +
            kpiB1DetailResult +
            '}'
        );
    }
}