package com.nexigroup.pagopa.cruscotto.service.dto;

import com.nexigroup.pagopa.cruscotto.domain.enumeration.EvaluationType;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import java.io.Serializable;
import java.time.LocalDate;
import lombok.Data;

/**
 * A DTO for the {@link com.nexigroup.pagopa.cruscotto.domain.KpiB3Result} entity.
 */
@Data
public class KpiB3ResultDTO implements Serializable {

    private Long id;

    private Long instanceId;

    private Long instanceModuleId;

    private LocalDate analysisDate;

    private Boolean excludePlannedShutdown;

    private Boolean excludeUnplannedShutdown;

    private Double eligibilityThreshold;

    private Double tolerance;

    private EvaluationType evaluationType;

    private OutcomeStatus outcome;

}