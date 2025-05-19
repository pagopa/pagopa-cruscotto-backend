package com.nexigroup.pagopa.cruscotto.repository;

import com.nexigroup.pagopa.cruscotto.domain.KpiA1Result;
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
public interface KpiA1ResultRepository extends JpaRepository<KpiA1Result, Long>, JpaSpecificationExecutor<KpiA1Result> {

    @Modifying
    @Query("DELETE KpiA1Result kpiA1Result WHERE kpiA1Result.instanceModule.id = :instanceModuleId")
    int deleteAllByInstanceModuleId(@Param("instanceModuleId") Long instanceModuleId);

    @Query("SELECT a FROM KpiA1Result a WHERE a.instanceModule.id = :instanceModuleId")
    List<KpiA1Result> selectByInstanceModuleId(@Param("instanceModuleId") Long instanceModuleId);
}
