package com.nexigroup.pagopa.cruscotto.repository;

import com.nexigroup.pagopa.cruscotto.domain.KpiC2Result;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data repository for the KpiC2Result entity.
 */
@Repository
public interface KpiC2ResultRepository extends JpaRepository<KpiC2Result, Long>, JpaSpecificationExecutor<KpiC2Result> {

    @Modifying
    @Query("DELETE KpiC2Result kpiC2Result WHERE kpiC2Result.instanceModule.id = :instanceModuleId")
    int deleteAllByInstanceModuleId(@Param("instanceModuleId") Long instanceModuleId);

    @Query("SELECT b FROM KpiC2Result b WHERE b.instanceModule.id = :instanceModuleId")
    List<KpiC2Result> selectByInstanceModuleId(@Param("instanceModuleId") Long instanceModuleId);

    @Query("SELECT b FROM KpiC2Result b WHERE b.instanceModule.id = :instanceModuleId ORDER BY b.analysisDate DESC")
    List<KpiC2Result> findAllByInstanceModuleIdOrderByAnalysisDateDesc(@Param("instanceModuleId") Long instanceModuleId);
}
