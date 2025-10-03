package com.nexigroup.pagopa.cruscotto.service.dto;

import com.nexigroup.pagopa.cruscotto.domain.enumeration.EvaluationType;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import java.io.Serializable;
import java.time.LocalDate;
import lombok.Data;

/**
 * A DTO for the {@link com.nexigroup.pagopa.cruscotto.domain.KpiB3DetailResult} entity.
 */
@Data
public class KpiB3DetailResultDTO implements Serializable {

    private Long id;

    private Long instanceId;

    private Long instanceModuleId;

    private Long anagStationId;

    private Long kpiB3ResultId;

    private LocalDate analysisDate;

    private EvaluationType evaluationType;

    private LocalDate evaluationStartDate;

    private LocalDate evaluationEndDate;

    private Integer totalStandIn;

    private OutcomeStatus outcome;

}