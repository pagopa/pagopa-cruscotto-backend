package com.nexigroup.pagopa.cruscotto.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.AnalysisOutcome;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.AnalysisType;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.ModuleStatus;
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
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

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
    @SequenceGenerator(name = "SQCRUSC8_INSTMODU", sequenceName = "SQCRUSC8_INSTMODU", allocationSize = 1)
    @GeneratedValue(generator = "SQCRUSC8_INSTMODU", strategy = GenerationType.SEQUENCE)
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

    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "TE_MODULE_CODE", length = 50, nullable = false)
    private String moduleCode;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "TE_ANALYSIS_TYPE", nullable = false)
    private AnalysisType analysisType;

    @NotNull
    @Column(name = "FL_ALLOW_MANUAL_OUTCOME", nullable = false)
    private boolean allowManualOutcome;

    @Enumerated(EnumType.STRING)
    @Column(name = "TE_AUTOMATIC_OUTCOME")
    private AnalysisOutcome automaticOutcome;

    @Column(name = "DT_AUTOMATIC_OUTCOME_DATE")
    private Instant automaticOutcomeDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "TE_MANUAL_OUTCOME")
    private AnalysisOutcome manualOutcome;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "TE_STATUS", nullable = false)
    private ModuleStatus status;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CO_MANUAL_OUTCOME_USER_ID", nullable = true)
    private AuthUser manualOutcomeUser;

    @Column(name = "DT_MANUAL_OUTCOME_DATE")
    private Instant manualOutcomeDate;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof InstanceModule)) {
            return false;
        }
        return id != null && id.equals(((InstanceModule) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }
}
