package com.nexigroup.pagopa.cruscotto.service;

import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import com.nexigroup.pagopa.cruscotto.service.dto.InstanceDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.InstanceModuleDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiConfigurationDTO;
import java.time.LocalDate;

/**
 * Service for handling KPI B.4 data operations.
 */
public interface KpiB8DataService {

    /**
     * Save KPI B.4 results in the three tables (Result, DetailResult, AnalyticData)
     * This method is transactional and will handle the delete and save operations correctly.
     *
     * @param instanceDTO the instance
     * @param instanceModuleDTO the instance module
     * @param kpiConfigurationDTO the KPI configuration
     * @param analysisDate the analysis date
     * @param outcome the calculation outcome
     * @return the final outcome (potentially corrected for monthly evaluation)
     */
    OutcomeStatus saveKpiB8Results(InstanceDTO instanceDTO, InstanceModuleDTO instanceModuleDTO,
                                   KpiConfigurationDTO kpiConfigurationDTO, LocalDate analysisDate,
                                   OutcomeStatus outcome);
}
