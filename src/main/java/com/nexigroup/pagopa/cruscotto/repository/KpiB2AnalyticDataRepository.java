package com.nexigroup.pagopa.cruscotto.repository;

import com.nexigroup.pagopa.cruscotto.domain.KpiB2AnalyticData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data repository for the KpiB2AnalyticData entity.
 */

@Repository
public interface KpiB2AnalyticDataRepository extends JpaRepository<KpiB2AnalyticData, Long>, JpaSpecificationExecutor<KpiB2AnalyticData> {
    @Modifying
    @Query("DELETE KpiB2AnalyticData kpiB2AnalyticData WHERE kpiB2AnalyticData.instanceModule.id = :instanceModuleId")
    int deleteAllByInstanceModuleId(@Param("instanceModuleId") Long instanceModuleId);

    @Query("""
        SELECT d.id
        FROM KpiB2AnalyticData d
        WHERE d.instance.id = :instanceId
          AND d.analysisDate = (
                SELECT MAX(dd.analysisDate)
                FROM KpiB2AnalyticData dd
                WHERE dd.instance.id = :instanceId
          )
    """)
    List<Long> findLatestAnalyticDataIdsByInstanceId(
        @Param("instanceId") Long instanceId
    );



}
