package com.nexigroup.pagopa.cruscotto.repository;

import com.nexigroup.pagopa.cruscotto.domain.KpiB3Result;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data repository for the KpiB3Result entity.
 */

@Repository
public interface KpiB3ResultRepository extends JpaRepository<KpiB3Result, Long>, JpaSpecificationExecutor<KpiB3Result> {
    @Modifying(clearAutomatically = true)
    @Query("DELETE KpiB3Result kpiB3Result WHERE kpiB3Result.instanceModule.id = :instanceModuleId")
    int deleteAllByInstanceModuleId(@Param("instanceModuleId") Long instanceModuleId);

    @Query(
        "SELECT kpiB3Result FROM KpiB3Result kpiB3Result WHERE kpiB3Result.instanceModule.id = :instanceModuleId ORDER BY kpiB3Result.analysisDate DESC"
    )
    List<KpiB3Result> findAllByInstanceModuleIdOrderByAnalysisDateDesc(@Param("instanceModuleId") Long instanceModuleId);
}