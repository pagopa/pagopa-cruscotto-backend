package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.*;
import com.nexigroup.pagopa.cruscotto.repository.*;
import com.nexigroup.pagopa.cruscotto.service.KpiB2DetailResultService;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB2DetailResultDTO;
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
 * Service Implementation for managing {@link KpiB2DetailResult}.
 */
@Service
@Transactional
public class KpiB2DetailResultServiceImpl implements KpiB2DetailResultService {

    private static final Logger LOGGER = LoggerFactory.getLogger(KpiB2DetailResultServiceImpl.class);

    private final AnagStationRepository anagStationRepository;

    private final InstanceRepository instanceRepository;

    private final InstanceModuleRepository instanceModuleRepository;

    private final KpiB2DetailResultRepository kpiB2DetailResultRepository;

    private final KpiB2ResultRepository kpiB2ResultRepository;

    private final QueryBuilder queryBuilder;

    public KpiB2DetailResultServiceImpl(
        AnagStationRepository anagStationRepository,
        InstanceRepository instanceRepository,
        InstanceModuleRepository instanceModuleRepository,
        KpiB2DetailResultRepository kpiB2DetailResultRepository,
        KpiB2ResultRepository kpiB2ResultRepository,
        QueryBuilder queryBuilder
    ) {
        this.anagStationRepository = anagStationRepository;
        this.instanceRepository = instanceRepository;
        this.instanceModuleRepository = instanceModuleRepository;
        this.kpiB2DetailResultRepository = kpiB2DetailResultRepository;
        this.kpiB2ResultRepository = kpiB2ResultRepository;
        this.queryBuilder = queryBuilder;
    }

    /**
     * Save kpiB2DetailResult.
     *
     * @param kpiB2DetailResultDTO the entity to save.
     */
    @Override
    public KpiB2DetailResultDTO save(KpiB2DetailResultDTO kpiB2DetailResultDTO) {
        Instance instance = instanceRepository
            .findById(kpiB2DetailResultDTO.getInstanceId())
            .orElseThrow(() -> new IllegalArgumentException("Instance not found"));

        InstanceModule instanceModule = instanceModuleRepository
            .findById(kpiB2DetailResultDTO.getInstanceModuleId())
            .orElseThrow(() -> new IllegalArgumentException("InstanceModule not found"));

        KpiB2Result kpiB2DResult = kpiB2ResultRepository
            .findById(kpiB2DetailResultDTO.getKpiB2ResultId())
            .orElseThrow(() -> new IllegalArgumentException("KpiB2Result not found"));

        KpiB2DetailResult kpiB2DetailResult = getKpiB2DetailResult(kpiB2DetailResultDTO, instance, instanceModule, kpiB2DResult);

        kpiB2DetailResult = kpiB2DetailResultRepository.save(kpiB2DetailResult);

        kpiB2DetailResultDTO.setId(kpiB2DetailResult.getId());

        return kpiB2DetailResultDTO;
    }

    private static @NotNull KpiB2DetailResult getKpiB2DetailResult(
        KpiB2DetailResultDTO kpiB2DetailResultDTO,
        Instance instance,
        InstanceModule instanceModule,
        KpiB2Result kpiB2Result
    ) {
        KpiB2DetailResult kpiB2DetailResult = new KpiB2DetailResult();
        kpiB2DetailResult.setInstance(instance);
        kpiB2DetailResult.setInstanceModule(instanceModule);
        kpiB2DetailResult.setAnalysisDate(kpiB2DetailResultDTO.getAnalysisDate());
        kpiB2DetailResult.setEvaluationType(kpiB2DetailResultDTO.getEvaluationType());
        kpiB2DetailResult.setEvaluationStartDate(kpiB2DetailResultDTO.getEvaluationStartDate());
        kpiB2DetailResult.setEvaluationEndDate(kpiB2DetailResultDTO.getEvaluationEndDate());
        kpiB2DetailResult.setTotReq(kpiB2DetailResultDTO.getTotReq());
        kpiB2DetailResult.setAvgTime(kpiB2DetailResultDTO.getAvgTime());
        kpiB2DetailResult.setOverTimeLimit(kpiB2DetailResultDTO.getOverTimeLimit());
        kpiB2DetailResult.setOutcome(kpiB2DetailResultDTO.getOutcome());
        kpiB2DetailResult.setKpiB2Result(kpiB2Result);

        return kpiB2DetailResult;
    }

