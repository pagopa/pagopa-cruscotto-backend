package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.*;
import com.nexigroup.pagopa.cruscotto.repository.InstanceModuleRepository;
import com.nexigroup.pagopa.cruscotto.repository.InstanceRepository;
import com.nexigroup.pagopa.cruscotto.repository.KpiA2DetailResultRepository;
import com.nexigroup.pagopa.cruscotto.repository.KpiA2ResultRepository;
import com.nexigroup.pagopa.cruscotto.service.KpiA2DetailResultService;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiA2DetailResultDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB2DetailResultDTO;
import com.nexigroup.pagopa.cruscotto.service.qdsl.QueryBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPQLQuery;
import java.util.List;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link KpiA2DetailResult}.
 */

@Service
@Transactional
public class KpiA2DetailResultServiceImpl implements KpiA2DetailResultService {

    private static final Logger LOGGER = LoggerFactory.getLogger(KpiA2DetailResultServiceImpl.class);

    private final InstanceRepository instanceRepository;

    private final InstanceModuleRepository instanceModuleRepository;

    private final KpiA2DetailResultRepository kpiA2DetailResultRepository;

    private final KpiA2ResultRepository kpiA2ResultRepository;

    private final QueryBuilder queryBuilder;

    public KpiA2DetailResultServiceImpl(
        InstanceRepository instanceRepository,
        InstanceModuleRepository instanceModuleRepository,
        KpiA2DetailResultRepository kpiA2DetailResultRepository,
        KpiA2ResultRepository kpiA2ResultRepository,
        QueryBuilder queryBuilder
    ) {
        this.instanceRepository = instanceRepository;
        this.instanceModuleRepository = instanceModuleRepository;
        this.kpiA2DetailResultRepository = kpiA2DetailResultRepository;
        this.kpiA2ResultRepository = kpiA2ResultRepository;
        this.queryBuilder = queryBuilder;
    }

    /**
     * Save kpiA2DetailResult.
     *
     * @param kpiA2DetailResultDTO the entity to save.
     */
    @Override
    public KpiA2DetailResultDTO save(KpiA2DetailResultDTO kpiA2DetailResultDTO) {
        Instance instance = instanceRepository
            .findById(kpiA2DetailResultDTO.getInstanceId())
            .orElseThrow(() -> new IllegalArgumentException("Instance not found"));

        InstanceModule instanceModule = instanceModuleRepository
            .findById(kpiA2DetailResultDTO.getInstanceModuleId())
            .orElseThrow(() -> new IllegalArgumentException("InstanceModule not found"));

        KpiA2Result kpiA2DResult = kpiA2ResultRepository
            .findById(kpiA2DetailResultDTO.getKpiA2ResultId())
            .orElseThrow(() -> new IllegalArgumentException("KpiA2Result not found"));

        KpiA2DetailResult kpiA2DetailResult = getKpiA2DetailResult(kpiA2DetailResultDTO, instance, instanceModule, kpiA2DResult);

        kpiA2DetailResult = kpiA2DetailResultRepository.save(kpiA2DetailResult);

        kpiA2DetailResultDTO.setId(kpiA2DetailResult.getId());

        return kpiA2DetailResultDTO;
    }

    private static @NotNull KpiA2DetailResult getKpiA2DetailResult(
        KpiA2DetailResultDTO kpiA2DetailResultDTO,
        Instance instance,
        InstanceModule instanceModule,
        KpiA2Result kpiA2Result
    ) {
        KpiA2DetailResult kpiA2DetailResult = new KpiA2DetailResult();
        kpiA2DetailResult.setInstance(instance);
        kpiA2DetailResult.setInstanceModule(instanceModule);
        kpiA2DetailResult.setAnalysisDate(kpiA2DetailResultDTO.getAnalysisDate());
        kpiA2DetailResult.setEvaluationStartDate(kpiA2DetailResultDTO.getEvaluationStartDate());
        kpiA2DetailResult.setEvaluationEndDate(kpiA2DetailResultDTO.getEvaluationEndDate());
        kpiA2DetailResult.setTotPayments(kpiA2DetailResultDTO.getTotPayments());
        kpiA2DetailResult.setTotIncorrectPayments(kpiA2DetailResultDTO.getTotIncorrectPayments());
        kpiA2DetailResult.setErrorPercentage(kpiA2DetailResultDTO.getErrorPercentage());
        kpiA2DetailResult.setOutcome(kpiA2DetailResultDTO.getOutcome());
        kpiA2DetailResult.setKpiA2Result(kpiA2Result);

        return kpiA2DetailResult;
    }

    @Override
    public int deleteAllByInstanceModule(long instanceModuleId) {
        return kpiA2DetailResultRepository.deleteAllByInstanceModuleId(instanceModuleId);
    }

    @Override
    public List<KpiA2DetailResultDTO> findByResultId(long resultId) {
        final QKpiA2DetailResult qKpiA2DetailResult = QKpiA2DetailResult.kpiA2DetailResult;

        JPQLQuery<KpiA2DetailResultDTO> query = queryBuilder
            .createQuery()
            .from(qKpiA2DetailResult)
            .where(qKpiA2DetailResult.kpiA2Result.id.eq(resultId))
            .select(
                Projections.fields(
                    KpiA2DetailResultDTO.class,
                    qKpiA2DetailResult.id.as("id"),
                    qKpiA2DetailResult.instance.id.as("instanceId"),
                    qKpiA2DetailResult.instanceModule.id.as("instanceModuleId"),
                    qKpiA2DetailResult.analysisDate.as("analysisDate"),
                    qKpiA2DetailResult.evaluationStartDate.as("evaluationStartDate"),
                    qKpiA2DetailResult.evaluationEndDate.as("evaluationEndDate"),
                    qKpiA2DetailResult.totPayments.as("totPayments"),
                    qKpiA2DetailResult.totIncorrectPayments.as("totIncorrectPayments"),
                    qKpiA2DetailResult.errorPercentage.as("errorPercentage"),
                    qKpiA2DetailResult.outcome.as("outcome"),
                    qKpiA2DetailResult.kpiA2Result.id.as("kpiA2ResultId")
                )
            );

        return query.fetch();
    }
}
