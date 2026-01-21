package com.nexigroup.pagopa.cruscotto.repository;

import com.nexigroup.pagopa.cruscotto.domain.KpiA2DetailResult;
import com.nexigroup.pagopa.cruscotto.domain.KpiB9DetailResult;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data repository for the KpiB9DetailResult entity.
 */

@Repository
public interface KpiB9DetailResultRepository extends JpaRepository<KpiB9DetailResult, Long>, JpaSpecificationExecutor<KpiB9DetailResult> {
    @Modifying
    @Query("DELETE KpiB9DetailResult kpiB9DetailResult WHERE kpiB9DetailResult.instanceModule.id = :instanceModuleId")
    int deleteAllByInstanceModuleId(@Param("instanceModuleId") Long instanceModuleId);

    @Query("SELECT a FROM KpiB9DetailResult a WHERE a.instanceModule.id = :instanceModuleId")
    List<KpiB9DetailResult> selectByInstanceModuleId(@Param("instanceModuleId") Long instanceModuleId);

    List<KpiB9DetailResult> findLatestByInstanceId(
        @Param("instanceId") Long instanceId
    );
}
