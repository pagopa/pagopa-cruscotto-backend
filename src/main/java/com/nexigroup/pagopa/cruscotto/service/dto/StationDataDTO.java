package com.nexigroup.pagopa.cruscotto.service.dto;

import lombok.Data;

/**
 * DTO representing station data for KPI B.6 analysis
 */
@Data
public class StationDataDTO {
    
    /** Station unique identifier */
    private String stationCode;
    
    /** Station status (ACTIVE/INACTIVE) */
    private String status;
    
    /** Whether payment options are enabled */
    private Boolean paymentOptionsEnabled;
    
    /** Institution fiscal code */
    private String institutionFiscalCode;
    
    /** Partner fiscal code */
    private String partnerFiscalCode;
    
    /** Station name/description */
    private String stationName;
}