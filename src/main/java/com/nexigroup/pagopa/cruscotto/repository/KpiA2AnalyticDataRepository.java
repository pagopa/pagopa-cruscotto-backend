package com.nexigroup.pagopa.cruscotto.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.nexigroup.pagopa.cruscotto.domain.KpiA2AnalyticData;

import java.util.List;

/**
 * Spring Data repository for the KpiA2AnalyticData entity.
 */

@Repository
public interface KpiA2AnalyticDataRepository extends JpaRepository<KpiA2AnalyticData, Long>, JpaSpecificationExecutor<KpiA2AnalyticData> {
	
    @Modifying
    @Query("DELETE KpiA2AnalyticData kpiA2AnalyticData WHERE kpiA2AnalyticData.instanceModule.id = :instanceModuleId")
    int deleteAllByInstanceModuleId(@Param("instanceModuleId") Long instanceModuleId);
    
    @Query("SELECT a FROM KpiA2AnalyticData a WHERE a.instanceModule.id = :instanceModuleId")
    List<KpiA2AnalyticData> selectByInstanceModuleId(@Param("instanceModuleId") Long instanceModuleId);
}
