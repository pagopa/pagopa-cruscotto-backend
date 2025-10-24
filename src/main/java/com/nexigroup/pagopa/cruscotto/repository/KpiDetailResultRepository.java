package com.nexigroup.pagopa.cruscotto.repository;

import com.nexigroup.pagopa.cruscotto.domain.KpiDetailResult;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.EvaluationType;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.ModuleCode;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
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
    List<KpiDetailResult> findByModuleCode(ModuleCode moduleCode);

    /**
     * Find detail results by module code and KPI result id.
     */
    List<KpiDetailResult> findByModuleCodeAndKpiResultId(ModuleCode moduleCode, Long kpiResultId);

    /**
     * Find detail results by module code and instance module id.
     */
    List<KpiDetailResult> findByModuleCodeAndInstanceModuleId(ModuleCode moduleCode, Long instanceModuleId);

    /**
     * Find detail result by module code, KPI result id and evaluation type.
     */
    Optional<KpiDetailResult> findByModuleCodeAndKpiResultIdAndEvaluationType(ModuleCode moduleCode, Long kpiResultId, EvaluationType evaluationType);

    /**
     * Delete all detail results by module code and instance module id.
     */
    @Modifying
    @Query("DELETE FROM KpiDetailResult k WHERE k.moduleCode = :moduleCode AND k.instanceModuleId = :instanceModuleId")
    void deleteByModuleCodeAndInstanceModuleId(@Param("moduleCode") ModuleCode moduleCode, @Param("instanceModuleId") Long instanceModuleId);

    /**
     * Delete all detail results by module code and KPI result id.
     */
    @Modifying
    @Query("DELETE FROM KpiDetailResult k WHERE k.moduleCode = :moduleCode AND k.kpiResultId = :kpiResultId")
    void deleteByModuleCodeAndKpiResultId(@Param("moduleCode") ModuleCode moduleCode, @Param("kpiResultId") Long kpiResultId);

    /**
     * Find detail results by module code and evaluation period.
     */
    @Query("SELECT k FROM KpiDetailResult k WHERE k.moduleCode = :moduleCode AND k.evaluationStartDate >= :startDate AND k.evaluationEndDate <= :endDate ORDER BY k.evaluationStartDate")
    List<KpiDetailResult> findByModuleCodeAndEvaluationPeriod(@Param("moduleCode") ModuleCode moduleCode, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}