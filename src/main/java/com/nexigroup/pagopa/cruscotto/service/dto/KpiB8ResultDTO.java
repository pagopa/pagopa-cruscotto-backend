package com.nexigroup.pagopa.cruscotto.service.dto;

import com.nexigroup.pagopa.cruscotto.domain.enumeration.EvaluationType;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * A DTO for the {@link com.nexigroup.pagopa.cruscotto.domain.KpiB3Result} entity.
 */
@Data
public class KpiB8ResultDTO implements Serializable {

    private Long id;

    private Long instanceId;

    private Long instanceModuleId;

    private LocalDate analysisDate;

    private Double eligibilityThreshold;

    private Double tolerance;

    private EvaluationType evaluationType;

    private OutcomeStatus outcome;
}
