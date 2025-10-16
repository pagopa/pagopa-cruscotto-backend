package com.nexigroup.pagopa.cruscotto.service;

import com.nexigroup.pagopa.cruscotto.domain.Instance;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB4AnalyticDataDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB4DetailResultDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB4ResultDTO;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service interface for managing KPI B4 calculations and results.
 */
public interface KpiB4Service {

    /**
     * Execute KPI B4 calculation for a specific instance.
     * Implements the core business logic for API usage analysis.
     *
     * @param instance the instance to analyze
     * @return the main result of KPI B4 analysis
     */
    KpiB4ResultDTO executeKpiB4Calculation(Instance instance);

    /**
     * Get KPI B4 results by instance ID.
     *
     * @param instanceId the instance ID
     * @return the list of KPI B4 results
     */
    List<KpiB4ResultDTO> getKpiB4Results(String instanceId);

    /**
     * Get KPI B4 results by instance ID with pagination.
     *
     * @param instanceId the instance ID
     * @param pageable the pagination information
     * @return the page of KPI B4 results
     */
    Page<KpiB4ResultDTO> getKpiB4Results(String instanceId, Pageable pageable);

    /**
     * Get KPI B4 result by instance ID and analysis date.
     *
     * @param instanceId the instance ID
     * @param analysisDate the analysis date
     * @return the KPI B4 result
     */
    KpiB4ResultDTO getKpiB4Result(String instanceId, LocalDateTime analysisDate);

    /**
     * Get KPI B4 detail results (monthly and total analysis).
     *
     * @param instanceId the instance ID
     * @param analysisDate the analysis date
     * @return the list of detail results
     */
    List<KpiB4DetailResultDTO> getKpiB4DetailResults(String instanceId, LocalDateTime analysisDate);

    /**
     * Get KPI B4 detail results with pagination.
     *
     * @param instanceId the instance ID
     * @param analysisDate the analysis date
     * @param pageable the pagination information
     * @return the page of detail results
     */
    Page<KpiB4DetailResultDTO> getKpiB4DetailResults(String instanceId, LocalDateTime analysisDate, Pageable pageable);

    /**
     * Get KPI B4 analytic data for drill-down functionality.
     *
     * @param instanceId the instance ID
     * @param analysisDate the analysis date
     * @return the list of analytic data
     */
    List<KpiB4AnalyticDataDTO> getKpiB4AnalyticData(String instanceId, LocalDateTime analysisDate);

    /**
     * Get KPI B4 analytic data with pagination.
     *
     * @param instanceId the instance ID
     * @param analysisDate the analysis date
     * @param pageable the pagination information
     * @return the page of analytic data
     */
    Page<KpiB4AnalyticDataDTO> getKpiB4AnalyticData(String instanceId, LocalDateTime analysisDate, Pageable pageable);

    /**
     * Delete KPI B4 data for a specific instance.
     *
     * @param instanceId the instance ID
     */
    void deleteKpiB4Data(String instanceId);

    /**
     * Check if KPI B4 calculation already exists for instance and date.
     *
     * @param instanceId the instance ID
     * @param analysisDate the analysis date
     * @return true if calculation exists
     */
    boolean existsKpiB4Calculation(String instanceId, LocalDateTime analysisDate);

    /**
     * Update the outcome of a specific KPI B4 result.
     * This method is used to correct the outcome when the job calculates it differently from the service.
     *
     * @param resultId the KPI B4 result ID
     * @param outcome the new outcome to set
     */
    void updateKpiB4ResultOutcome(Long resultId, com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus outcome);

    /**
     * Recalculate KPI B4 for a specific instance (force recalculation).
     *
     * @param instanceId the instance ID
     * @return the recalculated result
     */
    KpiB4ResultDTO recalculateKpiB4(String instanceId);
}