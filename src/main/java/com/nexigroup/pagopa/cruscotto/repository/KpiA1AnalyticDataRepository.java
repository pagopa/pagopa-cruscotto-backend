package com.nexigroup.pagopa.cruscotto.repository;

import com.nexigroup.pagopa.cruscotto.domain.KpiA1AnalyticData;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data repository for the KpiA1AnalyticData entity.
 */
@Repository
public interface KpiA1AnalyticDataRepository extends JpaRepository<KpiA1AnalyticData, Long>, JpaSpecificationExecutor<KpiA1AnalyticData> {
    @Modifying
    @Query("DELETE KpiA1AnalyticData kpiA1AnalyticData WHERE kpiA1AnalyticData.instanceModule.id = :instanceModuleId")
    int deleteAllByInstanceModuleId(@Param("instanceModuleId") Long instanceModuleId);

    @Query("SELECT a FROM KpiA1AnalyticData a WHERE a.instanceModule.id = :instanceModuleId")
    List<KpiA1AnalyticData> selectByInstanceModuleId(@Param("instanceModuleId") Long instanceModuleId);
}
