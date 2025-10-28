package com.nexigroup.pagopa.cruscotto.repository;

import com.nexigroup.pagopa.cruscotto.domain.KpiB5AnalyticData;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data repository for the KpiB5AnalyticData entity.
 */
@Repository
public interface KpiB5AnalyticDataRepository extends JpaRepository<KpiB5AnalyticData, Long>, JpaSpecificationExecutor<KpiB5AnalyticData> {
    
    @Modifying
    @Query("DELETE KpiB5AnalyticData kpiB5AnalyticData WHERE kpiB5AnalyticData.instanceModule.id = :instanceModuleId")
    int deleteAllByInstanceModuleId(@Param("instanceModuleId") Long instanceModuleId);

    @Query("SELECT b FROM KpiB5AnalyticData b WHERE b.instanceModule.id = :instanceModuleId")
    List<KpiB5AnalyticData> selectByInstanceModuleId(@Param("instanceModuleId") Long instanceModuleId);

    @Query("SELECT b FROM KpiB5AnalyticData b WHERE b.kpiB5DetailResult.id = :kpiB5DetailResultId")
    List<KpiB5AnalyticData> selectByKpiB5DetailResultId(@Param("kpiB5DetailResultId") Long kpiB5DetailResultId);
}