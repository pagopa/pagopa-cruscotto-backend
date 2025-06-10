package com.nexigroup.pagopa.cruscotto.service.bean;

import com.nexigroup.pagopa.cruscotto.domain.enumeration.AnalysisType;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.ModuleStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ModuleRequestBean {

    private Long id;

    @NotBlank
    private String code;

    @NotBlank
    private String name;

    @NotBlank
    private String description;

    @NotNull
    private AnalysisType analysisType;

    @NotNull
    private Boolean allowManualOutcome;

    @NotNull
    private ModuleStatus status;

    @NotNull
    private Boolean configAverageTimeLimit;

    @NotNull
    private Boolean configEligibilityThreshold;

    @NotNull
    private Boolean configEvaluationType;

    @NotNull
    private Boolean configExcludePlannedShutdown;

    @NotNull
    private Boolean configExcludeUnplannedShutdown;

    @NotNull
    private Boolean configTolerance;
}
