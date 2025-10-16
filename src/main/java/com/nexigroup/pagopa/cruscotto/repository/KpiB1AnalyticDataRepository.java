package com.nexigroup.pagopa.cruscotto.repository;

import com.nexigroup.pagopa.cruscotto.domain.KpiB1AnalyticData;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data repository for the KpiB1AnalyticData entity.
 */
@Repository
public interface KpiB1AnalyticDataRepository extends JpaRepository<KpiB1AnalyticData, Long>, JpaSpecificationExecutor<KpiB1AnalyticData> {
    
    @Modifying
    @Query("DELETE KpiB1AnalyticData kpiB1AnalyticData WHERE kpiB1AnalyticData.instanceModule.id = :instanceModuleId")
    int deleteAllByInstanceModuleId(@Param("instanceModuleId") Long instanceModuleId);

    @Query("SELECT b FROM KpiB1AnalyticData b WHERE b.instanceModule.id = :instanceModuleId")
    List<KpiB1AnalyticData> selectByInstanceModuleId(@Param("instanceModuleId") Long instanceModuleId);
}