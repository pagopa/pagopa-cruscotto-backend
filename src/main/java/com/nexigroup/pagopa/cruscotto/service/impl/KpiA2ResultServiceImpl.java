package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.Instance;
import com.nexigroup.pagopa.cruscotto.domain.InstanceModule;
import com.nexigroup.pagopa.cruscotto.domain.KpiA2Result;
import com.nexigroup.pagopa.cruscotto.domain.QKpiA2Result;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import com.nexigroup.pagopa.cruscotto.repository.InstanceModuleRepository;
import com.nexigroup.pagopa.cruscotto.repository.InstanceRepository;
import com.nexigroup.pagopa.cruscotto.repository.KpiA2ResultRepository;
import com.nexigroup.pagopa.cruscotto.service.KpiA2ResultService;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiA2ResultDTO;
import com.nexigroup.pagopa.cruscotto.service.qdsl.QueryBuilder;
import com.querydsl.jpa.impl.JPAUpdateClause;
import java.util.List;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link KpiA2Result}.
 */

@Service
@Transactional
public class KpiA2ResultServiceImpl implements KpiA2ResultService {

    private static final Logger LOGGER = LoggerFactory.getLogger(KpiA2ResultServiceImpl.class);

    private final InstanceRepository instanceRepository;

    private final InstanceModuleRepository instanceModuleRepository;

    private final KpiA2ResultRepository kpiA2ResultRepository;

    private final QueryBuilder queryBuilder;

    public KpiA2ResultServiceImpl(
        InstanceRepository instanceRepository,
        InstanceModuleRepository instanceModuleRepository,
        KpiA2ResultRepository kpiA2ResultRepository,
        QueryBuilder queryBuilder
    ) {
        this.instanceRepository = instanceRepository;
        this.instanceModuleRepository = instanceModuleRepository;
        this.kpiA2ResultRepository = kpiA2ResultRepository;
        this.queryBuilder = queryBuilder;
    }

    /**
     * Save kpiA2Result.
     *
     * @param kpiA2ResultDTO the entity to save.
     */
    @Override
    public KpiA2ResultDTO save(KpiA2ResultDTO kpiA2ResultDTO) {
        Instance instance = instanceRepository
            .findById(kpiA2ResultDTO.getInstanceId())
            .orElseThrow(() -> new IllegalArgumentException("Instance not found"));

        InstanceModule instanceModule = instanceModuleRepository
            .findById(kpiA2ResultDTO.getInstanceModuleId())
            .orElseThrow(() -> new IllegalArgumentException("InstanceModule not found"));

        KpiA2Result kpiA2Result = getKpiA2Result(kpiA2ResultDTO, instance, instanceModule);

        kpiA2Result = kpiA2ResultRepository.save(kpiA2Result);

        kpiA2ResultDTO.setId(kpiA2Result.getId());

        return kpiA2ResultDTO;
    }

    private static @NotNull KpiA2Result getKpiA2Result(KpiA2ResultDTO kpiA2ResultDTO, Instance instance, InstanceModule instanceModule) {
        KpiA2Result kpiA2Result = new KpiA2Result();
        kpiA2Result.setInstance(instance);
        kpiA2Result.setInstanceModule(instanceModule);
        kpiA2Result.setAnalysisDate(kpiA2ResultDTO.getAnalysisDate());
        kpiA2Result.setTolerance(kpiA2ResultDTO.getTolerance());
        kpiA2Result.setOutcome(kpiA2ResultDTO.getOutcome());

        return kpiA2Result;
    }

    @Override
    public int deleteAllByInstanceModule(long instanceModuleId) {
        return kpiA2ResultRepository.deleteAllByInstanceModuleId(instanceModuleId);
    }

    @Override
    public void updateKpiA2ResultOutcome(long id, OutcomeStatus outcomeStatus) {
        LOGGER.debug("Request to update KpiA2Result {} outcome status to {}", id, outcomeStatus);

        JPAUpdateClause jpql = queryBuilder.updateQuery(QKpiA2Result.kpiA2Result);

        jpql.set(QKpiA2Result.kpiA2Result.outcome, outcomeStatus).where(QKpiA2Result.kpiA2Result.id.eq(id)).execute();
    }

    @Override
    public List<KpiA2ResultDTO> findByInstanceModuleId(long instanceModuleId) {
        return kpiA2ResultRepository
            .selectByInstanceModuleId(instanceModuleId)
            .stream()
            .map(KpiA2ResultServiceImpl::getKpiA2ResultDTO)
            .collect(Collectors.toList());
    }

    private static @NotNull KpiA2ResultDTO getKpiA2ResultDTO(KpiA2Result kpiA2Result) {
        KpiA2ResultDTO kpiA2ResultDTO = new KpiA2ResultDTO();
        kpiA2ResultDTO.setId(kpiA2Result.getId());
        kpiA2ResultDTO.setInstanceModuleId((kpiA2Result.getInstanceModule().getId()));
        kpiA2ResultDTO.setAnalysisDate(kpiA2Result.getAnalysisDate());
        kpiA2ResultDTO.setTolerance(kpiA2Result.getTolerance());
        kpiA2ResultDTO.setOutcome(kpiA2Result.getOutcome());
        return kpiA2ResultDTO;
    }
}
