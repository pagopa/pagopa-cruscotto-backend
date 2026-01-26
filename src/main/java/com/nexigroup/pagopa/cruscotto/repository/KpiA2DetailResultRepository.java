package com.nexigroup.pagopa.cruscotto.repository;

import com.nexigroup.pagopa.cruscotto.domain.KpiA2DetailResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data repository for the KpiA2DetailResult entity.
 */

@Repository
public interface KpiA2DetailResultRepository extends JpaRepository<KpiA2DetailResult, Long>, JpaSpecificationExecutor<KpiA2DetailResult> {
    @Modifying
    @Query("DELETE KpiA2DetailResult kpiA2DetailResult WHERE kpiA2DetailResult.instanceModule.id = :instanceModuleId")
    int deleteAllByInstanceModuleId(@Param("instanceModuleId") Long instanceModuleId);

    @Query("""
        SELECT d
        FROM KpiA2DetailResult d
        WHERE d.instance.id = :instanceId
          AND d.analysisDate = (
              SELECT MAX(dd.analysisDate)
              FROM KpiA2DetailResult dd
              WHERE dd.instance.id = :instanceId
          )
        ORDER BY d.instanceModule.id ASC
    """)
    List<KpiA2DetailResult> findLatestByInstanceId(
        @Param("instanceId") Long instanceId
    );
}
