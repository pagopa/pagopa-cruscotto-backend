package com.nexigroup.pagopa.cruscotto.repository;

import com.nexigroup.pagopa.cruscotto.domain.SpontaneousDrilldown;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the SpontaneousDrilldown entity.
 */
@Repository
public interface SpontaneousDrilldownRepository extends JpaRepository<SpontaneousDrilldown, Long> {

    /**
     * Find all SpontaneousDrilldown records by KPI B5 Analytic Data ID
     *
     * @param kpiB5AnalyticDataId the ID of the KPI B5 Analytic Data
     * @return list of SpontaneousDrilldown records
     */
    @Query("SELECT s FROM SpontaneousDrilldown s WHERE s.kpiB5AnalyticData.id = :kpiB5AnalyticDataId")
    List<SpontaneousDrilldown> findByKpiB5AnalyticDataId(@Param("kpiB5AnalyticDataId") Long kpiB5AnalyticDataId);

    /**
     * Delete all SpontaneousDrilldown records by KPI B5 Analytic Data ID
     *
     * @param kpiB5AnalyticDataId the ID of the KPI B5 Analytic Data
     * @return number of deleted records
     */
    @Modifying
    @Query("DELETE FROM SpontaneousDrilldown s WHERE s.kpiB5AnalyticData.id = :kpiB5AnalyticDataId")
    int deleteByKpiB5AnalyticDataId(@Param("kpiB5AnalyticDataId") Long kpiB5AnalyticDataId);

    /**
     * Delete all SpontaneousDrilldown records by instance module ID
     *
     * @param instanceModuleId the ID of the instance module
     * @return number of deleted records
     */
    @Modifying
    @Query("DELETE FROM SpontaneousDrilldown s WHERE s.instanceModule.id = :instanceModuleId")
    int deleteByInstanceModuleId(@Param("instanceModuleId") Long instanceModuleId);
}