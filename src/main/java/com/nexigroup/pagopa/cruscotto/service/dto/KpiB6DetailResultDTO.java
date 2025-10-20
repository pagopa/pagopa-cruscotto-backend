package com.nexigroup.pagopa.cruscotto.service.dto;

import com.nexigroup.pagopa.cruscotto.domain.enumeration.EvaluationType;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import com.nexigroup.pagopa.cruscotto.kpi.framework.KpiDetailResult;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO for KPI B.6 Detail Result - Payment Options (monthly/total breakdown)
 */
@Data
public class KpiB6DetailResultDTO implements KpiDetailResult {
    
    private Long id;
    
    private Long instanceId;
    
    private Long instanceModuleId;
    
    private Long kpiB6ResultId;
    
    private LocalDate analysisDate;
    
    private EvaluationType evaluationType;
    
    private LocalDate evaluationStartDate;
    
    private LocalDate evaluationEndDate;
    
    /** Active stations in this period */
    private Integer activeStations;
    
    /** Stations with payment options in this period */
    private Integer stationsWithPaymentOptions;
    
    /** Percentage of compliance in this period */
    private BigDecimal compliancePercentage;
    
    /** Difference from expected (actual - expected) */
    private Integer stationDifference;
    
    /** Percentage difference from expected */
    private BigDecimal stationDifferencePercentage;
    
    /** Outcome for this evaluation period */
    private OutcomeStatus outcome;
}