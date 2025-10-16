package com.nexigroup.pagopa.cruscotto.service;

import com.nexigroup.pagopa.cruscotto.service.dto.PagopaAPILogDTO;
import java.util.List;

/**
 * Service Interface for managing PagopaApiLog.
 */
public interface PagopaApiLogService {
    
    /**
     * Find PagoPA API log data by KPI B4 analytic data ID.
     * This retrieves the historical snapshot data associated with a specific KPI B4 analysis.
     *
     * @param analyticDataId the ID of the KpiB4AnalyticData record
     * @return the list of PagopaAPILogDTO records
     */
    List<PagopaAPILogDTO> findByKpiB4AnalyticDataId(Long analyticDataId);
}