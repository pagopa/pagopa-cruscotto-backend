package com.nexigroup.pagopa.cruscotto.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.nexigroup.pagopa.cruscotto.domain.KpiA2Result;

/**
 * Spring Data repository for the KpiA2Result entity.
 */

@Repository
public interface KpiA2ResultRepository extends JpaRepository<KpiA2Result, Long>, JpaSpecificationExecutor<KpiA2Result> {
	
    @Modifying
    @Query("DELETE KpiA2Result kpiA2Result WHERE kpiA2Result.instanceModule.id = :instanceModuleId")
    int deleteAllByInstanceModuleId(@Param("instanceModuleId") Long instanceModuleId); 
}
