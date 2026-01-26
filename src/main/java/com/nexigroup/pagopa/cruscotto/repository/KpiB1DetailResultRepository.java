package com.nexigroup.pagopa.cruscotto.repository;

import com.nexigroup.pagopa.cruscotto.domain.KpiA2DetailResult;
import com.nexigroup.pagopa.cruscotto.domain.KpiB1DetailResult;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data repository for the KpiB1DetailResult entity.
 */
@Repository
public interface KpiB1DetailResultRepository extends JpaRepository<KpiB1DetailResult, Long>, JpaSpecificationExecutor<KpiB1DetailResult> {
    
    @Modifying
    @Query("DELETE KpiB1DetailResult kpiB1DetailResult WHERE kpiB1DetailResult.instanceModule.id = :instanceModuleId")
    int deleteAllByInstanceModuleId(@Param("instanceModuleId") Long instanceModuleId);

    @Query("SELECT b FROM KpiB1DetailResult b WHERE b.instanceModule.id = :instanceModuleId")
    List<KpiB1DetailResult> selectByInstanceModuleId(@Param("instanceModuleId") Long instanceModuleId);

    List<KpiB1DetailResult> findLatestByInstanceId(
        @Param("instanceId") Long instanceId
    );
}