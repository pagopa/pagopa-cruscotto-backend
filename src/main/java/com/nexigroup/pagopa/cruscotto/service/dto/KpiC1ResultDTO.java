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
 * DTO per KpiC1Result.
 * Rappresenta i risultati principali del KPI C.1 (Invio notifiche tramite servizio IO)
 * Conforme al contratto OpenAPI
 */
@Data
@NoArgsConstructor
public class KpiC1ResultDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    @NotNull
    private Long instanceId;

    @NotNull
    private Long instanceModuleId;

    @NotNull
    private LocalDate analysisDate;

    @NotNull
    private BigDecimal eligibilityThreshold;

    @NotNull
    private BigDecimal tolerance;

    @NotNull
    private EvaluationType evaluationType;

    @NotNull
    private OutcomeStatus outcome;
}