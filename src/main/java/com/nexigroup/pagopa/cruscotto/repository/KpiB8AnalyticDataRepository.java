
package com.nexigroup.pagopa.cruscotto.repository;

import com.nexigroup.pagopa.cruscotto.domain.KpiB8AnalyticData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data repository for the KpiB8AnalyticData entity.
 */

@Repository
public interface KpiB8AnalyticDataRepository extends JpaRepository<KpiB8AnalyticData, Long>, JpaSpecificationExecutor<KpiB8AnalyticData> {
    @Modifying(clearAutomatically = true)
    @Query("DELETE KpiB8AnalyticData kpiB8AnalyticData WHERE kpiB8AnalyticData.instanceModule.id = :instanceModuleId")
    int deleteAllByInstanceModuleId(@Param("instanceModuleId") Long instanceModuleId);

    @Query(
        "SELECT kpiB8AnalyticData FROM KpiB8AnalyticData kpiB8AnalyticData WHERE kpiB8AnalyticData.instanceModule.id = :instanceModuleId ORDER BY kpiB8AnalyticData.eventTimestamp DESC"
    )
    List<KpiB8AnalyticData> findAllByInstanceModuleIdOrderByEventTimestampDesc(@Param("instanceModuleId") Long instanceModuleId);

    @Query(
        "SELECT kpiB8AnalyticData FROM KpiB8AnalyticData kpiB8AnalyticData WHERE kpiB8AnalyticData.kpiB8DetailResult.id = :detailResultId ORDER BY kpiB8AnalyticData.eventTimestamp DESC"
    )
    List<KpiB8AnalyticData> findAllByDetailResultIdOrderByEventTimestampDesc(@Param("detailResultId") Long detailResultId);
}
