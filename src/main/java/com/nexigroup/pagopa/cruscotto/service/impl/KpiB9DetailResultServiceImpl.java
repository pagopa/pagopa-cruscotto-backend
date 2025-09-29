package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.*;
import com.nexigroup.pagopa.cruscotto.repository.*;
import com.nexigroup.pagopa.cruscotto.service.KpiB9DetailResultService;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB9DetailResultDTO;
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
 * Service Implementation for managing {@link KpiB9DetailResult}.
 */
@Service
@Transactional
public class KpiB9DetailResultServiceImpl implements KpiB9DetailResultService {

    private static final Logger LOGGER = LoggerFactory.getLogger(KpiB9DetailResultServiceImpl.class);

    private final InstanceRepository instanceRepository;

    private final InstanceModuleRepository instanceModuleRepository;

    private final KpiB9DetailResultRepository kpiB9DetailResultRepository;

    private final KpiB9ResultRepository kpiB9ResultRepository;

    private final QueryBuilder queryBuilder;

    public KpiB9DetailResultServiceImpl(
        InstanceRepository instanceRepository,
        InstanceModuleRepository instanceModuleRepository,
        KpiB9DetailResultRepository kpiB9DetailResultRepository,
        KpiB9ResultRepository kpiB9ResultRepository,
        QueryBuilder queryBuilder
    ) {
        this.instanceRepository = instanceRepository;
        this.instanceModuleRepository = instanceModuleRepository;
        this.kpiB9DetailResultRepository = kpiB9DetailResultRepository;
        this.kpiB9ResultRepository = kpiB9ResultRepository;
        this.queryBuilder = queryBuilder;
    }

    /**
     * Save kpiB9DetailResult.
     *
     * @param kpiB9DetailResultDTO the entity to save.
     */
    @Override
    public KpiB9DetailResultDTO save(KpiB9DetailResultDTO kpiB9DetailResultDTO) {
        Instance instance = instanceRepository
            .findById(kpiB9DetailResultDTO.getInstanceId())
            .orElseThrow(() -> new IllegalArgumentException("Instance not found"));

        InstanceModule instanceModule = instanceModuleRepository
            .findById(kpiB9DetailResultDTO.getInstanceModuleId())
            .orElseThrow(() -> new IllegalArgumentException("InstanceModule not found"));

        KpiB9Result kpiB9DResult = kpiB9ResultRepository
            .findById(kpiB9DetailResultDTO.getKpiB9ResultId())
            .orElseThrow(() -> new IllegalArgumentException("KpiB9Result not found"));

        KpiB9DetailResult kpiB9DetailResult = getKpiB9DetailResult(kpiB9DetailResultDTO, instance, instanceModule, kpiB9DResult);

        kpiB9DetailResult = kpiB9DetailResultRepository.save(kpiB9DetailResult);

        kpiB9DetailResultDTO.setId(kpiB9DetailResult.getId());

        return kpiB9DetailResultDTO;
    }

    private static @NotNull KpiB9DetailResult getKpiB9DetailResult(
        KpiB9DetailResultDTO kpiB9DetailResultDTO,
        Instance instance,
        InstanceModule instanceModule,
        KpiB9Result kpiB9Result
    ) {
        KpiB9DetailResult kpiB9DetailResult = new KpiB9DetailResult();
        kpiB9DetailResult.setInstance(instance);
        kpiB9DetailResult.setInstanceModule(instanceModule);
        kpiB9DetailResult.setAnalysisDate(kpiB9DetailResultDTO.getAnalysisDate());
        kpiB9DetailResult.setEvaluationType(kpiB9DetailResultDTO.getEvaluationType());
        kpiB9DetailResult.setEvaluationStartDate(kpiB9DetailResultDTO.getEvaluationStartDate());
        kpiB9DetailResult.setEvaluationEndDate(kpiB9DetailResultDTO.getEvaluationEndDate());
        kpiB9DetailResult.setTotRes(kpiB9DetailResultDTO.getTotRes());
        kpiB9DetailResult.setResKo(kpiB9DetailResultDTO.getResKo());
        kpiB9DetailResult.setResKoPercentage(kpiB9DetailResultDTO.getResKoPercentage());
        kpiB9DetailResult.setOutcome(kpiB9DetailResultDTO.getOutcome());
        kpiB9DetailResult.setKpiB9Result(kpiB9Result);

        return kpiB9DetailResult;
    }

