package com.nexigroup.pagopa.cruscotto.repository;

import com.nexigroup.pagopa.cruscotto.domain.KpiA2AnalyticData;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data repository for the KpiA2AnalyticData entity.
 */

@Repository
public interface KpiA2AnalyticDataRepository extends JpaRepository<KpiA2AnalyticData, Long>, JpaSpecificationExecutor<KpiA2AnalyticData> {
    @Modifying
    @Query("DELETE KpiA2AnalyticData kpiA2AnalyticData WHERE kpiA2AnalyticData.instanceModule.id = :instanceModuleId")
    int deleteAllByInstanceModuleId(@Param("instanceModuleId") Long instanceModuleId);
}
