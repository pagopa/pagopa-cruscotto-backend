package com.nexigroup.pagopa.cruscotto.service;

import com.nexigroup.pagopa.cruscotto.domain.Instance;
import com.nexigroup.pagopa.cruscotto.domain.InstanceModule;
import com.nexigroup.pagopa.cruscotto.domain.AnagStation;
import com.nexigroup.pagopa.cruscotto.domain.KpiB3AnalyticData;
import com.nexigroup.pagopa.cruscotto.domain.PagopaNumeroStandin;
import com.nexigroup.pagopa.cruscotto.domain.PagopaNumeroStandinDrilldown;
import com.nexigroup.pagopa.cruscotto.service.dto.PagopaNumeroStandinDTO;
import java.time.LocalDate;
import java.util.List;

/**
 * Service Interface for managing {@link com.nexigroup.pagopa.cruscotto.domain.PagopaNumeroStandinDrilldown}.
 * 
 * Provides operations for storing and retrieving historical Stand-In data snapshots
 * used in KPI B.3 drilldown analysis.
 */
public interface PagopaNumeroStandinDrilldownService {

    /**
     * Save Stand-In data snapshot for drilldown analysis.
     * This creates a historical snapshot of the Stand-In data at the time of KPI analysis.
     * 
     * @param instance the instance entity
     * @param instanceModule the instance module entity
     * @param station the station entity
     * @param kpiB3AnalyticData the KPI B.3 analytic data entity
     * @param analysisDate the analysis date
     * @param standInData the original Stand-In data to snapshot
     */
    void saveStandInSnapshot(
        Instance instance,
        InstanceModule instanceModule,
        AnagStation station,
        KpiB3AnalyticData kpiB3AnalyticData,
        LocalDate analysisDate,
        List<PagopaNumeroStandin> standInData
    );

    /**
     * Add Stand-In snapshots to batch for bulk save.
     * 
     * @param batch the batch list to add records to
     * @param instance the instance entity
     * @param instanceModule the instance module entity
     * @param station the station entity
     * @param kpiB3AnalyticData the KPI B.3 analytic data entity
     * @param analysisDate the analysis date
     * @param standInData the original Stand-In data to snapshot
     */
    void addToBatch(
        List<PagopaNumeroStandinDrilldown> batch,
        Instance instance,
        InstanceModule instanceModule,
        AnagStation station,
        KpiB3AnalyticData kpiB3AnalyticData,
        LocalDate analysisDate,
        List<PagopaNumeroStandin> standInData
    );

    /**
     * Save a batch of drilldown records in a single transaction.
     * 
     * @param batch the list of drilldown records to save
     * @return number of saved records
     */
    int saveBatch(List<PagopaNumeroStandinDrilldown> batch);

    /**
     * Find all Stand-In drilldown records for a specific KPI B.3 analytic data entry.
     * This represents the final drilldown level showing the historical snapshot data.
     * 
     * @param analyticDataId the ID of the KpiB3AnalyticData record
     * @return the list of PagopaNumeroStandinDTO records with partner information
     */
    List<PagopaNumeroStandinDTO> findByAnalyticDataId(Long analyticDataId);

    /**
     * Convert a PagopaNumeroStandinDrilldown entity to DTO with partner information.
     * 
     * @param entity the entity to convert
     * @return the converted DTO with partner information
     */
    PagopaNumeroStandinDTO convertToDTO(PagopaNumeroStandinDrilldown entity);

    /**
     * Delete all drilldown records for a specific instance module.
     * 
     * @param instanceModuleId the instance module ID
     * @return number of deleted records
     */
    int deleteAllByInstanceModuleId(Long instanceModuleId);

    /**
     * Delete drilldown records for a specific instance module and analysis date.
     * 
     * @param instanceModuleId the instance module ID
     * @param analysisDate the analysis date
     * @return number of deleted records
     */
    int deleteByInstanceModuleIdAndAnalysisDate(Long instanceModuleId, LocalDate analysisDate);
}