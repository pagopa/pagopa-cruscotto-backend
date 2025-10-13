package com.nexigroup.pagopa.cruscotto.service.dto;

import lombok.Data;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.EvaluationType;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * A DTO for the {@link com.nexigroup.pagopa.cruscotto.domain.KpiB1DetailResult} entity.
 */
@Data
public class KpiB1DetailResultDTO implements Serializable {

    private Long id;

    private Long instanceId;

    private Long instanceModuleId;

    private Long kpiB1ResultId;

    private LocalDate analysisDate;

    private EvaluationType evaluationType;

    private LocalDate evaluationStartDate;

    private LocalDate evaluationEndDate;

    private Integer totalInstitutions;

    private Integer institutionDifference;

    private BigDecimal institutionDifferencePercentage;

    private OutcomeStatus institutionOutcome;

    private Integer totalTransactions;

    private Integer transactionDifference;

    private BigDecimal transactionDifferencePercentage;

    private OutcomeStatus transactionOutcome;
}