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
 * KPI B.6 Detail Result - Payment Options (monthly/total breakdown)
 * Tracks detailed compliance results per evaluation period
 */
@Entity
@Table(name = "KPI_B6_DETAIL_RESULT")
@Getter
@Setter
@DynamicUpdate
@DynamicInsert
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class KpiB6DetailResult implements Serializable {

    private static final long serialVersionUID = -7845123456789012346L;

    @Id
    @Column(name = "CO_ID")
    @SequenceGenerator(name = "SQCRUSC8_KPIB6DETARESU", sequenceName = "SQCRUSC8_KPIB6DETARESU", allocationSize = 1)
    @GeneratedValue(generator = "SQCRUSC8_KPIB6DETARESU", strategy = GenerationType.SEQUENCE)
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
    @JoinColumn(name = "CO_KPI_B6_RESULT_ID", nullable = false)
    private KpiB6Result kpiB6Result;

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
    @Column(name = "CO_ACTIVE_STATIONS", nullable = false)
    private Integer activeStations;

    @NotNull
    @Column(name = "CO_STATIONS_WITH_PAYMENT_OPTIONS", nullable = false)
    private Integer stationsWithPaymentOptions;

    @NotNull
    @Column(name = "DE_COMPLIANCE_PERCENTAGE", nullable = false, precision = 5, scale = 2)
    private BigDecimal compliancePercentage;

    @Column(name = "CO_STATION_DIFFERENCE")
    private Integer stationDifference;

    @Column(name = "DE_STATION_DIFFERENCE_PERCENTAGE", precision = 5, scale = 2)
    private BigDecimal stationDifferencePercentage;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "TE_OUTCOME", nullable = false)
    private OutcomeStatus outcome;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof KpiB6DetailResult that)) return false;

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
                "evaluationStartDate = " + evaluationStartDate + ", " +
                "evaluationEndDate = " + evaluationEndDate + ", " +
                "activeStations = " + activeStations + ", " +
                "stationsWithPaymentOptions = " + stationsWithPaymentOptions + ", " +
                "compliancePercentage = " + compliancePercentage + ", " +
                "outcome = " + outcome + ")";
    }
}