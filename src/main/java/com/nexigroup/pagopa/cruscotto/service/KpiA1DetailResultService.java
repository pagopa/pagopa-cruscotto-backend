package com.nexigroup.pagopa.cruscotto.service;

import com.nexigroup.pagopa.cruscotto.domain.KpiA1DetailResult;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiA1DetailResultDTO;
import java.util.List;

/**
 * Service Interface for managing {@link KpiA1DetailResult}.
 */

public interface KpiA1DetailResultService {
    KpiA1DetailResultDTO save(KpiA1DetailResultDTO kpiA1DetailResultDTO);

    int deleteAllByInstanceModule(long instanceModuleId);

    List<KpiA1DetailResultDTO> findByResultId(long resultId);
}
