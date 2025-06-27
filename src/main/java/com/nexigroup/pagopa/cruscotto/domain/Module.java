package com.nexigroup.pagopa.cruscotto.domain;

import com.nexigroup.pagopa.cruscotto.domain.enumeration.AnalysisType;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.ModuleStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.time.ZonedDateTime;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

/**
 * A Module.
 */
@Entity
@Table(name = "MODULE")
@Getter
@Setter
@DynamicUpdate
@DynamicInsert
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Module extends AbstractAuditingEntity<Long> implements Serializable {

    private static final long serialVersionUID = 5160338151158081264L;

    @Id
    @Column(name = "CO_ID")
    @SequenceGenerator(name = "SQCRUSC8_MODU", sequenceName = "SQCRUSC8_MODU", allocationSize = 1)
    @GeneratedValue(generator = "SQCRUSC8_MODU", strategy = GenerationType.SEQUENCE)
    private Long id;

    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "TE_CODE", length = 50, nullable = false)
    private String code;

    @NotNull
    @Size(min = 1, max = 256)
    @Column(name = "TE_NAME", length = 256, nullable = false)
    private String name;

    @NotNull
    @Size(min = 1, max = 2048)
    @Column(name = "TE_DESCRIPTION", length = 2048, nullable = false)
    private String description;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "TE_ANALYSIS_TYPE", nullable = false)
    private AnalysisType analysisType;

    @NotNull
    @Column(name = "FL_ALLOW_MANUAL_OUTCOME", nullable = false)
    private boolean allowManualOutcome;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "TE_STATUS", nullable = false)
    private ModuleStatus status;

    @NotNull
    @Column(name = "FL_CONFIG_EXCLUDE_PLANNED_SHUTDOWN", nullable = false)
    private Boolean configExcludePlannedShutdown;

    @NotNull
    @Column(name = "FL_CONFIG_EXCLUDE_UNPLANNED_SHUTDOWN", nullable = false)
    private Boolean configExcludeUnplannedShutdown;

    @NotNull
    @Column(name = "FL_CONFIG_ELIGIBILITY_THRESHOLD", nullable = false)
    private Boolean configEligibilityThreshold;

    @NotNull
    @Column(name = "fl_config_tolerance", nullable = false)
    private Boolean configTolerance;

    @NotNull
    @Column(name = "FL_CONFIG_AVERAGE_TIME_LIMIT", nullable = false)
    private Boolean configAverageTimeLimit;

    @NotNull
    @Column(name = "FL_CONFIG_EVALUATION_TYPE", nullable = false)
    private Boolean configEvaluationType;

    @Column(name = "FL_DELETED", nullable = false)
    private boolean deleted;

    @Column(name = "DT_DELETED_DATE")
    private ZonedDateTime deletedDate = null;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Module)) {
            return false;
        }
        return id != null && id.equals(((Module) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }
}
