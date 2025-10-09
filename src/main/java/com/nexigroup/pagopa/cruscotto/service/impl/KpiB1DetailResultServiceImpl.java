package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.*;
import com.nexigroup.pagopa.cruscotto.repository.*;
import com.nexigroup.pagopa.cruscotto.service.KpiB1DetailResultService;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB1DetailResultDTO;
import com.nexigroup.pagopa.cruscotto.service.qdsl.QueryBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPQLQuery;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link KpiB1DetailResult}.
 */
@Service
@Transactional
public class KpiB1DetailResultServiceImpl implements KpiB1DetailResultService {

    private static final Logger LOGGER = LoggerFactory.getLogger(KpiB1DetailResultServiceImpl.class);

    private final InstanceRepository instanceRepository;

    private final InstanceModuleRepository instanceModuleRepository;

    private final KpiB1DetailResultRepository kpiB1DetailResultRepository;

    private final KpiB1ResultRepository kpiB1ResultRepository;

    private final QueryBuilder queryBuilder;

    public KpiB1DetailResultServiceImpl(
        InstanceRepository instanceRepository,
        InstanceModuleRepository instanceModuleRepository,
        KpiB1DetailResultRepository kpiB1DetailResultRepository,
        KpiB1ResultRepository kpiB1ResultRepository,
        QueryBuilder queryBuilder
    ) {
        this.instanceRepository = instanceRepository;
        this.instanceModuleRepository = instanceModuleRepository;
        this.kpiB1DetailResultRepository = kpiB1DetailResultRepository;
        this.kpiB1ResultRepository = kpiB1ResultRepository;
        this.queryBuilder = queryBuilder;
    }

    /**
     * Save kpiB1DetailResult.
     *
     * @param kpiB1DetailResultDTO the entity to save.
     */
    @Override
    public KpiB1DetailResultDTO save(KpiB1DetailResultDTO kpiB1DetailResultDTO) {
        Instance instance = instanceRepository
            .findById(kpiB1DetailResultDTO.getInstanceId())
            .orElseThrow(() -> new IllegalArgumentException("Instance not found"));

        InstanceModule instanceModule = instanceModuleRepository
            .findById(kpiB1DetailResultDTO.getInstanceModuleId())
            .orElseThrow(() -> new IllegalArgumentException("InstanceModule not found"));

        KpiB1Result kpiB1Result = kpiB1ResultRepository
            .findById(kpiB1DetailResultDTO.getKpiB1ResultId())
            .orElseThrow(() -> new IllegalArgumentException("KpiB1Result not found"));

        KpiB1DetailResult kpiB1DetailResult = getKpiB1DetailResult(
            kpiB1DetailResultDTO,
            instance,
            instanceModule,
            kpiB1Result
        );

        kpiB1DetailResult = kpiB1DetailResultRepository.save(kpiB1DetailResult);

        kpiB1DetailResultDTO.setId(kpiB1DetailResult.getId());

        return kpiB1DetailResultDTO;
    }

    private static @NotNull KpiB1DetailResult getKpiB1DetailResult(
        KpiB1DetailResultDTO kpiB1DetailResultDTO,
        Instance instance,
        InstanceModule instanceModule,
        KpiB1Result kpiB1Result
    ) {
        KpiB1DetailResult kpiB1DetailResult = new KpiB1DetailResult();
        kpiB1DetailResult.setInstance(instance);
        kpiB1DetailResult.setInstanceModule(instanceModule);
        kpiB1DetailResult.setAnalysisDate(kpiB1DetailResultDTO.getAnalysisDate());
        kpiB1DetailResult.setEvaluationType(kpiB1DetailResultDTO.getEvaluationType());
        kpiB1DetailResult.setEvaluationStartDate(kpiB1DetailResultDTO.getEvaluationStartDate());
        kpiB1DetailResult.setEvaluationEndDate(kpiB1DetailResultDTO.getEvaluationEndDate());
        kpiB1DetailResult.setTotalTransactions(kpiB1DetailResultDTO.getTotalTransactions());
        kpiB1DetailResult.setUniqueEntities(kpiB1DetailResultDTO.getUniqueEntities());
        kpiB1DetailResult.setOutcome(kpiB1DetailResultDTO.getOutcome());
        kpiB1DetailResult.setKpiB1Result(kpiB1Result);

        return kpiB1DetailResult;
    }

    private static @NotNull KpiB1DetailResultDTO getKpiB1DetailResultDTO(KpiB1DetailResult kpiB1DetailResult) {
        KpiB1DetailResultDTO kpiB1DetailResultDTO = new KpiB1DetailResultDTO();
        kpiB1DetailResultDTO.setId(kpiB1DetailResult.getId());
        kpiB1DetailResultDTO.setInstanceId(kpiB1DetailResult.getInstance().getId());
        kpiB1DetailResultDTO.setInstanceModuleId(kpiB1DetailResult.getInstanceModule().getId());
        kpiB1DetailResultDTO.setAnalysisDate(kpiB1DetailResult.getAnalysisDate());
        kpiB1DetailResultDTO.setEvaluationType(kpiB1DetailResult.getEvaluationType());
        kpiB1DetailResultDTO.setEvaluationStartDate(kpiB1DetailResult.getEvaluationStartDate());
        kpiB1DetailResultDTO.setEvaluationEndDate(kpiB1DetailResult.getEvaluationEndDate());
        kpiB1DetailResultDTO.setTotalTransactions(kpiB1DetailResult.getTotalTransactions());
        kpiB1DetailResultDTO.setUniqueEntities(kpiB1DetailResult.getUniqueEntities());
        kpiB1DetailResultDTO.setOutcome(kpiB1DetailResult.getOutcome());
        kpiB1DetailResultDTO.setKpiB1ResultId(kpiB1DetailResult.getKpiB1Result().getId());

        return kpiB1DetailResultDTO;
    }

    @Override
    public int deleteAllByInstanceModule(long instanceModuleId) {
        return kpiB1DetailResultRepository.deleteAllByInstanceModuleId(instanceModuleId);
    }

    @Override
    public List<KpiB1DetailResultDTO> findByResultId(long resultId) {
        final QKpiB1DetailResult qKpiB1DetailResult = QKpiB1DetailResult.kpiB1DetailResult;

        JPQLQuery<KpiB1DetailResultDTO> query = queryBuilder
            .createQuery()
            .from(qKpiB1DetailResult)
            .where(qKpiB1DetailResult.kpiB1Result.id.eq(resultId))
            .orderBy(qKpiB1DetailResult.evaluationType.asc(),
                     qKpiB1DetailResult.evaluationStartDate.asc())
            .select(
                Projections.fields(
                    KpiB1DetailResultDTO.class,
                    qKpiB1DetailResult.id.as("id"),
                    qKpiB1DetailResult.instance.id.as("instanceId"),
                    qKpiB1DetailResult.instanceModule.id.as("instanceModuleId"),
                    qKpiB1DetailResult.analysisDate.as("analysisDate"),
                    qKpiB1DetailResult.evaluationType.as("evaluationType"),
                    qKpiB1DetailResult.evaluationStartDate.as("evaluationStartDate"),
                    qKpiB1DetailResult.evaluationEndDate.as("evaluationEndDate"),
                    qKpiB1DetailResult.totalTransactions.as("totalTransactions"),
                    qKpiB1DetailResult.uniqueEntities.as("uniqueEntities"),
                    qKpiB1DetailResult.outcome.as("outcome"),
                    qKpiB1DetailResult.kpiB1Result.id.as("kpiB1ResultId")
                )
            );

        return query.fetch();
    }
}