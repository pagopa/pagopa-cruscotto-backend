package com.nexigroup.pagopa.cruscotto.service.dto;

import com.nexigroup.pagopa.cruscotto.domain.enumeration.EvaluationType;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * A DTO for the {@link com.nexigroup.pagopa.cruscotto.domain.KpiB3DetailResult} entity.
 */
@Data

public class KpiB8DetailResultDTO implements Serializable {

    @NotNull
    private Long id;

    @NotNull
    private Long instanceId;

    @NotNull
    private Long instanceModuleId;


    @NotNull
    private LocalDate analysisDate;

    @NotNull
    private EvaluationType evaluationType;

    @NotNull
    private LocalDate evaluationStartDate;

    @NotNull
    private LocalDate evaluationEndDate;

    @NotNull
    private Long totReq;

    @NotNull
    private Long reqKO;

    @NotNull
    private BigDecimal perKO;

    private OutcomeStatus outcome;

    private Long kpiB8ResultId;
}
