package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.Instance;
import com.nexigroup.pagopa.cruscotto.domain.InstanceModule;
import com.nexigroup.pagopa.cruscotto.domain.KpiB9Result;
import com.nexigroup.pagopa.cruscotto.domain.QKpiB9Result;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import com.nexigroup.pagopa.cruscotto.repository.InstanceModuleRepository;
import com.nexigroup.pagopa.cruscotto.repository.InstanceRepository;
import com.nexigroup.pagopa.cruscotto.repository.KpiB9ResultRepository;
import com.nexigroup.pagopa.cruscotto.service.KpiB9ResultService;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB9ResultDTO;
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
 * Service Implementation for managing {@link KpiB9Result}.
 */
@Service
@Transactional
public class KpiB9ResultServiceImpl implements KpiB9ResultService {

    private static final Logger LOGGER = LoggerFactory.getLogger(KpiB9ResultServiceImpl.class);

    private final InstanceRepository instanceRepository;

    private final InstanceModuleRepository instanceModuleRepository;

    private final KpiB9ResultRepository kpiB9ResultRepository;

    private final QueryBuilder queryBuilder;

    public KpiB9ResultServiceImpl(
        InstanceRepository instanceRepository,
        InstanceModuleRepository instanceModuleRepository,
        KpiB9ResultRepository kpiB9ResultRepository,
        QueryBuilder queryBuilder
    ) {
        this.instanceRepository = instanceRepository;
        this.instanceModuleRepository = instanceModuleRepository;
        this.kpiB9ResultRepository = kpiB9ResultRepository;
        this.queryBuilder = queryBuilder;
    }

    /**
     * Save kpiB9Result.
     *
     * @param kpiB9ResultDTO the entity to save.
     */
    @Override
    public KpiB9ResultDTO save(KpiB9ResultDTO kpiB9ResultDTO) {
        Instance instance = instanceRepository
            .findById(kpiB9ResultDTO.getInstanceId())
            .orElseThrow(() -> new IllegalArgumentException("Instance not found"));

        InstanceModule instanceModule = instanceModuleRepository
            .findById(kpiB9ResultDTO.getInstanceModuleId())
            .orElseThrow(() -> new IllegalArgumentException("InstanceModule not found"));

        KpiB9Result kpiB9Result = getKpiB9Result(kpiB9ResultDTO, instance, instanceModule);

        kpiB9Result = kpiB9ResultRepository.save(kpiB9Result);

        kpiB9ResultDTO.setId(kpiB9Result.getId());

        return kpiB9ResultDTO;
    }

    private static @NotNull KpiB9Result getKpiB9Result(KpiB9ResultDTO kpiB9ResultDTO, Instance instance, InstanceModule instanceModule) {
        KpiB9Result kpiB9Result = new KpiB9Result();
        kpiB9Result.setInstance(instance);
        kpiB9Result.setInstanceModule(instanceModule);
        kpiB9Result.setAnalysisDate(kpiB9ResultDTO.getAnalysisDate());
        kpiB9Result.setExcludePlannedShutdown(kpiB9ResultDTO.getExcludePlannedShutdown());
        kpiB9Result.setExcludeUnplannedShutdown(kpiB9ResultDTO.getExcludeUnplannedShutdown());
        kpiB9Result.setEligibilityThreshold(kpiB9ResultDTO.getEligibilityThreshold());
        kpiB9Result.setTolerance(kpiB9ResultDTO.getTolerance());
        kpiB9Result.setEvaluationType(kpiB9ResultDTO.getEvaluationType());
        kpiB9Result.setOutcome(kpiB9ResultDTO.getOutcome());

        return kpiB9Result;
    }

    private static @NotNull KpiB9ResultDTO getKpiB9ResultDTO(KpiB9Result kpiB9Result) {
        KpiB9ResultDTO kpiB9ResultDTO = new KpiB9ResultDTO();
        kpiB9ResultDTO.setId(kpiB9Result.getId());
        kpiB9ResultDTO.setInstanceModuleId((kpiB9Result.getInstanceModule().getId()));
        kpiB9ResultDTO.setAnalysisDate(kpiB9Result.getAnalysisDate());
        kpiB9ResultDTO.setExcludePlannedShutdown(kpiB9Result.getExcludePlannedShutdown());
        kpiB9ResultDTO.setExcludeUnplannedShutdown(kpiB9Result.getExcludeUnplannedShutdown());
        kpiB9ResultDTO.setEligibilityThreshold(kpiB9Result.getEligibilityThreshold());
        kpiB9ResultDTO.setTolerance(kpiB9Result.getTolerance());
        kpiB9ResultDTO.setEvaluationType(kpiB9Result.getEvaluationType());
        kpiB9ResultDTO.setOutcome(kpiB9Result.getOutcome());
        return kpiB9ResultDTO;
    }

    @Override
    public int deleteAllByInstanceModule(long instanceModuleId) {
        return kpiB9ResultRepository.deleteAllByInstanceModuleId(instanceModuleId);
    }

    @Override
    public void updateKpiB9ResultOutcome(long id, OutcomeStatus outcomeStatus) {
        LOGGER.debug("Request to update KpiB9Result {} outcome status to {}", id, outcomeStatus);

        JPAUpdateClause jpql = queryBuilder.updateQuery(QKpiB9Result.kpiB9Result);

        jpql.set(QKpiB9Result.kpiB9Result.outcome, outcomeStatus).where(QKpiB9Result.kpiB9Result.id.eq(id)).execute();
    }

    @Override
    public List<KpiB9ResultDTO> findByInstanceModuleId(long instanceModuleId) {
        return kpiB9ResultRepository
            .selectByInstanceModuleId(instanceModuleId)
            .stream()
            .map(KpiB9ResultServiceImpl::getKpiB9ResultDTO)
            .collect(Collectors.toList());
    }
}
