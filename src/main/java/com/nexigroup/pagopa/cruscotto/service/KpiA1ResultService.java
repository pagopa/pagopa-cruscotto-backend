package com.nexigroup.pagopa.cruscotto.service;

import com.nexigroup.pagopa.cruscotto.domain.KpiA1Result;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiA1ResultDTO;
import java.util.List;

/**
 * Service Interface for managing {@link KpiA1Result}.
 */

public interface KpiA1ResultService {
    KpiA1ResultDTO save(KpiA1ResultDTO kpiA1ResultDTO);

    int deleteAllByInstanceModule(long instanceModuleId);

    void updateKpiA1ResultOutcome(long id, OutcomeStatus outcomeStatus);

    List<KpiA1ResultDTO> findByInstanceModuleId(long instanceModuleId);
}
