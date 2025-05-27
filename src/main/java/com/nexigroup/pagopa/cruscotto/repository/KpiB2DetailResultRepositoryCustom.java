package com.nexigroup.pagopa.cruscotto.repository;

import com.nexigroup.pagopa.cruscotto.service.dto.KpiB2DetailResultDTO;
import java.util.List;

public interface KpiB2DetailResultRepositoryCustom {
    /**
     * Trova i risultati dettagliati utilizzando kpiB2ResultId.
     *
     * @param resultId Id di riferimento di KpiB2Result.
     * @return Lista di risultati dettagliati.
     */
    List<KpiB2DetailResultDTO> findByKpiB2ResultId(long resultId);
}
