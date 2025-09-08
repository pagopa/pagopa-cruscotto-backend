package com.nexigroup.pagopa.cruscotto.service.dto;

import lombok.Data;
import java.io.Serializable;

@Data
public class KpiA2AnalyticIncorrectTaxonomyDataDTO implements Serializable {
    
    private Long id;
    
    private Long kpiA2AnalyticDataId;
    
    private String transferCategory;
    
    private Long total;
    
    private java.time.Instant fromHour;
    
    private java.time.Instant endHour;
}
