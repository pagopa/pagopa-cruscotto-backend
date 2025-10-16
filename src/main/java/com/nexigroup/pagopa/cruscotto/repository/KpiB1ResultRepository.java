package com.nexigroup.pagopa.cruscotto.repository;

import com.nexigroup.pagopa.cruscotto.domain.KpiB1Result;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data repository for the KpiB1Result entity.
 */
@Repository
public interface KpiB1ResultRepository extends JpaRepository<KpiB1Result, Long>, JpaSpecificationExecutor<KpiB1Result> {
    
    @Modifying
    @Query("DELETE KpiB1Result kpiB1Result WHERE kpiB1Result.instanceModule.id = :instanceModuleId")
    int deleteAllByInstanceModuleId(@Param("instanceModuleId") Long instanceModuleId);

    @Query("SELECT b FROM KpiB1Result b WHERE b.instanceModule.id = :instanceModuleId")
    List<KpiB1Result> selectByInstanceModuleId(@Param("instanceModuleId") Long instanceModuleId);
}