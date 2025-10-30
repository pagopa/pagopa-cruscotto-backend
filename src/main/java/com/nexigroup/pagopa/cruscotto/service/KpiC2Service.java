package com.nexigroup.pagopa.cruscotto.service;

import com.nexigroup.pagopa.cruscotto.domain.Instance;
import com.nexigroup.pagopa.cruscotto.service.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service interface for managing KPI C2 calculations and results.
 * KPI C2: Utilizzo funzionalità pagamenti spontanei
 */
public interface KpiC2Service {

    /**
     * Execute KPI C2 calculation for a specific instance.
     * Implements the core business logic for API usage analysis.
     *
     * @param instance the instance to analyze
     * @return the main result of KPI C2 analysis
     */
    KpiC2ResultDTO executeKpiC2Calculation(Instance instance);

    /**
     * Get KPI C2 results by instance ID.
     *
     * @param instanceId the instance ID
     * @return the list of KPI C2 results
     */
    List<KpiC2ResultDTO> getKpiC2Results(String instanceId);

    /**
     * Get KPI C2 results by instance ID with pagination.
     *
     * @param instanceId the instance ID
     * @param pageable the pagination information
     * @return the page of KPI C2 results
     */
    Page<KpiC2ResultDTO> getKpiC2Results(String instanceId, Pageable pageable);

    /**
     * Get KPI C2 result by instance ID and analysis date.
     *
     * @param instanceId the instance ID
     * @param analysisDate the analysis date
     * @return the KPI C2 result
     */
    KpiC2ResultDTO getKpiC2Result(String instanceId, LocalDateTime analysisDate);

    /**
     * Get KPI C2 detail results (monthly and total analysis).
     *
     * @param instanceId the instance ID
     * @param analysisDate the analysis date
     * @return the list of detail results
     */
    List<KpiC2DetailResultDTO> getKpiC2DetailResults(String instanceId, LocalDateTime analysisDate);

    /**
     * Get KPI C2 detail results with pagination.
     *
     * @param instanceId the instance ID
     * @param analysisDate the analysis date
     * @param pageable the pagination information
     * @return the page of detail results
     */
    Page<KpiC2DetailResultDTO> getKpiC2DetailResults(String instanceId, LocalDateTime analysisDate, Pageable pageable);

    /**
     * Get KPI C2 analytic data for drill-down functionality.
     *
     * @param instanceId the instance ID
     * @param analysisDate the analysis date
     * @return the list of analytic data
     */
    List<KpiC2AnalyticDataDTO> getKpiC2AnalyticData(String instanceId, LocalDateTime analysisDate);

    /**
     * Get KPI C2 analytic data with pagination.
     *
     * @param instanceId the instance ID
     * @param analysisDate the analysis date
     * @param pageable the pagination information
     * @return the page of analytic data
     */
    Page<KpiC2AnalyticDataDTO> getKpiC2AnalyticData(String instanceId, LocalDateTime analysisDate, Pageable pageable);

    /**
     * Delete KPI C2 data for a specific instance.
     *
     * @param instanceId the instance ID
     */
    void deleteKpiC2Data(String instanceId);

    /**
     * Check if KPI C2 calculation already exists for instance and date.
     *
     * @param instanceId the instance ID
     * @param analysisDate the analysis date
     * @return true if calculation exists
     */
    boolean existsKpiC2Calculation(String instanceId, LocalDateTime analysisDate);

    /**
     * Update the outcome of a specific KPI C2 result.
     * This method is used to correct the outcome when the job calculates it differently from the service.
     *
     * @param resultId the KPI C2 result ID
     * @param outcome the new outcome to set
     */
    void updateKpiC2ResultOutcome(Long resultId, com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus outcome);

    /**
     * Recalculate KPI C2 for a specific instance (force recalculation).
     *
     * @param instanceId the instance ID
     * @return the recalculated result
     */
    KpiC2ResultDTO recalculateKpiC2(String instanceId);
}
