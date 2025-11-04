package com.nexigroup.pagopa.cruscotto.repository;

import com.nexigroup.pagopa.cruscotto.domain.KpiB4DetailResult;
import com.nexigroup.pagopa.cruscotto.domain.KpiC2AnalyticDrillDown;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * Spring Data repository for the KpiC2AnalyticDrillDown entity.
 */
@Repository
public interface KpiC2AnalyticDrillDownRepository extends JpaRepository<KpiC2AnalyticDrillDown, Long>, JpaSpecificationExecutor<KpiC2AnalyticDrillDown> {

    @Modifying
    @Query("DELETE KpiC2AnalyticDrillDown KpiC2AnalyticDrillDown WHERE KpiC2AnalyticDrillDown.kpiC2AnalyticData.instanceModule.id = :instanceModuleId")
    int deleteAllByInstanceModuleId(@Param("instanceModuleId") Long instanceModuleId);

    @Query("SELECT b FROM KpiC2AnalyticDrillDown b WHERE b.kpiC2AnalyticData.id = :kpiC2AnalyticDataId")
    List<KpiC2AnalyticDrillDown> selectByKpiC2AnalyticDataId(@Param("kpiC2AnalyticDataId") Long kpiC2AnalyticDataId);

    @Query("SELECT b FROM KpiC2AnalyticDrillDown b WHERE b.partnerCf = :partnerFiscalCode")
    List<KpiC2AnalyticDrillDown> selectByPartnerFiscalCode(@Param("partnerFiscalCode") String partnerFiscalCode);

    @Modifying
    @Query("DELETE FROM KpiC2AnalyticDrillDown d WHERE d.instanceModule.id = :instanceModuleId AND d.analysisDate = :analysisDate")
    int deleteByInstanceModuleIdAndAnalysisDate(@Param("instanceModuleId") Long instanceModuleId, @Param("analysisDate") LocalDate analysisDate);


    List<KpiC2AnalyticDrillDown> findByInstanceIdAndAnalysisDate(Long instanceId, LocalDate analysisDate);
}
