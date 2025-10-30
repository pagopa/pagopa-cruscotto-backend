package com.nexigroup.pagopa.cruscotto.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.SpontaneousPayments;
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
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

/**
 * A KpiB5AnalyticDrillDown.
 * Contains snapshot of pagopa_spontaneous data at the time of analysis.
 */
@Entity
@Table(name = "KPI_B5_ANALYTIC_DRILL_DOWN")
@Getter
@Setter
@DynamicUpdate
@DynamicInsert
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class KpiB5AnalyticDrillDown implements Serializable {

    private static final long serialVersionUID = -6090145346504076144L;

    @Id
    @Column(name = "CO_ID")
    @SequenceGenerator(name = "SQCRUSC8_KPIB5ANALDD", sequenceName = "SQCRUSC8_KPIB5ANALDD", allocationSize = 1)
    @GeneratedValue(generator = "SQCRUSC8_KPIB5ANALDD", strategy = GenerationType.SEQUENCE)
    private Long id;

    @JsonIgnore
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CO_KPI_B5_ANALYTIC_DATA_ID", nullable = false)
    private KpiB5AnalyticData kpiB5AnalyticData;

    @Column(name = "CO_PARTNER_ID")
    private Long partnerId;

    @Column(name = "TE_PARTNER_NAME", length = 255)
    private String partnerName;

    @NotNull
    @Column(name = "TE_PARTNER_FISCAL_CODE", nullable = false, length = 255)
    private String partnerFiscalCode;

    @NotNull
    @Column(name = "TE_STATION_CODE", nullable = false, length = 255)
    private String stationCode;

    @NotNull
    @Column(name = "TE_FISCAL_CODE", nullable = false, length = 255)
    private String fiscalCode;

    @NotNull
    @Column(name = "FL_SPONTANEOUS_PAYMENT", nullable = false)
    private Boolean spontaneousPayment;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "TE_SPONTANEOUS_PAYMENTS", nullable = false, length = 20)
    private SpontaneousPayments spontaneousPayments;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof KpiB5AnalyticDrillDown that)) return false;

        return new EqualsBuilder().append(id, that.id).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(id).toHashCode();
    }

    @Override
    public String toString() {
        return (
            "KpiB5AnalyticDrillDown{" +
            "id=" +
            id +
            ", kpiB5AnalyticData=" +
            kpiB5AnalyticData +
            ", partnerId=" +
            partnerId +
            ", partnerName='" +
            partnerName + '\'' +
            ", partnerFiscalCode='" +
            partnerFiscalCode + '\'' +
            ", stationCode='" +
            stationCode + '\'' +
            ", fiscalCode='" +
            fiscalCode + '\'' +
            ", spontaneousPayment=" +
            spontaneousPayment +
            ", spontaneousPayments=" +
            spontaneousPayments +
            '}'
        );
    }
}