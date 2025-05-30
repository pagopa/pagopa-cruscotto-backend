package com.nexigroup.pagopa.cruscotto.service;

import com.nexigroup.pagopa.cruscotto.domain.KpiB9Result;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB9ResultDTO;
import java.util.List;

/**
 * Service Interface for managing {@link KpiB9Result}.
 */

public interface KpiB9ResultService {
    KpiB9ResultDTO save(KpiB9ResultDTO kpiB9ResultDTO);

    int deleteAllByInstanceModule(long instanceModuleId);

    void updateKpiB9ResultOutcome(long id, OutcomeStatus outcomeStatus);

    List<KpiB9ResultDTO> findByInstanceModuleId(long instanceModuleId);
}
