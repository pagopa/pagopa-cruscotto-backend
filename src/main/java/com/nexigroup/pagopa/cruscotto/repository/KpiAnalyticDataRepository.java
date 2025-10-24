package com.nexigroup.pagopa.cruscotto.repository;

import com.nexigroup.pagopa.cruscotto.domain.KpiAnalyticData;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.ModuleCode;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the KpiAnalyticData entity.
 * Generic repository that handles all KPI types using ModuleCode discrimination.
 */
@SuppressWarnings("unused")
@Repository
public interface KpiAnalyticDataRepository extends JpaRepository<KpiAnalyticData, Long> {

    /**
     * Find all analytic data by module code.
     */
    List<KpiAnalyticData> findByModuleCode(ModuleCode moduleCode);

    /**
     * Find analytic data by module code and detail result id.
     */
    List<KpiAnalyticData> findByModuleCodeAndKpiDetailResultId(ModuleCode moduleCode, Long kpiDetailResultId);

    /**
     * Find analytic data by module code and instance module id.
     */
    List<KpiAnalyticData> findByModuleCodeAndInstanceModuleId(ModuleCode moduleCode, Long instanceModuleId);

    /**
     * Find analytic data by module code and date range.
     */
    @Query("SELECT k FROM KpiAnalyticData k WHERE k.moduleCode = :moduleCode AND k.dataDate BETWEEN :startDate AND :endDate")
    List<KpiAnalyticData> findByModuleCodeAndDataDateBetween(
        @Param("moduleCode") ModuleCode moduleCode,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    /**
     * Delete all analytic data by module code and instance module id.
     */
    @Modifying
    @Query("DELETE FROM KpiAnalyticData k WHERE k.moduleCode = :moduleCode AND k.instanceModuleId = :instanceModuleId")
    void deleteByModuleCodeAndInstanceModuleId(@Param("moduleCode") ModuleCode moduleCode, @Param("instanceModuleId") Long instanceModuleId);

    /**
     * Delete all analytic data by module code and detail result id.
     */
    @Modifying
    @Query("DELETE FROM KpiAnalyticData k WHERE k.moduleCode = :moduleCode AND k.kpiDetailResultId = :kpiDetailResultId")
    void deleteByModuleCodeAndKpiDetailResultId(@Param("moduleCode") ModuleCode moduleCode, @Param("kpiDetailResultId") Long kpiDetailResultId);

    /**
     * Count analytic data by module code and station code.
     */
    @Query("SELECT COUNT(k) FROM KpiAnalyticData k WHERE k.moduleCode = :moduleCode AND k.stationCode = :stationCode")
    Long countByModuleCodeAndStationCode(@Param("moduleCode") ModuleCode moduleCode, @Param("stationCode") String stationCode);

    /**
     * Find analytic data by module code, station code and date range.
     */
    @Query("SELECT k FROM KpiAnalyticData k WHERE k.moduleCode = :moduleCode AND k.stationCode = :stationCode AND k.dataDate BETWEEN :startDate AND :endDate")
    List<KpiAnalyticData> findByModuleCodeAndStationCodeAndDataDateBetween(
        @Param("moduleCode") ModuleCode moduleCode,
        @Param("stationCode") String stationCode,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );
}