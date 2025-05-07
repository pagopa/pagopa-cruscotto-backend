package com.nexigroup.pagopa.cruscotto.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.EvaluationType;

import java.io.Serializable;
import java.util.Objects;

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
import lombok.Getter;
import lombok.Setter;

/**
 * A KpiConfiguration.
 */
@Entity
@Table(name = "KPI_CONFIGURATION")
@Getter
@Setter
@DynamicUpdate
@DynamicInsert
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class KpiConfiguration implements Serializable {

    private static final long serialVersionUID = -7648526018257208858L;

    @Id
    @Column(name = "CO_ID")
    @SequenceGenerator(name = "SQDASH_KPICONF01", sequenceName = "SQDASH_KPICONF01", allocationSize = 1)
    @GeneratedValue(generator = "SQDASH_KPICONF01", strategy = GenerationType.SEQUENCE)
    private Long id;

    @JsonIgnore
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CO_MODULE_ID", nullable = false)
    private Module module;

    @Column(name = "FL_EXCLUDE_PLANNED_SHUTDOWN")
    private Boolean excludePlannedShutdown;

    @Column(name = "FL_EXCLUDE_UNPLANNED_SHUTDOWN")
    private Boolean excludeUnplannedShutdown;

    @Column(name = "CO_ELIGIBILITY_THRESHOLD")
    private Double eligibilityThreshold;

    @Column(name = "CO_TOLLERANCE")
    private Double tollerance;
    
    @Column(name = "CO_AVERAGE_TIME_LIMIT")
    private Double averageTimeLimit;

    @Enumerated(EnumType.STRING)
    @Column(name = "TE_EVALUATION_TYPE")
    private EvaluationType evaluationType;

    
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof KpiConfiguration that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return (
            "KpiConfiguration{" +
            "id=" +
            id +
            ", module=" +
            module +
            ", excludePlannedShutdown=" +
            excludePlannedShutdown +
            ", excludeUnplannedShutdown=" +
            excludeUnplannedShutdown +
            ", eligibilityThreshold=" +
            eligibilityThreshold +
            ", tollerance=" +
            tollerance +
            ", averageTimeLimit=" +
            averageTimeLimit +            
            ", evaluationType=" +
            evaluationType +
            '}'
        );
    }
}
