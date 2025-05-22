package com.nexigroup.pagopa.cruscotto.repository;

import com.nexigroup.pagopa.cruscotto.domain.KpiA1DetailResult;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data repository for the KpiA1DetailResult entity.
 */

@Repository
public interface KpiA1DetailResultRepository extends JpaRepository<KpiA1DetailResult, Long>, JpaSpecificationExecutor<KpiA1DetailResult> {
    @Modifying
    @Query("DELETE KpiA1DetailResult kpiA1DetailResult WHERE kpiA1DetailResult.instanceModule.id = :instanceModuleId")
    int deleteAllByInstanceModuleId(@Param("instanceModuleId") Long instanceModuleId);

    @Query("SELECT a FROM KpiA1DetailResult a WHERE a.instanceModule.id = :instanceModuleId")
    List<KpiA1DetailResult> selectByInstanceModuleId(@Param("instanceModuleId") Long instanceModuleId);
}
