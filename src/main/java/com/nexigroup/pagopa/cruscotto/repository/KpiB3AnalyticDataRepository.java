package com.nexigroup.pagopa.cruscotto.repository;

import com.nexigroup.pagopa.cruscotto.domain.KpiB3AnalyticData;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data repository for the KpiB3AnalyticData entity.
 */

@Repository
public interface KpiB3AnalyticDataRepository extends JpaRepository<KpiB3AnalyticData, Long>, JpaSpecificationExecutor<KpiB3AnalyticData> {
    @Modifying(clearAutomatically = true)
    @Query("DELETE KpiB3AnalyticData kpiB3AnalyticData WHERE kpiB3AnalyticData.instanceModule.id = :instanceModuleId")
    int deleteAllByInstanceModuleId(@Param("instanceModuleId") Long instanceModuleId);

    @Query(
        "SELECT kpiB3AnalyticData FROM KpiB3AnalyticData kpiB3AnalyticData WHERE kpiB3AnalyticData.instanceModule.id = :instanceModuleId ORDER BY kpiB3AnalyticData.eventTimestamp DESC"
    )
    List<KpiB3AnalyticData> findAllByInstanceModuleIdOrderByEventTimestampDesc(@Param("instanceModuleId") Long instanceModuleId);

    @Query(
        "SELECT kpiB3AnalyticData FROM KpiB3AnalyticData kpiB3AnalyticData WHERE kpiB3AnalyticData.kpiB3DetailResult.id = :detailResultId ORDER BY kpiB3AnalyticData.eventTimestamp DESC"
    )
    List<KpiB3AnalyticData> findAllByDetailResultIdOrderByEventTimestampDesc(@Param("detailResultId") Long detailResultId);
}