package com.nexigroup.pagopa.cruscotto.service;

import com.nexigroup.pagopa.cruscotto.domain.Instance;
import com.nexigroup.pagopa.cruscotto.service.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service interface for managing KPI B8 calculations and results.
 */
public interface KpiB8Service {

    /**
     * Execute KPI B8 calculation for a specific instance.
     * Implements the core business logic for API usage analysis.
     *
     * @param instance the instance to analyze
     * @return the main result of KPI B8 analysis
     */
    KpiB8ResultDTO executeKpiB8Calculation(Instance instance);

    /**
     * Get KPI B8 results by instance ID.
     *
     * @param instanceId the instance ID
     * @return the list of KPI B8 results
     */
    List<KpiB8ResultDTO> getKpiB8Results(String instanceId);

    /**
     * Get KPI B8 results by instance ID with pagination.
     *
     * @param instanceId the instance ID
     * @param pageable the pagination information
     * @return the page of KPI B8 results
     */
    Page<KpiB8ResultDTO> getKpiB8Results(String instanceId, Pageable pageable);

    /**
     * Get KPI B8 result by instance ID and analysis date.
     *
     * @param instanceId the instance ID
     * @param analysisDate the analysis date
     * @return the KPI B8 result
     */
    KpiB8ResultDTO getKpiB8Result(String instanceId, LocalDateTime analysisDate);

    /**
     * Get KPI B8 detail results (monthly and total analysis).
     *
     * @param instanceId the instance ID
     * @param analysisDate the analysis date
     * @return the list of detail results
     */
    List<KpiB8DetailResultDTO> getKpiB8DetailResults(String instanceId, LocalDateTime analysisDate);

    /**
     * Get KPI B8 detail results with pagination.
     *
     * @param instanceId the instance ID
     * @param analysisDate the analysis date
     * @param pageable the pagination information
     * @return the page of detail results
     */
    Page<KpiB8DetailResultDTO> getKpiB8DetailResults(String instanceId, LocalDateTime analysisDate, Pageable pageable);

    /**
     * Get KPI B8 analytic data for drill-down functionality.
     *
     * @param instanceId the instance ID
     * @param analysisDate the analysis date
     * @return the list of analytic data
     */
    List<KpiB8AnalyticDataDTO> getKpiB8AnalyticData(String instanceId, LocalDateTime analysisDate);

    /**
     * Get KPI B8 analytic data with pagination.
     *
     * @param instanceId the instance ID
     * @param analysisDate the analysis date
     * @param pageable the pagination information
     * @return the page of analytic data
     */
    Page<KpiB8AnalyticDataDTO> getKpiB8AnalyticData(String instanceId, LocalDateTime analysisDate, Pageable pageable);

    /**
     * Delete KPI B8 data for a specific instance.
     *
     * @param instanceId the instance ID
     */
    void deleteKpiB8Data(String instanceId);

    /**
     * Check if KPI B8 calculation already exists for instance and date.
     *
     * @param instanceId the instance ID
     * @param analysisDate the analysis date
     * @return true if calculation exists
     */
    boolean existsKpiB8Calculation(String instanceId, LocalDateTime analysisDate);

    /**
     * Update the outcome of a specific KPI B8 result.
     * This method is used to correct the outcome when the job calculates it differently from the service.
     *
     * @param resultId the KPI B8 result ID
     * @param outcome the new outcome to set
     */
    void updateKpiB8ResultOutcome(Long resultId, com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus outcome);

    /**
     * Recalculate KPI B8 for a specific instance (force recalculation).
     *
     * @param instanceId the instance ID
     * @return the recalculated result
     */
    KpiB8ResultDTO recalculateKpiB8(String instanceId);
}
