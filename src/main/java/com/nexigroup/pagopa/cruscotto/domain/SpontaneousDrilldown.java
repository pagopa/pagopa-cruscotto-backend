package com.nexigroup.pagopa.cruscotto.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.SpontaneousPayments;
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
import jakarta.persistence.Transient;
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
 * A SpontaneousDrilldown - Snapshot of pagopa_spontaneous data at analysis time.
 * This entity stores historical data for KPI B.5 drilldown analysis.
 */
@Entity
@Table(name = "SPONTANEOUS_DRILLDOWN")
@Getter
@Setter
@DynamicUpdate
@DynamicInsert
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class SpontaneousDrilldown implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "CO_ID")
    @SequenceGenerator(name = "SQCRUSC8_SPONTDD", sequenceName = "SQCRUSC8_SPONTDD", allocationSize = 1)
    @GeneratedValue(generator = "SQCRUSC8_SPONTDD", strategy = GenerationType.SEQUENCE)
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SpontaneousDrilldown)) {
            return false;
        }
        SpontaneousDrilldown that = (SpontaneousDrilldown) o;
        return new EqualsBuilder().append(id, that.id).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(id).toHashCode();
    }

    /**
     * Get the spontaneous payments enum value derived from the boolean field.
     */
    @Transient
    public SpontaneousPayments getSpontaneousPayments() {
        return SpontaneousPayments.fromBoolean(this.spontaneousPayment);
    }

    @Override
    public String toString() {
        return "SpontaneousDrilldown{" +
            "id=" + id +
            ", partnerId=" + partnerId +
            ", partnerName='" + partnerName + '\'' +
            ", partnerFiscalCode='" + partnerFiscalCode + '\'' +
            ", stationCode='" + stationCode + '\'' +
            ", fiscalCode='" + fiscalCode + '\'' +
            ", spontaneousPayment=" + spontaneousPayment +
            '}';
    }
}