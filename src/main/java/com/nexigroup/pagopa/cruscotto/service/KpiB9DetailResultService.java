package com.nexigroup.pagopa.cruscotto.service;

import com.nexigroup.pagopa.cruscotto.domain.KpiB9DetailResult;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB9DetailResultDTO;
import java.util.List;

/**
 * Service Interface for managing {@link KpiB9DetailResult}.
 */

public interface KpiB9DetailResultService {
    KpiB9DetailResultDTO save(KpiB9DetailResultDTO kpiB9DetailResultDTO);

    int deleteAllByInstanceModule(long instanceModuleId);

    List<KpiB9DetailResultDTO> findByResultId(long resultId);
}
