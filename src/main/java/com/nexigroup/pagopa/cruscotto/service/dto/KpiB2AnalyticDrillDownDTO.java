package com.nexigroup.pagopa.cruscotto.service.dto;

import lombok.Data;
import java.io.Serializable;

@Data
public class KpiB2AnalyticDrillDownDTO implements Serializable {
    
    private Long id;
    
    private Long kpiB2AnalyticDataId;
    
    private Long totalRequests;

    private Long okRequests;

    private Double averageTimeMs;
    
    private java.time.Instant fromHour;
    
    private java.time.Instant endHour;
}
