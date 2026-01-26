package com.nexigroup.pagopa.cruscotto.repository;

import com.nexigroup.pagopa.cruscotto.domain.KpiB5DetailResult;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data repository for the KpiB5DetailResult entity.
 */
@Repository
public interface KpiB5DetailResultRepository extends JpaRepository<KpiB5DetailResult, Long>, JpaSpecificationExecutor<KpiB5DetailResult> {

    @Modifying
    @Query("DELETE KpiB5DetailResult kpiB5DetailResult WHERE kpiB5DetailResult.instanceModule.id = :instanceModuleId")
    int deleteAllByInstanceModuleId(@Param("instanceModuleId") Long instanceModuleId);

    @Query("SELECT b FROM KpiB5DetailResult b WHERE b.instanceModule.id = :instanceModuleId")
    List<KpiB5DetailResult> selectByInstanceModuleId(@Param("instanceModuleId") Long instanceModuleId);

    @Query("SELECT b FROM KpiB5DetailResult b WHERE b.kpiB5Result.id = :kpiB5ResultId")
    List<KpiB5DetailResult> selectByKpiB5ResultId(@Param("kpiB5ResultId") Long kpiB5ResultId);

    @Query("""
        SELECT d
        FROM KpiB5DetailResult d
        WHERE d.instance.id = :instanceId
          AND d.analysisDate = (
              SELECT MAX(dd.analysisDate)
              FROM KpiB5DetailResult dd
              WHERE dd.instance.id = :instanceId
          )
        ORDER BY d.instanceModule.id ASC
    """)
    List<KpiB5DetailResult> findLatestByInstanceId(
        @Param("instanceId") Long instanceId
    );
}
