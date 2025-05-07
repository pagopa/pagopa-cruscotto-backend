package com.nexigroup.pagopa.cruscotto.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.InstanceStatus;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * A Instance.
 */
@Entity
@Table(name = "INSTANCE")
@Getter
@Setter
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Instance extends AbstractAuditingEntity<Long> implements Serializable {

    private static final long serialVersionUID = 4983447333260106318L;

    @Id
    @Column(name = "CO_ID")
    @SequenceGenerator(name = "SQDASH_INST02", sequenceName = "SQDASH_INST02", allocationSize = 1)
    @GeneratedValue(generator = "SQDASH_INST02", strategy = GenerationType.SEQUENCE)
    private Long id;

    @NotNull
    @Size(min = 1, max = 35)
    @Column(name = "TE_INSTANCE_IDENTIFICATION", length = 35, nullable = false)
    private String instanceIdentification;

    @JsonIgnore
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CO_PARTNER_ID", nullable = false)
    private AnagPartner partner;

    @NotNull
    @Column(name = "DT_PREDICTED_DATE_ANALYSIS", nullable = false)
    private LocalDate predictedDateAnalysis;

    @NotNull
    @Column(name = "DT_APPLICATION_DATE", nullable = false)
    private Instant applicationDate;

    @JsonIgnore
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CO_ASSIGNED_USER_ID", nullable = false)
    private AuthUser assignedUser;

    @NotNull
    @Column(name = "DT_ANALISYS_PERIOD_START_DATE", nullable = false)
    private LocalDate analysisPeriodStartDate;

    @NotNull
    @Column(name = "DT_ANALISYS_PERIOD_END_DATE", nullable = false)
    private LocalDate analysisPeriodEndDate;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "TE_STATUS", nullable = false)
    private InstanceStatus status;

    @Column(name = "DT_LAST_ANALISYS_DATE")
    private Instant lastAnalysisDate;
    
    @JsonIgnore
    @OneToMany(mappedBy = "instance", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<InstanceModule> instanceModules = new HashSet<>();

    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Instance)) {
            return false;
        }
        return id != null && id.equals(((Instance) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "Instance{" +
            "id=" + id +
            ", instanceIdentification='" + instanceIdentification + '\'' +
            ", partner=" + partner +
            ", predictedDateAnalysis=" + predictedDateAnalysis +
            ", applicationDate=" + applicationDate +
            ", assignedUser=" + assignedUser +
            ", analysisPeriodStartDate=" + analysisPeriodStartDate +
            ", analysisPeriodEndDate=" + analysisPeriodEndDate +
            ", status=" + status +
            ", lastAnalysisDate=" + lastAnalysisDate +
            "} " + super.toString();
    }
}
