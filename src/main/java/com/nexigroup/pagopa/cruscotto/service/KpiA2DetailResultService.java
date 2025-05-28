package com.nexigroup.pagopa.cruscotto.service;

import com.nexigroup.pagopa.cruscotto.domain.KpiA2DetailResult;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiA2DetailResultDTO;
import java.util.List;

/**
 * Service Interface for managing {@link KpiA2DetailResult}.
 */

public interface KpiA2DetailResultService {
    KpiA2DetailResultDTO save(KpiA2DetailResultDTO kpiA2DetailResultDTO);

    int deleteAllByInstanceModule(long instanceModuleId);

    List<KpiA2DetailResultDTO> findByResultId(long resultId);
}
