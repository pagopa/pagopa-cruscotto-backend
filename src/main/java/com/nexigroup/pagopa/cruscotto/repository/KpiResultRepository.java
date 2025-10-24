package com.nexigroup.pagopa.cruscotto.repository;

import com.nexigroup.pagopa.cruscotto.domain.KpiResult;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.ModuleCode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Generic repository for KPI results that works with any KPI type.
 * Eliminates the need for KPI-specific repositories.
 */
@Repository
public interface KpiResultRepository extends JpaRepository<KpiResult, Long>, JpaSpecificationExecutor<KpiResult> {

    /**
     * Find all results for a specific module code
     */
    List<KpiResult> findByModuleCode(ModuleCode moduleCode);

    /**
     * Find result by instance module ID and module code
     */
    Optional<KpiResult> findByInstanceModuleIdAndModuleCode(Long instanceModuleId, ModuleCode moduleCode);

    /**
     * Find results by module code and instance module ID (different parameter order)
     */
    List<KpiResult> findByModuleCodeAndInstanceModuleId(ModuleCode moduleCode, Long instanceModuleId);

    /**
     * Find results by module code, instance module ID and outcome
     */
    List<KpiResult> findByModuleCodeAndInstanceModuleIdAndOutcome(ModuleCode moduleCode, Long instanceModuleId, com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus outcome);

    /**
     * Delete by module code and instance module ID (different parameter order)
     */
    @Modifying
    @Query("DELETE FROM KpiResult kr WHERE kr.moduleCode = :moduleCode AND kr.instanceModuleId = :instanceModuleId")
    void deleteByModuleCodeAndInstanceModuleId(@Param("moduleCode") ModuleCode moduleCode, @Param("instanceModuleId") Long instanceModuleId);

    /**
     * Find results by instance ID and module code
     */
    List<KpiResult> findByInstanceIdAndModuleCode(Long instanceId, ModuleCode moduleCode);

    /**
     * Find results by partner fiscal code and module code
     */
    List<KpiResult> findByPartnerFiscalCodeAndModuleCode(String partnerFiscalCode, ModuleCode moduleCode);

    /**
     * Find results by analysis date range and module code
     */
    @Query("SELECT kr FROM KpiResult kr WHERE kr.moduleCode = :moduleCode " +
           "AND kr.analysisStartDate >= :startDate AND kr.analysisEndDate <= :endDate")
    List<KpiResult> findByAnalysisDateRangeAndModuleCode(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("moduleCode") ModuleCode moduleCode);

    /**
     * Find paginated results with filters
     */
    @Query("SELECT kr FROM KpiResult kr WHERE " +
           "(:moduleCode IS NULL OR kr.moduleCode = :moduleCode) AND " +
           "(:instanceId IS NULL OR kr.instanceId = :instanceId) AND " +
           "(:partnerFiscalCode IS NULL OR kr.partnerFiscalCode = :partnerFiscalCode) AND " +
           "(:startDate IS NULL OR kr.analysisStartDate >= :startDate) AND " +
           "(:endDate IS NULL OR kr.analysisEndDate <= :endDate)")
    Page<KpiResult> findWithFilters(
            @Param("moduleCode") ModuleCode moduleCode,
            @Param("instanceId") Long instanceId,
            @Param("partnerFiscalCode") String partnerFiscalCode,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable);

    /**
     * Delete all results for a specific instance module
     */
    @Modifying
    @Query("DELETE FROM KpiResult kr WHERE kr.instanceModuleId = :instanceModuleId AND kr.moduleCode = :moduleCode")
    void deleteByInstanceModuleIdAndModuleCode(@Param("instanceModuleId") Long instanceModuleId, @Param("moduleCode") ModuleCode moduleCode);

    /**
     * Delete all results for a specific instance
     */
    @Modifying
    @Query("DELETE FROM KpiResult kr WHERE kr.instanceId = :instanceId AND kr.moduleCode = :moduleCode")
    void deleteByInstanceIdAndModuleCode(@Param("instanceId") Long instanceId, @Param("moduleCode") ModuleCode moduleCode);

    /**
     * Count results by module code
     */
    long countByModuleCode(ModuleCode moduleCode);

    /**
     * Check if result exists for instance module and module code
     */
    boolean existsByInstanceModuleIdAndModuleCode(Long instanceModuleId, ModuleCode moduleCode);
}