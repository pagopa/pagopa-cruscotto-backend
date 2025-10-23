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
 * KPI B.6 Result - Payment Options
 * Tracks compliance of payment options across stations
 */
@Entity
@Table(name = "KPI_B6_RESULT")
@Getter
@Setter
@DynamicUpdate
@DynamicInsert
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class KpiB6Result implements Serializable {

    private static final long serialVersionUID = -7845123456789012345L;

    @Id
    @Column(name = "CO_ID")
    @SequenceGenerator(name = "SQCRUSC8_KPIB6RESU", sequenceName = "SQCRUSC8_KPIB6RESU", allocationSize = 1)
    @GeneratedValue(generator = "SQCRUSC8_KPIB6RESU", strategy = GenerationType.SEQUENCE)
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
    @Column(name = "DT_ANALYSIS_DATE", nullable = false)
    private LocalDate analysisDate;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "TE_EVALUATION_TYPE", nullable = false)
    private EvaluationType evaluationType;

    @NotNull
    @Column(name = "CO_TOTAL_ACTIVE_STATIONS", nullable = false)
    private Integer totalActiveStations;

    @NotNull
    @Column(name = "CO_STATIONS_WITH_PAYMENT_OPTIONS", nullable = false)
    private Integer stationsWithPaymentOptions;

    @NotNull
    @Column(name = "PC_PAYMENT_OPTIONS_PERCENTAGE", nullable = false, precision = 10, scale = 6)
    private BigDecimal paymentOptionsPercentage;

    @NotNull
    @Column(name = "PC_TOLERANCE_THRESHOLD", nullable = false, precision = 10, scale = 6)
    private BigDecimal toleranceThreshold;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "TE_OUTCOME", nullable = false)
    private OutcomeStatus outcome;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof KpiB6Result that)) return false;

        return new EqualsBuilder().append(id, that.id).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(id).toHashCode();
    }

    @Override
    public String toString() {
        return (
            "KpiB6Result{" +
            "id=" + id +
            ", analysisDate=" + analysisDate +
            ", evaluationType=" + evaluationType +
            ", totalActiveStations=" + totalActiveStations +
            ", stationsWithPaymentOptions=" + stationsWithPaymentOptions +
            ", paymentOptionsPercentage=" + paymentOptionsPercentage +
            ", toleranceThreshold=" + toleranceThreshold +
            ", outcome=" + outcome +
            '}'
        );
    }
}