package com.nexigroup.pagopa.cruscotto.repository;

import com.nexigroup.pagopa.cruscotto.domain.KpiB6DetailResult;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data repository for the KpiB6DetailResult entity.
 */
@Repository
public interface KpiB6DetailResultRepository extends JpaRepository<KpiB6DetailResult, Long>, JpaSpecificationExecutor<KpiB6DetailResult> {
    
    @Modifying
    @Query("DELETE KpiB6DetailResult kpiB6DetailResult WHERE kpiB6DetailResult.instanceModule.id = :instanceModuleId")
    int deleteAllByInstanceModuleId(@Param("instanceModuleId") Long instanceModuleId);

    @Query("SELECT kpiB6DetailResult FROM KpiB6DetailResult kpiB6DetailResult WHERE kpiB6DetailResult.kpiB6Result.id = :kpiB6ResultId")
    List<KpiB6DetailResult> findAllByKpiB6ResultId(@Param("kpiB6ResultId") Long kpiB6ResultId);
}