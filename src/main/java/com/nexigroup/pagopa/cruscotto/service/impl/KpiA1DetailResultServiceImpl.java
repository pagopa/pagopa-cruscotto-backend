package com.nexigroup.pagopa.cruscotto.service.impl;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nexigroup.pagopa.cruscotto.domain.Instance;
import com.nexigroup.pagopa.cruscotto.domain.InstanceModule;
import com.nexigroup.pagopa.cruscotto.domain.KpiA1DetailResult;
import com.nexigroup.pagopa.cruscotto.domain.KpiA1Result;
import com.nexigroup.pagopa.cruscotto.domain.QAnagStation;
import com.nexigroup.pagopa.cruscotto.domain.QKpiA1DetailResult;
import com.nexigroup.pagopa.cruscotto.repository.AnagStationRepository;
import com.nexigroup.pagopa.cruscotto.repository.InstanceModuleRepository;
import com.nexigroup.pagopa.cruscotto.repository.InstanceRepository;
import com.nexigroup.pagopa.cruscotto.repository.KpiA1DetailResultRepository;
import com.nexigroup.pagopa.cruscotto.repository.KpiA1ResultRepository;
import com.nexigroup.pagopa.cruscotto.service.KpiA1DetailResultService;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiA1DetailResultDTO;
import com.nexigroup.pagopa.cruscotto.service.qdsl.QueryBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPQLQuery;

/**
 * Service Implementation for managing {@link KpiA1DetailResult}.
 */
@Service
@Transactional
public class KpiA1DetailResultServiceImpl implements KpiA1DetailResultService {

    private static final Logger LOGGER = LoggerFactory.getLogger(KpiA1DetailResultServiceImpl.class);

    private final AnagStationRepository anagStationRepository;

    private final InstanceRepository instanceRepository;

    private final InstanceModuleRepository instanceModuleRepository;

    private final KpiA1DetailResultRepository kpiA1DetailResultRepository;

    private final KpiA1ResultRepository kpiA1ResultRepository;

    private final QueryBuilder queryBuilder;

    public KpiA1DetailResultServiceImpl(
        AnagStationRepository anagStationRepository,
        InstanceRepository instanceRepository,
        InstanceModuleRepository instanceModuleRepository,
        KpiA1DetailResultRepository kpiA1DetailResultRepository,
        KpiA1ResultRepository kpiA1ResultRepository,
        QueryBuilder queryBuilder
    ) {
        this.anagStationRepository = anagStationRepository;
        this.instanceRepository = instanceRepository;
        this.instanceModuleRepository = instanceModuleRepository;
        this.kpiA1DetailResultRepository = kpiA1DetailResultRepository;
        this.kpiA1ResultRepository = kpiA1ResultRepository;
        this.queryBuilder = queryBuilder;
    }

    /**
     * Save kpiA1DetailResult.
     *
     * @param kpiA1DetailResultDTO the entity to save.
     */
    @Override
    public KpiA1DetailResultDTO save(KpiA1DetailResultDTO kpiA1DetailResultDTO) {
        Instance instance = instanceRepository
            .findById(kpiA1DetailResultDTO.getInstanceId())
            .orElseThrow(() -> new IllegalArgumentException("Instance not found"));

        InstanceModule instanceModule = instanceModuleRepository
            .findById(kpiA1DetailResultDTO.getInstanceModuleId())
            .orElseThrow(() -> new IllegalArgumentException("InstanceModule not found"));

        KpiA1Result kpiA1DResult = kpiA1ResultRepository
            .findById(kpiA1DetailResultDTO.getKpiA1ResultId())
            .orElseThrow(() -> new IllegalArgumentException("KpiA1Result not found"));

        KpiA1DetailResult kpiA1DetailResult = getKpiA1DetailResult(kpiA1DetailResultDTO, instance, instanceModule, kpiA1DResult);

        kpiA1DetailResult = kpiA1DetailResultRepository.save(kpiA1DetailResult);

        kpiA1DetailResultDTO.setId(kpiA1DetailResult.getId());

        return kpiA1DetailResultDTO;
    }

    private static @NotNull KpiA1DetailResult getKpiA1DetailResult(
        KpiA1DetailResultDTO kpiA1DetailResultDTO,
        Instance instance,
        InstanceModule instanceModule,
        KpiA1Result kpiA1Result
    ) {
        KpiA1DetailResult kpiA1DetailResult = new KpiA1DetailResult();
        kpiA1DetailResult.setInstance(instance);
        kpiA1DetailResult.setInstanceModule(instanceModule);
        kpiA1DetailResult.setAnalysisDate(kpiA1DetailResultDTO.getAnalysisDate());
        kpiA1DetailResult.setEvaluationType(kpiA1DetailResultDTO.getEvaluationType());
        kpiA1DetailResult.setEvaluationStartDate(kpiA1DetailResultDTO.getEvaluationStartDate());
        kpiA1DetailResult.setEvaluationEndDate(kpiA1DetailResultDTO.getEvaluationEndDate());
        kpiA1DetailResult.setTotReq(kpiA1DetailResultDTO.getTotReq());
        kpiA1DetailResult.setReqTimeout(kpiA1DetailResultDTO.getReqTimeout());
        kpiA1DetailResult.setTimeoutPercentage(kpiA1DetailResultDTO.getTimeoutPercentage());
        kpiA1DetailResult.setOutcome(kpiA1DetailResultDTO.getOutcome());
        kpiA1DetailResult.setKpiA1Result(kpiA1Result);

        return kpiA1DetailResult;
    }

    @Override
    public int deleteAllByInstanceModule(long instanceModuleId) {
        return kpiA1DetailResultRepository.deleteAllByInstanceModuleId(instanceModuleId);
    }

    @Override
    public List<KpiA1DetailResultDTO> findByResultId(long resultId) {
        final QKpiA1DetailResult qKpiA1DetailResult = QKpiA1DetailResult.kpiA1DetailResult;
        
        JPQLQuery<KpiA1DetailResultDTO> query = queryBuilder
            .createQuery()
            .from(qKpiA1DetailResult)
            .where(qKpiA1DetailResult.kpiA1Result.id.eq(resultId))
            .select(
                Projections.fields(
                    KpiA1DetailResultDTO.class,
                    qKpiA1DetailResult.id.as("id"),
                    qKpiA1DetailResult.instance.id.as("instanceId"),
                    qKpiA1DetailResult.instanceModule.id.as("instanceModuleId"),
                    qKpiA1DetailResult.analysisDate.as("analysisDate"),
                    qKpiA1DetailResult.evaluationType.as("evaluationType"),
                    qKpiA1DetailResult.evaluationStartDate.as("evaluationStartDate"),
                    qKpiA1DetailResult.evaluationEndDate.as("evaluationEndDate"),
                    qKpiA1DetailResult.totReq.as("totReq"),
                    qKpiA1DetailResult.reqTimeout.as("reqTimeout"),
                    qKpiA1DetailResult.timeoutPercentage.as("timeoutPercentage"),
                    qKpiA1DetailResult.outcome.as("outcome"),
                    qKpiA1DetailResult.kpiA1Result.id.as("kpiA1ResultId")
                )
            );

        return query.fetch();
    }
}
