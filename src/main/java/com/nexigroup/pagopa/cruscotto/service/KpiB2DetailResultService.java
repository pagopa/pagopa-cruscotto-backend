package com.nexigroup.pagopa.cruscotto.service;

import com.nexigroup.pagopa.cruscotto.domain.KpiB2DetailResult;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB2DetailResultDTO;
import java.util.List;

/**
 * Service Interface for managing {@link KpiB2DetailResult}.
 */

public interface KpiB2DetailResultService {
    KpiB2DetailResultDTO save(KpiB2DetailResultDTO kpiB2DetailResultDTO);

    int deleteAllByInstanceModule(long instanceModuleId);

    List<KpiB2DetailResultDTO> findByResultId(long resultId);
}
