package com.nexigroup.pagopa.cruscotto.repository;

import com.nexigroup.pagopa.cruscotto.domain.KpiB5AnalyticDrillDown;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data repository for the KpiB5AnalyticDrillDown entity.
 */
@Repository
public interface KpiB5AnalyticDrillDownRepository extends JpaRepository<KpiB5AnalyticDrillDown, Long>, JpaSpecificationExecutor<KpiB5AnalyticDrillDown> {
    
    @Modifying
    @Query("DELETE KpiB5AnalyticDrillDown kpiB5AnalyticDrillDown WHERE kpiB5AnalyticDrillDown.kpiB5AnalyticData.instanceModule.id = :instanceModuleId")
    int deleteAllByInstanceModuleId(@Param("instanceModuleId") Long instanceModuleId);

    @Query("SELECT b FROM KpiB5AnalyticDrillDown b WHERE b.kpiB5AnalyticData.id = :kpiB5AnalyticDataId")
    List<KpiB5AnalyticDrillDown> selectByKpiB5AnalyticDataId(@Param("kpiB5AnalyticDataId") Long kpiB5AnalyticDataId);

    @Query("SELECT b FROM KpiB5AnalyticDrillDown b WHERE b.partnerFiscalCode = :partnerFiscalCode")
    List<KpiB5AnalyticDrillDown> selectByPartnerFiscalCode(@Param("partnerFiscalCode") String partnerFiscalCode);
}