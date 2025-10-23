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
 * KPI B.6 Analytic Data - Payment Options (detailed station data)
 * Stores granular data for each station's payment options status
 */
@Entity
@Table(name = "KPI_B6_ANALYTIC_DATA")
@Getter
@Setter
@DynamicUpdate
@DynamicInsert
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class KpiB6AnalyticData implements Serializable {

    private static final long serialVersionUID = -7845123456789012347L;

    @Id
    @Column(name = "CO_ID")
    @SequenceGenerator(name = "SQCRUSC8_KPIB6ANALDATA", sequenceName = "SQCRUSC8_KPIB6ANALDATA", allocationSize = 1)
    @GeneratedValue(generator = "SQCRUSC8_KPIB6ANALDATA", strategy = GenerationType.SEQUENCE)
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
    @JoinColumn(name = "CO_KPI_B6_DETAIL_RESULT_ID", nullable = false)
    private KpiB6DetailResult kpiB6DetailResult;

    @NotNull
    @Column(name = "DT_ANALISYS_DATE", nullable = false)
    private LocalDate analysisDate;

    @NotNull
    @Column(name = "DT_DATA_DATE", nullable = false)
    private LocalDate dataDate;

    @NotNull
    @Column(name = "TE_STATION_CODE", nullable = false, length = 50)
    private String stationCode;

    @NotNull
    @Column(name = "TE_STATION_STATUS", nullable = false, length = 20)
    private String stationStatus;

    @NotNull
    @Column(name = "FL_PAYMENT_OPTIONS_ENABLED", nullable = false)
    private Boolean paymentOptionsEnabled;

    @Column(name = "TE_INSTITUTION_FISCAL_CODE", length = 20)
    private String institutionFiscalCode;

    @Column(name = "TE_PARTNER_FISCAL_CODE", length = 20)
    private String partnerFiscalCode;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof KpiB6AnalyticData that)) return false;

        return new EqualsBuilder().append(id, that.id).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(id).toHashCode();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" +
                "id = " + id + ", " +
                "analysisDate = " + analysisDate + ", " +
                "dataDate = " + dataDate + ", " +
                "stationCode = " + stationCode + ", " +
                "stationStatus = " + stationStatus + ", " +
                "paymentOptionsEnabled = " + paymentOptionsEnabled + ", " +
                "institutionFiscalCode = " + institutionFiscalCode + ", " +
                "partnerFiscalCode = " + partnerFiscalCode + ")";
    }
}