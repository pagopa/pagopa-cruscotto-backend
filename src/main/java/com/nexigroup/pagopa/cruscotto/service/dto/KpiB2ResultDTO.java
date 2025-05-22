package com.nexigroup.pagopa.cruscotto.service.dto;

import com.nexigroup.pagopa.cruscotto.domain.KpiB2Result;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.EvaluationType;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import com.nexigroup.pagopa.cruscotto.service.validation.ValidationGroups;

import java.io.Serializable;
import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * A DTO for the {@link KpiB2Result} entity.
 */

@Getter
@Setter
public class KpiB2ResultDTO implements Serializable {

    private static final long serialVersionUID = 1464684720174723448L;
    

    private Long id;

    @NotNull(groups = { ValidationGroups.KpiB2Job.class })
    private Long instanceId;

    @NotNull(groups = { ValidationGroups.KpiB2Job.class })
    private Long instanceModuleId;

    @NotNull(groups = { ValidationGroups.KpiB2Job.class })
    private LocalDate analysisDate;

    @NotNull(groups = { ValidationGroups.KpiB2Job.class })
    private Boolean excludePlannedShutdown;

    @NotNull(groups = { ValidationGroups.KpiB2Job.class })
    private Boolean excludeUnplannedShutdown;

    @NotNull(groups = { ValidationGroups.KpiB2Job.class })
    private Double eligibilityThreshold;

    @NotNull(groups = { ValidationGroups.KpiB2Job.class })
    private Double tollerance;

    @NotNull(groups = { ValidationGroups.KpiB2Job.class })
    private Double averageTimeLimit;

    @NotNull(groups = { ValidationGroups.KpiB2Job.class })
    private EvaluationType evaluationType;

    @NotNull(groups = { ValidationGroups.KpiB2Job.class })
    private OutcomeStatus outcome;
    

    @Override
    public String toString() {
        return (
            "KpiB2ResultDTO [id=" +
            id +
            ", instanceId=" +
            instanceId +
            ", instanceModuleId=" +
            instanceModuleId +
            ", analysisDate=" +
            analysisDate +
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
            ", outcome=" +
            outcome +
            "]"
        );
    }
}
