package com.nexigroup.pagopa.cruscotto.repository;

import com.nexigroup.pagopa.cruscotto.service.dto.KpiB2AnalyticDataDTO;
import java.util.List;

public interface KpiB2AnalyticDataRepositoryCustom {
    List<KpiB2AnalyticDataDTO> findByDetailResultId(long detailResultId);
}
