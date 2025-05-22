package com.nexigroup.pagopa.cruscotto.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.nexigroup.pagopa.cruscotto.domain.KpiB2DetailResult;

/**
 * Spring Data repository for the KpiB2DetailResult entity.
 */

@Repository
public interface KpiB2DetailResultRepository extends JpaRepository<KpiB2DetailResult, Long>, JpaSpecificationExecutor<KpiB2DetailResult> {
    @Modifying
    @Query("DELETE KpiB2DetailResult kpiB2DetailResult WHERE kpiB2DetailResult.instanceModule.id = :instanceModuleId")
    int deleteAllByInstanceModuleId(@Param("instanceModuleId") Long instanceModuleId);

    @Query("SELECT b FROM KpiB2DetailResult b WHERE b.instanceModule.id = :instanceModuleId")
    List<KpiB2DetailResult> selectByInstanceModuleId(@Param("instanceModuleId") Long instanceModuleId);
}
