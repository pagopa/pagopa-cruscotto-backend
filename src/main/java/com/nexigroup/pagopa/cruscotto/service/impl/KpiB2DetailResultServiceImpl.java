package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.AnagStation;
import com.nexigroup.pagopa.cruscotto.domain.Instance;
import com.nexigroup.pagopa.cruscotto.domain.InstanceModule;
import com.nexigroup.pagopa.cruscotto.domain.KpiB2DetailResult;
import com.nexigroup.pagopa.cruscotto.domain.KpiB2Result;
import com.nexigroup.pagopa.cruscotto.repository.AnagStationRepository;
import com.nexigroup.pagopa.cruscotto.repository.InstanceModuleRepository;
import com.nexigroup.pagopa.cruscotto.repository.InstanceRepository;
import com.nexigroup.pagopa.cruscotto.repository.KpiB2DetailResultRepository;
import com.nexigroup.pagopa.cruscotto.repository.KpiB2ResultRepository;
import com.nexigroup.pagopa.cruscotto.service.KpiB2DetailResultService;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB2DetailResultDTO;
import com.nexigroup.pagopa.cruscotto.service.qdsl.QueryBuilder;
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

        AnagStation station = anagStationRepository
            .findById(kpiB2DetailResultDTO.getStationId())
            .orElseThrow(() -> new IllegalArgumentException("Station not found"));

        KpiB2Result kpiB2DResult = kpiB2ResultRepository
            .findById(kpiB2DetailResultDTO.getKpiB2ResultId())
            .orElseThrow(() -> new IllegalArgumentException("KpiB2Result not found"));

        KpiB2DetailResult kpiB2DetailResult = getKpiB2DetailResult(kpiB2DetailResultDTO, instance, instanceModule, station, kpiB2DResult);

        kpiB2DetailResult = kpiB2DetailResultRepository.save(kpiB2DetailResult);

        kpiB2DetailResultDTO.setId(kpiB2DetailResult.getId());

        return kpiB2DetailResultDTO;
    }

    private static @NotNull KpiB2DetailResult getKpiB2DetailResult(
        KpiB2DetailResultDTO kpiB2DetailResultDTO,
        Instance instance,
        InstanceModule instanceModule,
        AnagStation station,
        KpiB2Result kpiB2Result
    ) {
        KpiB2DetailResult kpiB2DetailResult = new KpiB2DetailResult();
        kpiB2DetailResult.setInstance(instance);
        kpiB2DetailResult.setInstanceModule(instanceModule);
        kpiB2DetailResult.setAnalysisDate(kpiB2DetailResultDTO.getAnalysisDate());
        kpiB2DetailResult.setStation(station);
        kpiB2DetailResult.setMethod(kpiB2DetailResultDTO.getMethod());
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

    @Override
    public int deleteAllByInstanceModule(long instanceModuleId) {
        return kpiB2DetailResultRepository.deleteAllByInstanceModuleId(instanceModuleId);
    }
}
