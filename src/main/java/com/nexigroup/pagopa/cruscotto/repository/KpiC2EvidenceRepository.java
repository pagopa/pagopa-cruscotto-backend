package com.nexigroup.pagopa.cruscotto.repository;

import com.nexigroup.pagopa.cruscotto.domain.KpiC2Evidence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data repository for the KpiC2Evidence entity.
 */
@Repository
public interface KpiC2EvidenceRepository extends JpaRepository<KpiC2Evidence, Long>, JpaSpecificationExecutor<KpiC2Evidence> {

    @Modifying
    @Query("DELETE KpiC2Evidence KpiC2Evidence WHERE KpiC2Evidence.kpiC2AnalyticData.instanceModule.id = :instanceModuleId")
    int deleteAllByInstanceModuleId(@Param("instanceModuleId") Long instanceModuleId);

    @Query("SELECT b FROM KpiC2Evidence b WHERE b.kpiC2AnalyticData.id = :kpiC2AnalyticDataId")
    List<KpiC2Evidence> selectByKpiC2AnalyticDataId(@Param("kpiC2AnalyticDataId") Long kpiC2AnalyticDataId);

    @Query("SELECT b FROM KpiC2Evidence b WHERE b.partnerFiscalCode = :partnerFiscalCode")
    List<KpiC2Evidence> selectByPartnerFiscalCode(@Param("partnerFiscalCode") String partnerFiscalCode);
}
