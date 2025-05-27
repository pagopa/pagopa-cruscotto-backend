package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.Instance;
import com.nexigroup.pagopa.cruscotto.domain.InstanceModule;
import com.nexigroup.pagopa.cruscotto.domain.KpiB2Result;
import com.nexigroup.pagopa.cruscotto.domain.QKpiB2Result;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import com.nexigroup.pagopa.cruscotto.repository.InstanceModuleRepository;
import com.nexigroup.pagopa.cruscotto.repository.InstanceRepository;
import com.nexigroup.pagopa.cruscotto.repository.KpiB2ResultRepository;
import com.nexigroup.pagopa.cruscotto.service.KpiB2ResultService;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB2ResultDTO;
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
 * Service Implementation for managing {@link KpiB2Result}.
 */
@Service
@Transactional
public class KpiB2ResultServiceImpl implements KpiB2ResultService {

    private static final Logger LOGGER = LoggerFactory.getLogger(KpiB2ResultServiceImpl.class);

    private final InstanceRepository instanceRepository;

    private final InstanceModuleRepository instanceModuleRepository;

    private final KpiB2ResultRepository kpiB2ResultRepository;

    private final QueryBuilder queryBuilder;

    public KpiB2ResultServiceImpl(
        InstanceRepository instanceRepository,
        InstanceModuleRepository instanceModuleRepository,
        KpiB2ResultRepository kpiB2ResultRepository,
        QueryBuilder queryBuilder
    ) {
        this.instanceRepository = instanceRepository;
        this.instanceModuleRepository = instanceModuleRepository;
        this.kpiB2ResultRepository = kpiB2ResultRepository;
        this.queryBuilder = queryBuilder;
    }

    /**
     * Save kpiB2Result.
     *
     * @param kpiB2ResultDTO the entity to save.
     */
    @Override
    public KpiB2ResultDTO save(KpiB2ResultDTO kpiB2ResultDTO) {
        Instance instance = instanceRepository
            .findById(kpiB2ResultDTO.getInstanceId())
            .orElseThrow(() -> new IllegalArgumentException("Instance not found"));

        InstanceModule instanceModule = instanceModuleRepository
            .findById(kpiB2ResultDTO.getInstanceModuleId())
            .orElseThrow(() -> new IllegalArgumentException("InstanceModule not found"));

        KpiB2Result kpiB2Result = getKpiB2Result(kpiB2ResultDTO, instance, instanceModule);

        kpiB2Result = kpiB2ResultRepository.save(kpiB2Result);

        kpiB2ResultDTO.setId(kpiB2Result.getId());

        return kpiB2ResultDTO;
    }

    private static @NotNull KpiB2Result getKpiB2Result(KpiB2ResultDTO kpiB2ResultDTO, Instance instance, InstanceModule instanceModule) {
        KpiB2Result kpiB2Result = new KpiB2Result();
        kpiB2Result.setInstance(instance);
        kpiB2Result.setInstanceModule(instanceModule);
        kpiB2Result.setAnalysisDate(kpiB2ResultDTO.getAnalysisDate());
        kpiB2Result.setExcludePlannedShutdown(kpiB2ResultDTO.getExcludePlannedShutdown());
        kpiB2Result.setExcludeUnplannedShutdown(kpiB2ResultDTO.getExcludeUnplannedShutdown());
        kpiB2Result.setEligibilityThreshold(kpiB2ResultDTO.getEligibilityThreshold());
        kpiB2Result.setTolerance(kpiB2ResultDTO.getTolerance());
        kpiB2Result.setAverageTimeLimit(kpiB2ResultDTO.getAverageTimeLimit());
        kpiB2Result.setEvaluationType(kpiB2ResultDTO.getEvaluationType());
        kpiB2Result.setOutcome(kpiB2ResultDTO.getOutcome());

        return kpiB2Result;
    }

    private static @NotNull KpiB2ResultDTO getKpiB2ResultDTO(KpiB2Result kpiB2Result) {
        KpiB2ResultDTO kpiB2ResultDTO = new KpiB2ResultDTO();
        kpiB2ResultDTO.setId(kpiB2Result.getId());
        kpiB2ResultDTO.setInstanceModuleId((kpiB2Result.getInstanceModule().getId()));
        kpiB2ResultDTO.setAnalysisDate(kpiB2Result.getAnalysisDate());
        kpiB2ResultDTO.setExcludePlannedShutdown(kpiB2Result.getExcludePlannedShutdown());
        kpiB2ResultDTO.setExcludeUnplannedShutdown(kpiB2Result.getExcludeUnplannedShutdown());
        kpiB2ResultDTO.setEligibilityThreshold(kpiB2Result.getEligibilityThreshold());
        kpiB2ResultDTO.setTolerance(kpiB2Result.getTolerance());
        kpiB2ResultDTO.setEvaluationType(kpiB2Result.getEvaluationType());
        kpiB2ResultDTO.setOutcome(kpiB2Result.getOutcome());
        return kpiB2ResultDTO;
    }

    @Override
    public int deleteAllByInstanceModule(long instanceModuleId) {
        return kpiB2ResultRepository.deleteAllByInstanceModuleId(instanceModuleId);
    }

    @Override
    public void updateKpiB2ResultOutcome(long id, OutcomeStatus outcomeStatus) {
        LOGGER.debug("Request to update KpiB2Result {} outcome status to {}", id, outcomeStatus);

        JPAUpdateClause jpql = queryBuilder.updateQuery(QKpiB2Result.kpiB2Result);

        jpql.set(QKpiB2Result.kpiB2Result.outcome, outcomeStatus).where(QKpiB2Result.kpiB2Result.id.eq(id)).execute();
    }

    @Override
    public List<KpiB2ResultDTO> findByInstanceModuleId(long instanceModuleId) {
        return kpiB2ResultRepository
            .selectByInstanceModuleId(instanceModuleId)
            .stream()
            .map(KpiB2ResultServiceImpl::getKpiB2ResultDTO)
            .collect(Collectors.toList());
    }
}