    private static @NotNull KpiB9DetailResultDTO getKpiB9DetailResultDTO(KpiB9DetailResult kpiB9DetailResult) {
        KpiB9DetailResultDTO kpiB9DetailResultDTO = new KpiB9DetailResultDTO();
        kpiB9DetailResultDTO.setId(kpiB9DetailResult.getId());
        kpiB9DetailResultDTO.setInstanceId(kpiB9DetailResult.getInstance() != null ? kpiB9DetailResult.getInstance().getId() : null);
        kpiB9DetailResultDTO.setInstanceModuleId(
            kpiB9DetailResult.getInstanceModule() != null ? kpiB9DetailResult.getInstanceModule().getId() : null
        );
        kpiB9DetailResultDTO.setAnalysisDate(kpiB9DetailResult.getAnalysisDate());
        kpiB9DetailResultDTO.setEvaluationType(kpiB9DetailResult.getEvaluationType());
        kpiB9DetailResultDTO.setEvaluationStartDate(kpiB9DetailResult.getEvaluationStartDate());
        kpiB9DetailResultDTO.setEvaluationEndDate(kpiB9DetailResult.getEvaluationEndDate());
        kpiB9DetailResultDTO.setTotRes(kpiB9DetailResult.getTotRes());
        kpiB9DetailResultDTO.setResKo(kpiB9DetailResult.getResKo());
        kpiB9DetailResultDTO.setResKoPercentage(kpiB9DetailResult.getResKoPercentage());
        kpiB9DetailResultDTO.setOutcome(kpiB9DetailResult.getOutcome());
        kpiB9DetailResultDTO.setKpiB9ResultId(
            kpiB9DetailResult.getKpiB9Result() != null ? kpiB9DetailResult.getKpiB9Result().getId() : null
        );
        return kpiB9DetailResultDTO;
    }

    @Override
    public int deleteAllByInstanceModule(long instanceModuleId) {
        return kpiB9DetailResultRepository.deleteAllByInstanceModuleId(instanceModuleId);
    }

    @Override
    public List<KpiB9DetailResultDTO> findByResultId(long resultId) {
        final QKpiB9DetailResult qKpiB9DetailResult = QKpiB9DetailResult.kpiB9DetailResult;

        JPQLQuery<KpiB9DetailResultDTO> query = queryBuilder
            .createQuery()
            .from(qKpiB9DetailResult)
            .where(qKpiB9DetailResult.kpiB9Result.id.eq(resultId))
            .orderBy(
                qKpiB9DetailResult.evaluationType.asc(),
                qKpiB9DetailResult.evaluationStartDate.asc())
            .select(
                Projections.fields(
                    KpiB9DetailResultDTO.class,
                    qKpiB9DetailResult.id.as("id"),
                    qKpiB9DetailResult.instance.id.as("instanceId"),
                    qKpiB9DetailResult.instanceModule.id.as("instanceModuleId"),
                    qKpiB9DetailResult.analysisDate.as("analysisDate"),
                    qKpiB9DetailResult.evaluationType.as("evaluationType"),
                    qKpiB9DetailResult.evaluationStartDate.as("evaluationStartDate"),
                    qKpiB9DetailResult.evaluationEndDate.as("evaluationEndDate"),
                    qKpiB9DetailResult.totRes.as("totRes"),
                    qKpiB9DetailResult.resKo.as("resKo"),
                    qKpiB9DetailResult.resKoPercentage.as("resKoPercentage"),
                    qKpiB9DetailResult.outcome.as("outcome"),
                    qKpiB9DetailResult.kpiB9Result.id.as("kpiB9ResultId")
                )
            );

        return query.fetch();
    }
}
