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
    List<KpiAnalyticData> findAllByModuleCode(ModuleCode moduleCode);

    /**
     * Find analytic data by module code and instance module id.
     */
    List<KpiAnalyticData> findAllByModuleCodeAndInstanceModuleId(ModuleCode moduleCode, Long instanceModuleId);

    /**
     * Find analytic data by module code and station code.
     */
    List<KpiAnalyticData> findAllByModuleCodeAndStationCode(ModuleCode moduleCode, String stationCode);

    /**
     * Find analytic data by module code and partner fiscal code.
     */
    List<KpiAnalyticData> findAllByModuleCodeAndPartnerFiscalCode(ModuleCode moduleCode, String partnerFiscalCode);

    /**
     * Delete all analytic data by module code and instance module id.
     */
    @Modifying
    @Query("DELETE FROM KpiAnalyticData k WHERE k.moduleCode = :moduleCode AND k.instanceModuleId = :instanceModuleId")
    void deleteAllByModuleCodeAndInstanceModuleId(@Param("moduleCode") ModuleCode moduleCode, @Param("instanceModuleId") Long instanceModuleId);

    /**
     * Count analytic data by module code and station code.
     */
    @Query("SELECT COUNT(k) FROM KpiAnalyticData k WHERE k.moduleCode = :moduleCode AND k.stationCode = :stationCode")
    Long countByModuleCodeAndStationCode(@Param("moduleCode") ModuleCode moduleCode, @Param("stationCode") String stationCode);
}