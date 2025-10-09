package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.Instance;
import com.nexigroup.pagopa.cruscotto.domain.InstanceModule;
import com.nexigroup.pagopa.cruscotto.domain.KpiB1Result;
import com.nexigroup.pagopa.cruscotto.domain.QKpiB1Result;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import com.nexigroup.pagopa.cruscotto.repository.InstanceModuleRepository;
import com.nexigroup.pagopa.cruscotto.repository.InstanceRepository;
import com.nexigroup.pagopa.cruscotto.repository.KpiB1ResultRepository;
import com.nexigroup.pagopa.cruscotto.service.KpiB1ResultService;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB1ResultDTO;
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
 * Service Implementation for managing {@link KpiB1Result}.
 */
@Service
@Transactional
public class KpiB1ResultServiceImpl implements KpiB1ResultService {

    private static final Logger LOGGER = LoggerFactory.getLogger(KpiB1ResultServiceImpl.class);

    private final InstanceRepository instanceRepository;

    private final InstanceModuleRepository instanceModuleRepository;

    private final KpiB1ResultRepository kpiB1ResultRepository;

    private final QueryBuilder queryBuilder;

    public KpiB1ResultServiceImpl(
        InstanceRepository instanceRepository,
        InstanceModuleRepository instanceModuleRepository,
        KpiB1ResultRepository kpiB1ResultRepository,
        QueryBuilder queryBuilder
    ) {
        this.instanceRepository = instanceRepository;
        this.instanceModuleRepository = instanceModuleRepository;
        this.kpiB1ResultRepository = kpiB1ResultRepository;
        this.queryBuilder = queryBuilder;
    }

    /**
     * Save kpiB1Result.
     *
     * @param kpiB1ResultDTO the entity to save.
     */
    @Override
    public KpiB1ResultDTO save(KpiB1ResultDTO kpiB1ResultDTO) {
        Instance instance = instanceRepository
            .findById(kpiB1ResultDTO.getInstanceId())
            .orElseThrow(() -> new IllegalArgumentException("Instance not found"));

        InstanceModule instanceModule = instanceModuleRepository
            .findById(kpiB1ResultDTO.getInstanceModuleId())
            .orElseThrow(() -> new IllegalArgumentException("InstanceModule not found"));

        KpiB1Result kpiB1Result = getKpiB1Result(kpiB1ResultDTO, instance, instanceModule);

        kpiB1Result = kpiB1ResultRepository.save(kpiB1Result);

        kpiB1ResultDTO.setId(kpiB1Result.getId());

        return kpiB1ResultDTO;
    }

    private static @NotNull KpiB1Result getKpiB1Result(KpiB1ResultDTO kpiB1ResultDTO, Instance instance, InstanceModule instanceModule) {
        KpiB1Result kpiB1Result = new KpiB1Result();
        kpiB1Result.setInstance(instance);
        kpiB1Result.setInstanceModule(instanceModule);
        kpiB1Result.setAnalysisDate(kpiB1ResultDTO.getAnalysisDate());
        kpiB1Result.setEvaluationType(kpiB1ResultDTO.getEvaluationType());
        kpiB1Result.setMinEntitiesThreshold(kpiB1ResultDTO.getMinEntitiesThreshold());
        kpiB1Result.setMinTransactionsThreshold(kpiB1ResultDTO.getMinTransactionsThreshold());
        kpiB1Result.setOutcome(kpiB1ResultDTO.getOutcome());

        return kpiB1Result;
    }

    private static @NotNull KpiB1ResultDTO getKpiB1ResultDTO(KpiB1Result kpiB1Result) {
        KpiB1ResultDTO kpiB1ResultDTO = new KpiB1ResultDTO();
        kpiB1ResultDTO.setId(kpiB1Result.getId());
        kpiB1ResultDTO.setInstanceId(kpiB1Result.getInstance().getId());
        kpiB1ResultDTO.setInstanceModuleId(kpiB1Result.getInstanceModule().getId());
        kpiB1ResultDTO.setAnalysisDate(kpiB1Result.getAnalysisDate());
        kpiB1ResultDTO.setEvaluationType(kpiB1Result.getEvaluationType());
        kpiB1ResultDTO.setMinEntitiesThreshold(kpiB1Result.getMinEntitiesThreshold());
        kpiB1ResultDTO.setMinTransactionsThreshold(kpiB1Result.getMinTransactionsThreshold());
        kpiB1ResultDTO.setOutcome(kpiB1Result.getOutcome());
        
        return kpiB1ResultDTO;
    }

    @Override
    public int deleteAllByInstanceModule(long instanceModuleId) {
        return kpiB1ResultRepository.deleteAllByInstanceModuleId(instanceModuleId);
    }

    @Override
    public void updateKpiB1ResultOutcome(Long resultId, OutcomeStatus outcome) {
        LOGGER.debug("Request to update KpiB1Result {} outcome status to {}", resultId, outcome);

        JPAUpdateClause jpql = queryBuilder.updateQuery(QKpiB1Result.kpiB1Result);

        jpql.set(QKpiB1Result.kpiB1Result.outcome, outcome).where(QKpiB1Result.kpiB1Result.id.eq(resultId)).execute();
    }

    public List<KpiB1ResultDTO> findByInstanceModuleId(long instanceModuleId) {
        return kpiB1ResultRepository
            .selectByInstanceModuleId(instanceModuleId)
            .stream()
            .map(KpiB1ResultServiceImpl::getKpiB1ResultDTO)
            .collect(Collectors.toList());
    }
}