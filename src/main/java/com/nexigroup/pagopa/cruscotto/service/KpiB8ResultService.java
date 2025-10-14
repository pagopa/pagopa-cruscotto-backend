package com.nexigroup.pagopa.cruscotto.service;

import com.nexigroup.pagopa.cruscotto.domain.KpiB8Result;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB8ResultDTO;

import java.util.List;

/**
 * Service Interface for managing {@link KpiB8Result}.
 */
public interface KpiB8ResultService {

    KpiB8ResultDTO save(KpiB8ResultDTO kpiB8ResultDTO);

    int deleteAllByInstanceModule(long instanceModuleId);

    void updateKpiB8ResultOutcome(long id, OutcomeStatus outcomeStatus);

    List<KpiB8ResultDTO> findByInstanceModuleId(long instanceModuleId);
}
