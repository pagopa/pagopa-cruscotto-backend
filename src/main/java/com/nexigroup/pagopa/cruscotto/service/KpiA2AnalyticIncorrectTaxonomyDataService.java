package com.nexigroup.pagopa.cruscotto.service;

import com.nexigroup.pagopa.cruscotto.service.dto.KpiA2AnalyticIncorrectTaxonomyDataDTO;
import java.util.List;

public interface KpiA2AnalyticIncorrectTaxonomyDataService {
    void saveAll(List<KpiA2AnalyticIncorrectTaxonomyDataDTO> incorrectTaxonomyDataList);

    List<KpiA2AnalyticIncorrectTaxonomyDataDTO> findByKpiA2AnalyticDataId(Long analyticDataId);

    int deleteAllByInstanceModule(long instanceModuleId);
}
