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
    @JoinColumn(name = "CO_KPI_B1_ANALYTIC_DATA_ID", nullable = false)
    private KpiB1AnalyticData kpiB1AnalyticData;

    @NotNull
    @Column(name = "DT_DATA_DATE", nullable = false)
    private LocalDate dataDate;

    @NotNull
    @Size(min = 1, max = 35)
    @Column(name = "TE_PARTNER_FISCAL_CODE", length = 35, nullable = false)
    private String partnerFiscalCode;

    @NotNull
    @Size(min = 1, max = 35)
    @Column(name = "TE_INSTITUTION_FISCAL_CODE", length = 35, nullable = false)
    private String institutionFiscalCode;

    @NotNull
    @Column(name = "CO_TRANSACTION_COUNT", nullable = false)
    private Integer transactionCount;

    @Size(max = 35)
    @Column(name = "TE_STATION_CODE", length = 35)
    private String stationCode;

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
            ", kpiB1AnalyticData=" +
            kpiB1AnalyticData +
            ", dataDate=" +
            dataDate +
            ", partnerFiscalCode='" +
            partnerFiscalCode + '\'' +
            ", institutionFiscalCode='" +
            institutionFiscalCode + '\'' +
            ", transactionCount=" +
            transactionCount +
            ", stationCode='" +
            stationCode + '\'' +
            '}'
        );
    }
}