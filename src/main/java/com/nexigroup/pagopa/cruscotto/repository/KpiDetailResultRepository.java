package com.nexigroup.pagopa.cruscotto.repository;

import com.nexigroup.pagopa.cruscotto.domain.KpiDetailResult;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.ModuleCode;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the KpiDetailResult entity.
 * Generic repository that handles all KPI types using ModuleCode discrimination.
 */
@SuppressWarnings("unused")
@Repository
public interface KpiDetailResultRepository extends JpaRepository<KpiDetailResult, Long> {

    /**
     * Find all detail results by module code.
     */
    List<KpiDetailResult> findAllByModuleCode(ModuleCode moduleCode);

    /**
     * Find detail results by module code and instance module id.
     */
    List<KpiDetailResult> findAllByModuleCodeAndInstanceModuleId(ModuleCode moduleCode, Long instanceModuleId);

    /**
     * Find detail results by module code and KPI result id.
     */
    List<KpiDetailResult> findAllByModuleCodeAndKpiResultId(ModuleCode moduleCode, Long kpiResultId);

    /**
     * Delete all detail results by module code and instance module id.
     */
    @Modifying
    @Query("DELETE FROM KpiDetailResult k WHERE k.moduleCode = :moduleCode AND k.instanceModuleId = :instanceModuleId")
    void deleteAllByModuleCodeAndInstanceModuleId(@Param("moduleCode") ModuleCode moduleCode, @Param("instanceModuleId") Long instanceModuleId);
}