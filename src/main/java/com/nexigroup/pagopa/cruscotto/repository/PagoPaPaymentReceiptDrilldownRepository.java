package com.nexigroup.pagopa.cruscotto.repository;

import com.nexigroup.pagopa.cruscotto.domain.PagoPaPaymentReceiptDrilldown;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data repository for the PagoPaPaymentReceiptDrilldown entity.
 */
@Repository
public interface PagoPaPaymentReceiptDrilldownRepository 
    extends JpaRepository<PagoPaPaymentReceiptDrilldown, Long>, JpaSpecificationExecutor<PagoPaPaymentReceiptDrilldown> {

    /**
     * Find all drilldown records for a specific instance, station and analysis date
     * @param instanceId the instance ID
     * @param stationId the station ID
     * @param analysisDate the analysis date (when the analysis was performed)
     * @return list of drilldown records ordered by start time
     */
    @Query("SELECT d FROM PagoPaPaymentReceiptDrilldown d " +
           "WHERE d.instance.id = :instanceId " +
           "AND d.station.id = :stationId " +
           "AND d.analysisDate = :analysisDate " +
           "ORDER BY d.startTime ASC")
    List<PagoPaPaymentReceiptDrilldown> findByInstanceIdAndStationIdAndAnalysisDate(
        @Param("instanceId") Long instanceId, 
        @Param("stationId") Long stationId, 
        @Param("analysisDate") LocalDate analysisDate);

    /**
     * Find all drilldown records for a specific instance, station and evaluation date
     * @param instanceId the instance ID
     * @param stationId the station ID
     * @param evaluationDate the evaluation date (the date of the data being analyzed)
     * @return list of drilldown records ordered by start time
     */
    @Query("SELECT d FROM PagoPaPaymentReceiptDrilldown d " +
           "WHERE d.instance.id = :instanceId " +
           "AND d.station.id = :stationId " +
           "AND d.evaluationDate = :evaluationDate " +
           "ORDER BY d.startTime ASC")
    List<PagoPaPaymentReceiptDrilldown> findByInstanceIdAndStationIdAndEvaluationDate(
        @Param("instanceId") Long instanceId, 
        @Param("stationId") Long stationId, 
        @Param("evaluationDate") LocalDate evaluationDate);

    /**
     * Delete all drilldown records for a specific instance
     * @param instanceId the instance ID
     * @return number of deleted records
     */
    @Modifying
    @Query("DELETE FROM PagoPaPaymentReceiptDrilldown d WHERE d.instance.id = :instanceId")
    int deleteAllByInstanceId(@Param("instanceId") Long instanceId);

    /**
     * Delete all drilldown records for a specific instance module
     * @param instanceModuleId the instance module ID
     * @return number of deleted records
     */
    @Modifying
    @Query("DELETE FROM PagoPaPaymentReceiptDrilldown d WHERE d.instanceModule.id = :instanceModuleId")
    int deleteAllByInstanceModuleId(@Param("instanceModuleId") Long instanceModuleId);

    /**
     * Delete drilldown records for a specific instance module and analysis date
     * This is used during re-processing to clean only the current analysis while preserving historical data
     * @param instanceModuleId the instance module ID
     * @param analysisDate the analysis date
     * @return number of deleted records
     */
    @Modifying
    @Query("DELETE FROM PagoPaPaymentReceiptDrilldown d WHERE d.instanceModule.id = :instanceModuleId AND d.analysisDate = :analysisDate")
    int deleteByInstanceModuleIdAndAnalysisDate(@Param("instanceModuleId") Long instanceModuleId, @Param("analysisDate") LocalDate analysisDate);

    /**
     * Find all drilldown records for a specific instance and evaluation date
     * @param instanceId the instance ID
     * @param evaluationDate the evaluation date
     * @return list of drilldown records
     */
    @Query("SELECT d FROM PagoPaPaymentReceiptDrilldown d " +
           "WHERE d.instance.id = :instanceId " +
           "AND d.evaluationDate = :evaluationDate " +
           "ORDER BY d.station.id ASC, d.startTime ASC")
    List<PagoPaPaymentReceiptDrilldown> findByInstanceIdAndEvaluationDate(
        @Param("instanceId") Long instanceId, 
        @Param("evaluationDate") LocalDate evaluationDate);
}