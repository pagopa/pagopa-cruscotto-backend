package com.nexigroup.pagopa.cruscotto.service;


import com.nexigroup.pagopa.cruscotto.domain.PagopaNumeroStandin;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import com.nexigroup.pagopa.cruscotto.service.dto.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Service for handling KPI B.3 data operations.
 */
public interface KpiB8DataService {

    /**
     * Save KPI B.3 results in the three tables (Result, DetailResult, AnalyticData)
     * This method is transactional and will handle the delete and save operations correctly.
     *
     * @param instanceDTO the instance
     * @param instanceModuleDTO the instance module
     * @param kpiConfigurationDTO the KPI configuration
     * @param analysisDate the analysis date
     * @param outcome the calculation outcome
     * @param standInData the Stand-In aggregated data from database
     */
    void saveKpiB8Results(InstanceDTO instanceDTO, InstanceModuleDTO instanceModuleDTO,
                         KpiConfigurationDTO kpiConfigurationDTO, LocalDate analysisDate,
                         OutcomeStatus outcome, List<PagopaNumeroStandin> standInData);

    /**
     * Creates and saves the main KPI B.3 result record
     *
     * @param instanceDTO the instance
     * @param instanceModuleDTO the instance module
     * @param kpiConfigurationDTO the KPI configuration
     * @param analysisDate the analysis date
     * @return the saved KpiB8ResultDTO
     */
    KpiB8ResultDTO createKpiB8Result(InstanceDTO instanceDTO, InstanceModuleDTO instanceModuleDTO,
                                    KpiConfigurationDTO kpiConfigurationDTO, LocalDate analysisDate);

    /**
     * Saves a KPI B.3 detail result (monthly or total)
     *
     * @param detailResult the detail result to save
     * @return the saved detail result
     */
    KpiB8DetailResultDTO saveKpiB8DetailResult(KpiB8DetailResultDTO detailResult);

    /**
     * Saves KPI B.3 analytic data for Stand-In events
     *
     * @param instanceDTO the instance
     * @param instanceModuleDTO the instance module
     * @param standInData the Stand-In data
     */
    void saveKpiB8AnalyticData(InstanceDTO instanceDTO, InstanceModuleDTO instanceModuleDTO,
                              List<PagopaNumeroStandin> standInData);

    /**
     * Updates the outcome of a KPI B.3 result
     *
     * @param resultId the result ID
     * @param outcome the new outcome
     */
    void updateKpiB8ResultOutcome(Long resultId, OutcomeStatus outcome);
}
