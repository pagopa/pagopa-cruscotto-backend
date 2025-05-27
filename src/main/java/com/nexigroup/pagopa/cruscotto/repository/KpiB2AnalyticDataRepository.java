package com.nexigroup.pagopa.cruscotto.repository;

import com.nexigroup.pagopa.cruscotto.domain.KpiB2AnalyticData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data repository for the KpiB2AnalyticData entity.
 */

@Repository
public interface KpiB2AnalyticDataRepository
    extends JpaRepository<KpiB2AnalyticData, Long>, JpaSpecificationExecutor<KpiB2AnalyticData>, KpiB2AnalyticDataRepositoryCustom {
    @Modifying
    @Query("DELETE KpiB2AnalyticData kpiB2AnalyticData WHERE kpiB2AnalyticData.instanceModule.id = :instanceModuleId")
    int deleteAllByInstanceModuleId(@Param("instanceModuleId") Long instanceModuleId);
}
