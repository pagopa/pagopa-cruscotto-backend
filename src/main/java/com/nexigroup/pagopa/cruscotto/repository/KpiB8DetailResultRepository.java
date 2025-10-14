
package com.nexigroup.pagopa.cruscotto.repository;

import com.nexigroup.pagopa.cruscotto.domain.KpiB8DetailResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data repository for the KpiB8DetailResult entity.
 */

@Repository
public interface KpiB8DetailResultRepository extends JpaRepository<KpiB8DetailResult, Long>, JpaSpecificationExecutor<KpiB8DetailResult> {
    @Modifying(clearAutomatically = true)
    @Query("DELETE KpiB8DetailResult kpiB8DetailResult WHERE kpiB8DetailResult.instanceModule.id = :instanceModuleId")
    int deleteAllByInstanceModuleId(@Param("instanceModuleId") Long instanceModuleId);

    @Query(
        "SELECT kpiB8DetailResult FROM KpiB8DetailResult kpiB8DetailResult WHERE kpiB8DetailResult.instanceModule.id = :instanceModuleId ORDER BY kpiB8DetailResult.analysisDate DESC"
    )
    List<KpiB8DetailResult> findAllByInstanceModuleIdOrderByAnalysisDateDesc(@Param("instanceModuleId") Long instanceModuleId);

    @Query(
        "SELECT kpiB8DetailResult FROM KpiB8DetailResult kpiB8DetailResult WHERE kpiB8DetailResult.kpiB8Result.id = :resultId ORDER BY kpiB8DetailResult.analysisDate DESC"
    )
    List<KpiB8DetailResult> findAllByResultIdOrderByAnalysisDateDesc(@Param("resultId") Long resultId);
}