    private static @NotNull KpiB2DetailResultDTO getKpiB2DetailResultDTO(KpiB2DetailResult kpiB2DetailResult) {
        KpiB2DetailResultDTO kpiB2DetailResultDTO = new KpiB2DetailResultDTO();
        kpiB2DetailResultDTO.setId(kpiB2DetailResult.getId());
        kpiB2DetailResultDTO.setInstanceId(kpiB2DetailResult.getInstance() != null ? kpiB2DetailResult.getInstance().getId() : null);
        kpiB2DetailResultDTO.setInstanceModuleId(
            kpiB2DetailResult.getInstanceModule() != null ? kpiB2DetailResult.getInstanceModule().getId() : null
        );
        kpiB2DetailResultDTO.setAnalysisDate(kpiB2DetailResult.getAnalysisDate());
        kpiB2DetailResultDTO.setEvaluationType(kpiB2DetailResult.getEvaluationType());
        kpiB2DetailResultDTO.setEvaluationStartDate(kpiB2DetailResult.getEvaluationStartDate());
        kpiB2DetailResultDTO.setEvaluationEndDate(kpiB2DetailResult.getEvaluationEndDate());
        kpiB2DetailResultDTO.setTotReq(kpiB2DetailResult.getTotReq());
        kpiB2DetailResultDTO.setAvgTime(kpiB2DetailResult.getAvgTime());
        kpiB2DetailResultDTO.setOverTimeLimit(kpiB2DetailResult.getOverTimeLimit());
        kpiB2DetailResultDTO.setOutcome(kpiB2DetailResult.getOutcome());
        kpiB2DetailResultDTO.setKpiB2ResultId(
            kpiB2DetailResult.getKpiB2Result() != null ? kpiB2DetailResult.getKpiB2Result().getId() : null
        );
        return kpiB2DetailResultDTO;
    }

    @Override
    public int deleteAllByInstanceModule(long instanceModuleId) {
        return kpiB2DetailResultRepository.deleteAllByInstanceModuleId(instanceModuleId);
    }

    @Override
    public List<KpiB2DetailResultDTO> findByResultId(long resultId) {
        final QKpiB2DetailResult qKpiB2DetailResult = QKpiB2DetailResult.kpiB2DetailResult;

        JPQLQuery<KpiB2DetailResultDTO> query = queryBuilder
            .createQuery()
            .from(qKpiB2DetailResult)
            .where(qKpiB2DetailResult.kpiB2Result.id.eq(resultId))
            .orderBy(
                qKpiB2DetailResult.evaluationType.asc(),
                qKpiB2DetailResult.evaluationStartDate.asc())
            .select(
                Projections.fields(
                    KpiB2DetailResultDTO.class,
                    qKpiB2DetailResult.id.as("id"),
                    qKpiB2DetailResult.instance.id.as("instanceId"),
                    qKpiB2DetailResult.instanceModule.id.as("instanceModuleId"),
                    qKpiB2DetailResult.analysisDate.as("analysisDate"),
                    qKpiB2DetailResult.evaluationType.as("evaluationType"),
                    qKpiB2DetailResult.evaluationStartDate.as("evaluationStartDate"),
                    qKpiB2DetailResult.evaluationEndDate.as("evaluationEndDate"),
                    qKpiB2DetailResult.totReq.as("totReq"),
                    qKpiB2DetailResult.avgTime.as("avgTime"),
                    qKpiB2DetailResult.overTimeLimit.as("overTimeLimit"),
                    qKpiB2DetailResult.outcome.as("outcome"),
                    qKpiB2DetailResult.kpiB2Result.id.as("kpiB2ResultId")
                )
            );

        return query.fetch();
    }
}
