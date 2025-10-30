package com.nexigroup.pagopa.cruscotto.repository;

import com.nexigroup.pagopa.cruscotto.domain.KpiC2AnalyticData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data repository for the KpiC2AnalyticData entity.
 */
@Repository
public interface KpiC2AnalyticDataRepository extends JpaRepository<KpiC2AnalyticData, Long>, JpaSpecificationExecutor<KpiC2AnalyticData> {

    @Modifying
    @Query("DELETE KpiC2AnalyticData kpiC2AnalyticData WHERE kpiC2AnalyticData.instanceModule.id = :instanceModuleId")
    int deleteAllByInstanceModuleId(@Param("instanceModuleId") Long instanceModuleId);

    @Query("SELECT b FROM KpiC2AnalyticData b WHERE b.instanceModule.id = :instanceModuleId")
    List<KpiC2AnalyticData> selectByInstanceModuleId(@Param("instanceModuleId") Long instanceModuleId);

    @Query("SELECT b FROM KpiC2AnalyticData b WHERE b.kpiC2DetailResult.id = :kpiC2DetailResultId")
    List<KpiC2AnalyticData> selectByKpiC2DetailResultId(@Param("kpiC2DetailResultId") Long kpiC2DetailResultId);
}
