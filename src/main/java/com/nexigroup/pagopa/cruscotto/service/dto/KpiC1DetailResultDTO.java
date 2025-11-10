package com.nexigroup.pagopa.cruscotto.service.dto;

import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.EvaluationType;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO per KpiC1DetailResult.
 * Rappresenta il dettaglio per singolo ente dei risultati del KPI C.1
 * Conforme al contratto OpenAPI
 */
@Data
@NoArgsConstructor
public class KpiC1DetailResultDTO implements Serializable {

    private static final long serialVersionUID = 1L;

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
    private Long totalInstitutions;

    @NotNull
    private Long okTotalInstitutions;

    @NotNull
    private BigDecimal percentageOkInstitutions;

    @NotNull
    private OutcomeStatus outcome;

    @NotNull
    private Long kpiC1ResultId;
}