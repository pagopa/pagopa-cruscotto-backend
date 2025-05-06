package com.nexigroup.pagopa.cruscotto.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.AnalysisOutcome;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.AnalysisType;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.ManualOutcome;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.ModuleStatus;

import java.io.Serializable;
import java.time.Instant;

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
 * A InstanceModule.
 */
@Entity
@Table(name = "INSTANCE_MODULE")
@Getter
@Setter
@DynamicUpdate
@DynamicInsert
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class InstanceModule extends AbstractAuditingEntity<Long> implements Serializable {

	private static final long serialVersionUID = -6261959579283430859L;

	@Id
    @Column(name = "CO_ID")
    @SequenceGenerator(name = "SQDASH_ISMO01", sequenceName = "SQDASH_ISMO01", allocationSize = 1)
    @GeneratedValue(generator = "SQDASH_ISMO01", strategy = GenerationType.SEQUENCE)
    private Long id;
	
    @JsonIgnore
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CO_INSTANCE_ID", nullable = false)
    private Instance instance;

    @JsonIgnore
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CO_MODULE_ID", nullable = false)
    private Module module;
    
    @Column(name = "DT_ANALISYS_DATE")
    private Instant analysisDate;
    
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "TE_ANALYSIS_TYPE", nullable = false)
    private AnalysisType analysisType;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "TE_ANALYSIS_OUTCOME")
    private AnalysisOutcome analysisOutcome;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "TE_MANUAL_OUTCOME")
    private ManualOutcome manualOutcome;
    
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "TE_STATUS", nullable = false)
    private ModuleStatus status;
    
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CO_ASSIGNED_USER_ID", nullable = true)
    private AuthUser assignedUser;    
    
    @Column(name = "DT_MANUAL_OUTCOME_DATE")
    private Instant manualOutcomeDate;
}
