package com.nexigroup.pagopa.cruscotto.repository;

import com.nexigroup.pagopa.cruscotto.domain.KpiC2DetailResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data repository for the KpiC2DetailResult entity.
 */
@Repository
public interface KpiC2DetailResultRepository extends JpaRepository<KpiC2DetailResult, Long>, JpaSpecificationExecutor<KpiC2DetailResult> {

    @Modifying
    @Query("DELETE KpiC2DetailResult kpiC2DetailResult WHERE kpiC2DetailResult.instanceModule.id = :instanceModuleId")
    int deleteAllByInstanceModuleId(@Param("instanceModuleId") Long instanceModuleId);

    @Query("SELECT b FROM KpiC2DetailResult b WHERE b.instanceModule.id = :instanceModuleId")
    List<KpiC2DetailResult> selectByInstanceModuleId(@Param("instanceModuleId") Long instanceModuleId);

    @Query("SELECT b FROM KpiC2DetailResult b WHERE b.kpiC2Result.id = :kpiC2ResultId")
    List<KpiC2DetailResult> selectByKpiC2ResultId(@Param("kpiC2ResultId") Long kpiC2ResultId);
}
