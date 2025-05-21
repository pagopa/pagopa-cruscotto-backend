package com.nexigroup.pagopa.cruscotto.repository;

import com.nexigroup.pagopa.cruscotto.domain.KpiA2DetailResult;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data repository for the KpiB2DetailResult entity.
 */
@Repository
public interface KpiA2DetailResultRepository extends JpaRepository<KpiA2DetailResult, Long>, JpaSpecificationExecutor<KpiA2DetailResult> {
    @Modifying
    @Query("DELETE KpiA2DetailResult kpiA2DetailResult WHERE kpiA2DetailResult.instanceModule.id = :instanceModuleId")
    int deleteAllByInstanceModuleId(@Param("instanceModuleId") Long instanceModuleId);

    @Query("SELECT a FROM KpiA2DetailResult a WHERE a.instanceModule.id = :instanceModuleId")
    List<KpiA2DetailResult> selectByInstanceModuleId(@Param("instanceModuleId") Long instanceModuleId);
}
