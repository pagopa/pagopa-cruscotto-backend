package com.nexigroup.pagopa.cruscotto.repository;

import com.nexigroup.pagopa.cruscotto.domain.PagopaNumeroStandinDrilldown;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data repository for the PagopaNumeroStandinDrilldown entity.
 */
@Repository
public interface PagopaNumeroStandinDrilldownRepository 
    extends JpaRepository<PagopaNumeroStandinDrilldown, Long>, JpaSpecificationExecutor<PagopaNumeroStandinDrilldown> {

    /**
     * Find all drilldown records for a specific KPI B.3 analytic data entry
     * @param analyticDataId the KPI B.3 analytic data ID
     * @return list of drilldown records ordered by data ora evento
     */
    @Query("SELECT d FROM PagopaNumeroStandinDrilldown d " +
           "WHERE d.kpiB3AnalyticData.id = :analyticDataId " +
           "ORDER BY d.dataOraEvento ASC")
    List<PagopaNumeroStandinDrilldown> findByKpiB3AnalyticDataId(@Param("analyticDataId") Long analyticDataId);

    /**
     * Delete all drilldown records for a specific instance module
     * @param instanceModuleId the instance module ID
     * @return number of deleted records
     */
    @Modifying
    @Query("DELETE FROM PagopaNumeroStandinDrilldown d WHERE d.instanceModule.id = :instanceModuleId")
    int deleteAllByInstanceModuleId(@Param("instanceModuleId") Long instanceModuleId);

    /**
     * Delete drilldown records for a specific instance module and analysis date
     * @param instanceModuleId the instance module ID
     * @param analysisDate the analysis date
     * @return number of deleted records
     */
    @Modifying
    @Query("DELETE FROM PagopaNumeroStandinDrilldown d WHERE d.instanceModule.id = :instanceModuleId AND d.analysisDate = :analysisDate")
    int deleteByInstanceModuleIdAndAnalysisDate(@Param("instanceModuleId") Long instanceModuleId, @Param("analysisDate") LocalDate analysisDate);

    /**
     * Find all drilldown records for a specific instance and station on analysis date
     * @param instanceId the instance ID
     * @param stationId the station ID  
     * @param analysisDate the analysis date (when the analysis was performed)
     * @return list of drilldown records ordered by data ora evento
     */
    @Query("SELECT d FROM PagopaNumeroStandinDrilldown d " +
           "WHERE d.instance.id = :instanceId " +
           "AND d.station.id = :stationId " +
           "AND d.analysisDate = :analysisDate " +
           "ORDER BY d.dataOraEvento ASC")
    List<PagopaNumeroStandinDrilldown> findByInstanceIdAndStationIdAndAnalysisDate(
        @Param("instanceId") Long instanceId, 
        @Param("stationId") Long stationId, 
        @Param("analysisDate") LocalDate analysisDate);

    /**
     * Check if data exists for a specific analytic data ID
     * @param analyticDataId the analytic data ID
     * @return true if records exist
     */
    boolean existsByKpiB3AnalyticDataId(Long analyticDataId);

    /**
     * Count total records for a specific analytic data ID
     * @param analyticDataId the analytic data ID  
     * @return count of records
     */
    long countByKpiB3AnalyticDataId(Long analyticDataId);
}