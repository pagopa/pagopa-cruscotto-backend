package com.nexigroup.pagopa.cruscotto.service;

import com.nexigroup.pagopa.cruscotto.domain.Instance;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.AnalysisOutcome;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.ModuleCode;
import com.nexigroup.pagopa.cruscotto.service.bean.InstanceRequestBean;
import com.nexigroup.pagopa.cruscotto.service.dto.InstanceDTO;
import com.nexigroup.pagopa.cruscotto.service.filter.InstanceFilter;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link Instance}.
 */
public interface InstanceService {
    /**
     * Get all the instance by filter.
     *
     * @param filter the pagination filter.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<InstanceDTO> findAll(InstanceFilter filter, Pageable pageable);

    Optional<InstanceDTO> findOne(Long id);

    InstanceDTO saveNew(InstanceRequestBean instance);

    InstanceDTO update(InstanceRequestBean instance);

    InstanceDTO delete(Long id);

    InstanceDTO updateStatus(Long id);

    List<InstanceDTO> findInstanceToCalculate(ModuleCode moduleCode, Integer limit);

    List<InstanceDTO> findInstanceToCalculate(Integer limit);

    void updateExecuteStateAndLastAnalysis(Long id, Instant lastAnalysisDate, AnalysisOutcome lastAnalysisOutcome);

    void updateInstanceStatusInProgress(long id);
}
