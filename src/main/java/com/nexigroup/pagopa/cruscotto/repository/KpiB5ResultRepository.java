package com.nexigroup.pagopa.cruscotto.repository;

import com.nexigroup.pagopa.cruscotto.domain.KpiB5Result;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data repository for the KpiB5Result entity.
 */
@Repository
public interface KpiB5ResultRepository extends JpaRepository<KpiB5Result, Long>, JpaSpecificationExecutor<KpiB5Result> {
    
    @Modifying
    @Query("DELETE KpiB5Result kpiB5Result WHERE kpiB5Result.instanceModule.id = :instanceModuleId")
    int deleteAllByInstanceModuleId(@Param("instanceModuleId") Long instanceModuleId);

    @Query("SELECT b FROM KpiB5Result b WHERE b.instanceModule.id = :instanceModuleId")
    List<KpiB5Result> selectByInstanceModuleId(@Param("instanceModuleId") Long instanceModuleId);
    
    @Query("SELECT b FROM KpiB5Result b WHERE b.instanceModule.id = :instanceModuleId ORDER BY b.analysisDate DESC")
    List<KpiB5Result> findAllByInstanceModuleIdOrderByAnalysisDateDesc(@Param("instanceModuleId") Long instanceModuleId);
}