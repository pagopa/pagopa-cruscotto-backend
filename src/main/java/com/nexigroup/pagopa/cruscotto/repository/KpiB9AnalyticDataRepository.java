package com.nexigroup.pagopa.cruscotto.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.nexigroup.pagopa.cruscotto.domain.KpiB9AnalyticData;

import java.util.List;

/**
 * Spring Data repository for the KpiB9AnalyticData entity.
 */

@Repository
public interface KpiB9AnalyticDataRepository extends JpaRepository<KpiB9AnalyticData, Long>, JpaSpecificationExecutor<KpiB9AnalyticData> {
	
    @Modifying
    @Query("DELETE KpiB9AnalyticData kpiB9AnalyticData WHERE kpiB9AnalyticData.instanceModule.id = :instanceModuleId")
    int deleteAllByInstanceModuleId(@Param("instanceModuleId") Long instanceModuleId);

    @Query("SELECT a FROM KpiB9AnalyticData a WHERE a.instanceModule.id = :instanceModuleId")
    List<KpiB9AnalyticData> selectByInstanceModuleId(@Param("instanceModuleId") Long instanceModuleId);
}
