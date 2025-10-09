package com.nexigroup.pagopa.cruscotto.repository;

import com.nexigroup.pagopa.cruscotto.domain.KpiB1AnalyticDrillDown;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data repository for the KpiB1AnalyticDrillDown entity.
 */
@Repository
public interface KpiB1AnalyticDrillDownRepository extends JpaRepository<KpiB1AnalyticDrillDown, Long>, JpaSpecificationExecutor<KpiB1AnalyticDrillDown> {
    
    @Modifying
    @Query("DELETE KpiB1AnalyticDrillDown kpiB1AnalyticDrillDown WHERE kpiB1AnalyticDrillDown.instanceModule.id = :instanceModuleId")
    int deleteAllByInstanceModuleId(@Param("instanceModuleId") Long instanceModuleId);

    @Query("SELECT b FROM KpiB1AnalyticDrillDown b WHERE b.instanceModule.id = :instanceModuleId")
    List<KpiB1AnalyticDrillDown> selectByInstanceModuleId(@Param("instanceModuleId") Long instanceModuleId);
}