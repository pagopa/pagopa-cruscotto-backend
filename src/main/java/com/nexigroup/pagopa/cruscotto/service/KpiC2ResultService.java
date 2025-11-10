package com.nexigroup.pagopa.cruscotto.service;

import com.nexigroup.pagopa.cruscotto.domain.KpiC2Result;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiC2ResultDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiC2ResultDTO;

import java.util.List;

/**
 * Service Interface for managing {@link KpiC2Result}.
 */
public interface KpiC2ResultService {

    KpiC2ResultDTO save(KpiC2ResultDTO kpiC2ResultDTO);

    int deleteAllByInstanceModule(long instanceModuleId);

    void updateKpiC2ResultOutcome(long id, OutcomeStatus outcomeStatus);

    List<KpiC2ResultDTO> findByInstanceModuleId(long instanceModuleId);
}
