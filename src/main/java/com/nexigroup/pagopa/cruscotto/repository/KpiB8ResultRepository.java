package com.nexigroup.pagopa.cruscotto.repository;

import com.nexigroup.pagopa.cruscotto.domain.KpiB8Result;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data repository for the KpiB8Result entity.
 */

@Repository
public interface KpiB8ResultRepository extends JpaRepository<KpiB8Result, Long>, JpaSpecificationExecutor<KpiB8Result> {
    @Modifying(clearAutomatically = true)
    @Query("DELETE KpiB8Result kpiB8Result WHERE kpiB8Result.instanceModule.id = :instanceModuleId")
    int deleteAllByInstanceModuleId(@Param("instanceModuleId") Long instanceModuleId);

    @Query(
        "SELECT kpiB8Result FROM KpiB8Result kpiB8Result WHERE kpiB8Result.instanceModule.id = :instanceModuleId ORDER BY kpiB8Result.analysisDate DESC"
    )
    List<KpiB8Result> findAllByInstanceModuleIdOrderByAnalysisDateDesc(@Param("instanceModuleId") Long instanceModuleId);
}
