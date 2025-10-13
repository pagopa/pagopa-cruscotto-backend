package com.nexigroup.pagopa.cruscotto.service.dto;

import lombok.Data;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.EvaluationType;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * A DTO for the {@link com.nexigroup.pagopa.cruscotto.domain.KpiB1Result} entity.
 */
@Data
public class KpiB1ResultDTO implements Serializable {

    private Long id;

    private Long instanceId;

    private Long instanceModuleId;

    private LocalDate analysisDate;

    private Integer institutionCount;

    private Long transactionCount;

    private BigDecimal institutionTolerance;

    private BigDecimal transactionTolerance;

    private EvaluationType evaluationType;

    private OutcomeStatus outcome;
}