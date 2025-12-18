package com.nexigroup.pagopa.cruscotto.repository;

import com.nexigroup.pagopa.cruscotto.domain.IoDrilldown;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repository for IoDrilldown negative evidences of KPI C.1.
 */
@Repository
public interface IoDrilldownRepository extends JpaRepository<IoDrilldown, Long> {

    @Query("SELECT i FROM IoDrilldown i WHERE i.kpiC1AnalyticData.id = :analyticDataId ORDER BY i.cfInstitution, i.dataDate")
    List<IoDrilldown> findByAnalyticDataId(@Param("analyticDataId") Long analyticDataId);

    @Query("SELECT i FROM IoDrilldown i WHERE i.instanceModule.id = :instanceModuleId AND i.referenceDate = :referenceDate")
    List<IoDrilldown> findByInstanceModuleAndReferenceDate(@Param("instanceModuleId") Long instanceModuleId,
                                                            @Param("referenceDate") java.time.LocalDate referenceDate);

    @Query("""
        SELECT i
        FROM IoDrilldown i
        WHERE i.instance.id = :instanceId
          AND i.dataDate = (
              SELECT MAX(ii.dataDate)
              FROM IoDrilldown ii
              WHERE ii.instance.id = :instanceId
          )
        ORDER BY i.cfInstitution, i.dataDate
    """)
    List<IoDrilldown> findLatestByInstanceId(@Param("instanceId") Long instanceId);

}



