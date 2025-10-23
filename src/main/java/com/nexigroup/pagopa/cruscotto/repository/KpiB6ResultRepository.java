package com.nexigroup.pagopa.cruscotto.repository;

import com.nexigroup.pagopa.cruscotto.domain.KpiB6Result;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data repository for the KpiB6Result entity.
 */
@Repository
public interface KpiB6ResultRepository extends JpaRepository<KpiB6Result, Long>, JpaSpecificationExecutor<KpiB6Result> {
    
    @Modifying
    @Query("DELETE KpiB6Result kpiB6Result WHERE kpiB6Result.instanceModule.id = :instanceModuleId")
    int deleteAllByInstanceModuleId(@Param("instanceModuleId") Long instanceModuleId);

    @Query("SELECT b FROM KpiB6Result b WHERE b.instanceModule.id = :instanceModuleId")
    List<KpiB6Result> selectByInstanceModuleId(@Param("instanceModuleId") Long instanceModuleId);
}