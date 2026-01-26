package com.nexigroup.pagopa.cruscotto.repository;

import com.nexigroup.pagopa.cruscotto.domain.KpiA1DetailResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data repository for the KpiA1DetailResult entity.
 */

@Repository
public interface KpiA1DetailResultRepository extends JpaRepository<KpiA1DetailResult, Long>, JpaSpecificationExecutor<KpiA1DetailResult> {
    @Modifying
    @Query("DELETE KpiA1DetailResult kpiA1DetailResult WHERE kpiA1DetailResult.instanceModule.id = :instanceModuleId")
    int deleteAllByInstanceModuleId(@Param("instanceModuleId") Long instanceModuleId);

    @Query("""
        SELECT d
        FROM KpiA1DetailResult d
        WHERE d.instance.id = :instanceId
          AND d.analysisDate = (
              SELECT MAX(dd.analysisDate)
              FROM KpiA1DetailResult dd
              WHERE dd.instance.id = :instanceId
          )
        ORDER BY d.instanceModule.id ASC
    """)
    List<KpiA1DetailResult> findLatestByInstanceId(
        @Param("instanceId") Long instanceId
    );

}
