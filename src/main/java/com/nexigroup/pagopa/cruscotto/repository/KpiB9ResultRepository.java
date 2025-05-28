package com.nexigroup.pagopa.cruscotto.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.nexigroup.pagopa.cruscotto.domain.KpiB9Result;

import java.util.List;

/**
 * Spring Data repository for the KpiB9Result entity.
 */

@Repository
public interface KpiB9ResultRepository extends JpaRepository<KpiB9Result, Long>, JpaSpecificationExecutor<KpiB9Result> {
	
    @Modifying
    @Query("DELETE KpiB9Result kpiB9Result WHERE kpiB9Result.instanceModule.id = :instanceModuleId")
    int deleteAllByInstanceModuleId(@Param("instanceModuleId") Long instanceModuleId);

    @Query("SELECT a FROM KpiB9Result a WHERE a.instanceModule.id = :instanceModuleId")
    List<KpiB9Result> selectByInstanceModuleId(@Param("instanceModuleId") Long instanceModuleId);
}
