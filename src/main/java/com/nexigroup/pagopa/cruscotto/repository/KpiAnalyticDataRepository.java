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
     * Delete all analytic data by module code and instance module id.
     */
    @Modifying
    @Query("DELETE FROM KpiAnalyticData k WHERE k.moduleCode = :moduleCode AND k.instanceModuleId = :instanceModuleId")
    void deleteAllByModuleCodeAndInstanceModuleId(@Param("moduleCode") ModuleCode moduleCode, @Param("instanceModuleId") Long instanceModuleId);
}