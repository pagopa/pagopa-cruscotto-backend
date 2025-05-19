package com.nexigroup.pagopa.cruscotto.repository;

import com.nexigroup.pagopa.cruscotto.domain.KpiB2Result;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.nexigroup.pagopa.cruscotto.domain.KpiB2Result;

import java.util.List;

/**
 * Spring Data repository for the KpiB2Result entity.
 */
@Repository
public interface KpiB2ResultRepository extends JpaRepository<KpiB2Result, Long>, JpaSpecificationExecutor<KpiB2Result> {

    @Modifying
    @Query("DELETE KpiB2Result kpiB2Result WHERE kpiB2Result.instanceModule.id = :instanceModuleId")
    int deleteAllByInstanceModuleId(@Param("instanceModuleId") Long instanceModuleId);

    @Query("SELECT b FROM KpiB2Result b WHERE b.instanceModule.id = :instanceModuleId")
    List<KpiB2Result> selectByInstanceModuleId(@Param("instanceModuleId") Long instanceModuleId);
}
