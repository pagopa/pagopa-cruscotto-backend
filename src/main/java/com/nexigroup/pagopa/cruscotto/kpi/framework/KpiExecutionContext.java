package com.nexigroup.pagopa.cruscotto.kpi.framework;

import com.nexigroup.pagopa.cruscotto.service.dto.InstanceDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.InstanceModuleDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiConfigurationDTO;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.Map;

/**
 * Generic execution context for KPI processing.
 * Contains all necessary information for KPI calculation.
 */
@Data
@Builder
public class KpiExecutionContext {
    
    /** Instance being processed */
    private InstanceDTO instance;
    
    /** Instance module configuration */
    private InstanceModuleDTO instanceModule;
    
    /** KPI configuration with thresholds and tolerances */
    private KpiConfigurationDTO configuration;
    
    /** Analysis period start date */
    private LocalDate analysisStart;
    
    /** Analysis period end date */
    private LocalDate analysisEnd;
    
    /** Partner fiscal code */
    private String partnerFiscalCode;
    
    /** Additional parameters for specific KPI needs */
    private Map<String, Object> additionalParameters;
    
    /**
     * Creates a context for the given analysis period.
     */
    public static KpiExecutionContext create(InstanceDTO instance, 
                                           InstanceModuleDTO instanceModule,
                                           KpiConfigurationDTO configuration) {
        return KpiExecutionContext.builder()
                .instance(instance)
                .instanceModule(instanceModule)
                .configuration(configuration)
                .analysisStart(instance.getAnalysisPeriodStartDate())
                .analysisEnd(instance.getAnalysisPeriodEndDate())
                .partnerFiscalCode(instance.getPartnerFiscalCode())
                .build();
    }
}