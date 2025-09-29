package com.nexigroup.pagopa.cruscotto.repository;

import com.nexigroup.pagopa.cruscotto.domain.KpiB3DetailResult;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data repository for the KpiB3DetailResult entity.
 */

@Repository
public interface KpiB3DetailResultRepository extends JpaRepository<KpiB3DetailResult, Long>, JpaSpecificationExecutor<KpiB3DetailResult> {
    @Modifying(clearAutomatically = true)
    @Query("DELETE KpiB3DetailResult kpiB3DetailResult WHERE kpiB3DetailResult.instanceModule.id = :instanceModuleId")
    int deleteAllByInstanceModuleId(@Param("instanceModuleId") Long instanceModuleId);

    @Query(
        "SELECT kpiB3DetailResult FROM KpiB3DetailResult kpiB3DetailResult WHERE kpiB3DetailResult.instanceModule.id = :instanceModuleId ORDER BY kpiB3DetailResult.analysisDate DESC"
    )
    List<KpiB3DetailResult> findAllByInstanceModuleIdOrderByAnalysisDateDesc(@Param("instanceModuleId") Long instanceModuleId);
}