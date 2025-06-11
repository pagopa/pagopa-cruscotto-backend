package com.nexigroup.pagopa.cruscotto.service.dto;

import com.nexigroup.pagopa.cruscotto.domain.enumeration.AnalysisType;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.ModuleStatus;
import java.io.Serializable;
import java.time.ZonedDateTime;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * A DTO for the {@link Module} entity.
 */
@Getter
@Setter
@EqualsAndHashCode
public class ModuleDTO implements Serializable {

    private static final long serialVersionUID = 8436953315566863920L;

    private Long id;

    private String code;

    private String name;

    private String description;

    private AnalysisType analysisType;

    private Boolean allowManualOutcome;

    private ModuleStatus status;

    private Boolean configExcludePlannedShutdown;

    private Boolean configExcludeUnplannedShutdown;

    private Boolean configEligibilityThreshold;

    private Boolean configTolerance;

    private Boolean configAverageTimeLimit;

    private Boolean configEvaluationType;

    private boolean deleted;

    private ZonedDateTime deletedDate;
}
