package com.nexigroup.pagopa.cruscotto.repository;
import java.util.List;
import com.nexigroup.pagopa.cruscotto.domain.KpiA2AnalyticIncorrectTaxonomyData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface KpiA2AnalyticIncorrectTaxonomyDataRepository extends JpaRepository<KpiA2AnalyticIncorrectTaxonomyData, Long> {
	List<KpiA2AnalyticIncorrectTaxonomyData> findByKpiA2AnalyticDataIdOrderByFromHourAsc(Long analyticDataId);

	List<KpiA2AnalyticIncorrectTaxonomyData>
    findByKpiA2AnalyticDataIdInOrderByFromHourAsc(
        List<Long> analyticDataIds
    );

	@Modifying
	@Query("DELETE FROM KpiA2AnalyticIncorrectTaxonomyData t WHERE t.kpiA2AnalyticDataId IN " +
		"(SELECT a.id FROM KpiA2AnalyticData a WHERE a.instanceModule.id = :instanceModuleId)")
	int deleteAllByInstanceModuleId(@Param("instanceModuleId") Long instanceModuleId);
}
