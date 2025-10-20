package com.nexigroup.pagopa.cruscotto.service.dto;

import com.nexigroup.pagopa.cruscotto.kpi.framework.KpiAnalyticData;
import lombok.Data;

import java.time.LocalDate;

/**
 * DTO for KPI B.6 Analytic Data - Payment Options (detailed station data)
 */
@Data
public class KpiB6AnalyticDataDTO implements KpiAnalyticData {
    
    private Long id;
    
    private Long instanceId;
    
    private Long instanceModuleId;
    
    private Long kpiB6DetailResultId;
    
    private LocalDate analysisDate;
    
    private LocalDate dataDate;
    
    /** Station code */
    private String stationCode;
    
    /** Station status (ACTIVE/INACTIVE) */
    private String stationStatus;
    
    /** Whether payment options are enabled on this station */
    private Boolean paymentOptionsEnabled;
    
    /** Institution fiscal code */
    private String institutionFiscalCode;
    
    /** Partner fiscal code */
    private String partnerFiscalCode;
}