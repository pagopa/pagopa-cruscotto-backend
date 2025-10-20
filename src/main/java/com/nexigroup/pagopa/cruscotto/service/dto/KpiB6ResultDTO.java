package com.nexigroup.pagopa.cruscotto.service.dto;

import com.nexigroup.pagopa.cruscotto.domain.enumeration.EvaluationType;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import com.nexigroup.pagopa.cruscotto.kpi.framework.KpiResult;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO for KPI B.6 Result - Payment Options
 */
@Data
public class KpiB6ResultDTO implements KpiResult {
    
    private Long id;
    
    private Long instanceId;
    
    private Long instanceModuleId;
    
    private LocalDate analysisDate;
    
    private EvaluationType evaluationType;
    
    /** Total active stations found */
    private Integer totalActiveStations;
    
    /** Total stations with payment options enabled */
    private Integer stationsWithPaymentOptions;
    
    /** Percentage of stations with payment options */
    private BigDecimal paymentOptionsPercentage;
    
    /** Tolerance threshold for minimum acceptable percentage */
    private BigDecimal toleranceThreshold;
    
    /** Final outcome status */
    private OutcomeStatus outcome;
}