package com.nexigroup.pagopa.cruscotto.repository;

import com.nexigroup.pagopa.cruscotto.domain.KpiA2Result;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data repository for the KpiA1Result entity.
 */
@Repository
public interface KpiA2ResultRepository extends JpaRepository<KpiA2Result, Long>, JpaSpecificationExecutor<KpiA2Result> {

    @Modifying
    @Query("DELETE KpiA2Result kpiA2Result WHERE kpiA2Result.instanceModule.id = :instanceModuleId")
    int deleteAllByInstanceModuleId(@Param("instanceModuleId") Long instanceModuleId);

    @Query("SELECT a FROM KpiA2Result a WHERE a.instanceModule.id = :instanceModuleId")
    List<KpiA2Result> selectByInstanceModuleId(@Param("instanceModuleId") Long instanceModuleId);
}
