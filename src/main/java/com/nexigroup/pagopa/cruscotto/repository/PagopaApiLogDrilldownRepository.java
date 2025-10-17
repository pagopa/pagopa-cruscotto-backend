package com.nexigroup.pagopa.cruscotto.repository;

import com.nexigroup.pagopa.cruscotto.domain.PagopaApiLogDrilldown;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data repository for the PagopaApiLogDrilldown entity.
 */
@Repository
public interface PagopaApiLogDrilldownRepository 
    extends JpaRepository<PagopaApiLogDrilldown, Long>, JpaSpecificationExecutor<PagopaApiLogDrilldown> {

    /**
     * Find all drilldown records for a specific KPI B.4 analytic data ID
     * @param analyticDataId the KPI B.4 analytic data ID
     * @return list of drilldown records ordered by data date, partner fiscal code, station code
     */
    @Query("SELECT d FROM PagopaApiLogDrilldown d " +
           "WHERE d.kpiB4AnalyticData.id = :analyticDataId " +
           "ORDER BY d.dataDate ASC, d.partnerFiscalCode ASC, d.stationCode ASC")
    List<PagopaApiLogDrilldown> findByKpiB4AnalyticDataId(@Param("analyticDataId") Long analyticDataId);

    /**
     * Find all drilldown records for a specific instance and analysis date
     * @param instanceId the instance ID
     * @param analysisDate the analysis date (when the analysis was performed)
     * @return list of drilldown records ordered by data date
     */
    @Query("SELECT d FROM PagopaApiLogDrilldown d " +
           "WHERE d.instance.id = :instanceId " +
           "AND d.analysisDate = :analysisDate " +
           "ORDER BY d.dataDate ASC, d.partnerFiscalCode ASC")
    List<PagopaApiLogDrilldown> findByInstanceIdAndAnalysisDate(
        @Param("instanceId") Long instanceId, 
        @Param("analysisDate") LocalDate analysisDate);

    /**
     * Find all drilldown records for a specific instance, station and data date
     * @param instanceId the instance ID
     * @param stationId the station ID
     * @param dataDate the data date (the date of the data being analyzed)
     * @return list of drilldown records ordered by data date, API
     */
    @Query("SELECT d FROM PagopaApiLogDrilldown d " +
           "WHERE d.instance.id = :instanceId " +
           "AND d.station.id = :stationId " +
           "AND d.dataDate = :dataDate " +
           "ORDER BY d.dataDate ASC, d.api ASC")
    List<PagopaApiLogDrilldown> findByInstanceIdAndStationIdAndDataDate(
        @Param("instanceId") Long instanceId, 
        @Param("stationId") Long stationId, 
        @Param("dataDate") LocalDate dataDate);

    /**
     * Delete all drilldown records for a specific instance
     * @param instanceId the instance ID
     * @return number of deleted records
     */
    @Modifying
    @Query("DELETE FROM PagopaApiLogDrilldown d WHERE d.instance.id = :instanceId")
    int deleteAllByInstanceId(@Param("instanceId") Long instanceId);

    /**
     * Delete all drilldown records for a specific instance module
     * @param instanceModuleId the instance module ID
     * @return number of deleted records
     */
    @Modifying
    @Query("DELETE FROM PagopaApiLogDrilldown d WHERE d.instanceModule.id = :instanceModuleId")
    int deleteAllByInstanceModuleId(@Param("instanceModuleId") Long instanceModuleId);

    /**
     * Delete drilldown records for a specific instance module and analysis date
     * This is used during re-processing to clean only the current analysis while preserving historical data
     * @param instanceModuleId the instance module ID
     * @param analysisDate the analysis date
     * @return number of deleted records
     */
    @Modifying
    @Query("DELETE FROM PagopaApiLogDrilldown d WHERE d.instanceModule.id = :instanceModuleId AND d.analysisDate = :analysisDate")
    int deleteByInstanceModuleIdAndAnalysisDate(@Param("instanceModuleId") Long instanceModuleId, @Param("analysisDate") LocalDate analysisDate);

    /**
     * Find all drilldown records for a specific KPI B.4 analytic data and partner fiscal code
     * @param analyticDataId the KPI B.4 analytic data ID
     * @param partnerFiscalCode the partner fiscal code
     * @return list of drilldown records ordered by data date, station code
     */
    @Query("SELECT d FROM PagopaApiLogDrilldown d " +
           "WHERE d.kpiB4AnalyticData.id = :analyticDataId " +
           "AND d.partnerFiscalCode = :partnerFiscalCode " +
           "ORDER BY d.dataDate ASC, d.stationCode ASC")
    List<PagopaApiLogDrilldown> findByKpiB4AnalyticDataIdAndPartnerFiscalCode(
        @Param("analyticDataId") Long analyticDataId, 
        @Param("partnerFiscalCode") String partnerFiscalCode);
}