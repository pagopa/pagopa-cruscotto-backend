package com.nexigroup.pagopa.cruscotto.repository;

import com.nexigroup.pagopa.cruscotto.domain.KpiB6AnalyticData;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data repository for the KpiB6AnalyticData entity.
 */
@Repository
public interface KpiB6AnalyticDataRepository extends JpaRepository<KpiB6AnalyticData, Long>, JpaSpecificationExecutor<KpiB6AnalyticData> {
    
    @Modifying
    @Query("DELETE KpiB6AnalyticData kpiB6AnalyticData WHERE kpiB6AnalyticData.instanceModule.id = :instanceModuleId")
    int deleteAllByInstanceModuleId(@Param("instanceModuleId") Long instanceModuleId);

    @Query("SELECT kpiB6AnalyticData FROM KpiB6AnalyticData kpiB6AnalyticData WHERE kpiB6AnalyticData.kpiB6DetailResult.id = :kpiB6DetailResultId")
    List<KpiB6AnalyticData> findAllByKpiB6DetailResultId(@Param("kpiB6DetailResultId") Long kpiB6DetailResultId);
}