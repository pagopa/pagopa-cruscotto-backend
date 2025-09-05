package com.nexigroup.pagopa.cruscotto.repository;
import java.util.List;
import com.nexigroup.pagopa.cruscotto.domain.KpiA2AnalyticIncorrectTaxonomyData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KpiA2AnalyticIncorrectTaxonomyDataRepository extends JpaRepository<KpiA2AnalyticIncorrectTaxonomyData, Long> {
	List<KpiA2AnalyticIncorrectTaxonomyData> findByKpiA2AnalyticDataId(Long analyticDataId);
}
