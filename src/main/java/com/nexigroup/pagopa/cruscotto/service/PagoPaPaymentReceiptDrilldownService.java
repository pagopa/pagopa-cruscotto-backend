package com.nexigroup.pagopa.cruscotto.service;

import com.nexigroup.pagopa.cruscotto.domain.AnagStation;
import com.nexigroup.pagopa.cruscotto.domain.Instance;
import com.nexigroup.pagopa.cruscotto.domain.InstanceModule;
import com.nexigroup.pagopa.cruscotto.service.dto.PagoPaPaymentReceiptDrilldownDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.PagoPaPaymentReceiptDTO;
import java.time.LocalDate;
import java.util.List;

/**
 * Service Interface for managing {@link com.nexigroup.pagopa.cruscotto.domain.PagoPaPaymentReceiptDrilldown}.
 */
public interface PagoPaPaymentReceiptDrilldownService {

    /**
     * Save quarter-hour aggregated data for drilldown analysis
     * 
     * @param instanceId the instance ID
     * @param instanceModuleId the instance module ID
     * @param stationId the station ID
     * @param evaluationDate the evaluation date
     * @param analysisDate the analysis date
     * @param rawData the raw payment receipt data to aggregate
     */
    void saveQuarterHourAggregatedData(
        Long instanceId, 
        Long instanceModuleId,
        Long stationId, 
        LocalDate evaluationDate,
        LocalDate analysisDate,
        List<PagoPaPaymentReceiptDTO> rawData
    );

    /**
     * Save quarter-hour aggregated data for drilldown analysis with pre-loaded entities.
     * This optimized version avoids database lookups for entities.
     * 
     * @param instance the pre-loaded instance entity
     * @param instanceModule the pre-loaded instance module entity
     * @param station the pre-loaded station entity
     * @param evaluationDate the evaluation date
     * @param analysisDate the analysis date
     * @param rawData the raw payment receipt data to aggregate
     */
    void saveQuarterHourAggregatedDataOptimized(
        Instance instance,
        InstanceModule instanceModule,
        AnagStation station,
        LocalDate evaluationDate,
        LocalDate analysisDate,
        List<PagoPaPaymentReceiptDTO> rawData
    );

    /**
     * Add quarter-hour aggregated data to a batch for later processing.
     * This method prepares data without immediately saving to database.
     * 
     * @param batch the list to collect drilldown records
     * @param instance the pre-loaded instance entity
     * @param instanceModule the pre-loaded instance module entity
     * @param station the pre-loaded station entity
     * @param evaluationDate the evaluation date
     * @param analysisDate the analysis date
     * @param rawData the raw payment receipt data to aggregate
     */
    void addToBatch(
        List<com.nexigroup.pagopa.cruscotto.domain.PagoPaPaymentReceiptDrilldown> batch,
        Instance instance,
        InstanceModule instanceModule,
        AnagStation station,
        LocalDate evaluationDate,
        LocalDate analysisDate,
        List<PagoPaPaymentReceiptDTO> rawData
    );

    /**
     * Save a batch of drilldown records in a single transaction.
     * 
     * @param batch the list of drilldown records to save
     * @return number of saved records
     */
    int saveBatch(List<com.nexigroup.pagopa.cruscotto.domain.PagoPaPaymentReceiptDrilldown> batch);

    /**
     * Get drilldown data for specific instance, station and evaluation date.
     * 
     * @param instanceId the instance ID
     * @param stationId the station ID
     * @param evaluationDate the evaluation date (the specific day to get drilldown for)
     * @return list of quarter-hour aggregated data (max 96 records)
     */
    List<PagoPaPaymentReceiptDrilldownDTO> getDrilldownData(
        Long instanceId, 
        Long stationId, 
        LocalDate evaluationDate
    );

    /**
     * Delete all drilldown data for a specific instance module
     * 
     * @param instanceModuleId the instance module ID
     * @return number of deleted records
     */
    int deleteAllByInstanceModuleId(Long instanceModuleId);

    /**
     * Delete drilldown data for a specific instance module and analysis date
     * This preserves historical data while cleaning only the current analysis
     * 
     * @param instanceModuleId the instance module ID
     * @param analysisDate the analysis date
     * @return number of deleted records
     */
    int deleteByInstanceModuleIdAndAnalysisDate(Long instanceModuleId, LocalDate analysisDate);
}