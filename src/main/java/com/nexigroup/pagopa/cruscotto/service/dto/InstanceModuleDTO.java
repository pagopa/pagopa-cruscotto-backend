package com.nexigroup.pagopa.cruscotto.service.dto;

import com.nexigroup.pagopa.cruscotto.domain.InstanceModule;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.AnalysisOutcome;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.AnalysisType;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.ManualOutcome;
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

    private Instant analysisDate;

    private AnalysisType analysisType;

    private AnalysisOutcome analysisOutcome;

    private ManualOutcome manualOutcome;

    private ModuleStatus status;

    private Long assignedUserId;

    private Instant manualOutcomeDate;
}
