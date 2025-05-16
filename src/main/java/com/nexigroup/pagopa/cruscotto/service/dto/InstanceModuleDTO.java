package com.nexigroup.pagopa.cruscotto.service.dto;

import com.nexigroup.pagopa.cruscotto.domain.InstanceModule;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.AnalysisOutcome;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.AnalysisType;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.ModuleStatus;

import java.io.Serializable;
import java.time.Instant;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * A DTO for the {@link InstanceModule} entity.
 */

@Getter
@Setter
@EqualsAndHashCode
public class InstanceModuleDTO implements Serializable {

    private static final long serialVersionUID = -1063577564042796502L;

    private Long id;

    private Long instanceId;

    private Long moduleId;

    private String moduleCode;

    private AnalysisType analysisType;

    private Boolean allowManualOutcome;

    private AnalysisOutcome automaticOutcome;
    
    private Instant automaticOutcomeDate;   

    private AnalysisOutcome manualOutcome;

    private ModuleStatus status;

    private Long assignedUserId;

    private Instant manualOutcomeDate;

    @Override
    public String toString() {
        return (
            "InstanceModuleDTO [id=" +
            id +
            ", instanceId=" +
            instanceId +
            ", moduleId=" +
            moduleId +
            ", moduleCode=" +
            moduleCode +
            ", analysisType=" +
            analysisType +
            ", allowManualOutcome=" +
            allowManualOutcome +
            ", automaticOutcomeDate=" +
            automaticOutcomeDate +
            ", automaticOutcome=" +
            automaticOutcome +
            ", manualOutcome=" +
            manualOutcome +
            ", status=" +
            status +
            ", assignedUserId=" +
            assignedUserId +
            ", manualOutcomeDate=" +
            manualOutcomeDate +
            "]"
        );
    }
}
