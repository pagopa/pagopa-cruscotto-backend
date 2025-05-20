package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.*;
import com.nexigroup.pagopa.cruscotto.repository.*;
import com.nexigroup.pagopa.cruscotto.service.KpiA1DetailResultService;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiA1DetailResultDTO;
import com.nexigroup.pagopa.cruscotto.service.qdsl.QueryBuilder;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

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

        AnagStation station = anagStationRepository
            .findById(kpiA1DetailResultDTO.getStationId())
            .orElseThrow(() -> new IllegalArgumentException("Station not found"));

        KpiA1Result kpiA1DResult = kpiA1ResultRepository
            .findById(kpiA1DetailResultDTO.getKpiA1ResultId())
            .orElseThrow(() -> new IllegalArgumentException("KpiA1Result not found"));

        KpiA1DetailResult kpiA1DetailResult = getKpiA1DetailResult(kpiA1DetailResultDTO, instance, instanceModule, station, kpiA1DResult);

        kpiA1DetailResult = kpiA1DetailResultRepository.save(kpiA1DetailResult);

        kpiA1DetailResultDTO.setId(kpiA1DetailResult.getId());

        return kpiA1DetailResultDTO;
    }

    private static @NotNull KpiA1DetailResult getKpiA1DetailResult(
        KpiA1DetailResultDTO kpiA1DetailResultDTO,
        Instance instance,
        InstanceModule instanceModule,
        AnagStation station,
        KpiA1Result kpiA1Result
    ) {
        KpiA1DetailResult kpiA1DetailResult = new KpiA1DetailResult();
        kpiA1DetailResult.setInstance(instance);
        kpiA1DetailResult.setInstanceModule(instanceModule);
        kpiA1DetailResult.setAnalysisDate(kpiA1DetailResultDTO.getAnalysisDate());
        kpiA1DetailResult.setStation(station);
        kpiA1DetailResult.setMethod(kpiA1DetailResultDTO.getMethod());
        kpiA1DetailResult.setEvaluationType(kpiA1DetailResultDTO.getEvaluationType());
        kpiA1DetailResult.setEvaluationStartDate(kpiA1DetailResultDTO.getEvaluationStartDate());
        kpiA1DetailResult.setEvaluationEndDate(kpiA1DetailResultDTO.getEvaluationEndDate());
        kpiA1DetailResult.setTotReq(kpiA1DetailResultDTO.getTotReq());

        kpiA1DetailResult.setOutcome(kpiA1DetailResultDTO.getOutcome());
        kpiA1DetailResult.setKpiA1Result(kpiA1Result);

        return kpiA1DetailResult;
    }

    private static @NotNull KpiA1DetailResultDTO getKpiA1DetailResultDTO(KpiA1DetailResult kpiA1DetailResult) {
        KpiA1DetailResultDTO kpiA1DetailResultDTO = new KpiA1DetailResultDTO();
        kpiA1DetailResultDTO.setId(kpiA1DetailResult.getId());
        kpiA1DetailResultDTO.setInstanceId(kpiA1DetailResult.getInstance()!=null ? kpiA1DetailResult.getInstance().getId() : null);
        kpiA1DetailResultDTO.setInstanceModuleId(kpiA1DetailResult.getInstanceModule()!=null ? kpiA1DetailResult.getInstanceModule().getId() : null);
        kpiA1DetailResultDTO.setAnalysisDate(kpiA1DetailResult.getAnalysisDate());
        kpiA1DetailResultDTO.setStationId(kpiA1DetailResult.getStation()!=null ? kpiA1DetailResult.getStation().getId() : null);
        kpiA1DetailResultDTO.setMethod(kpiA1DetailResult.getMethod());
        kpiA1DetailResultDTO.setEvaluationType(kpiA1DetailResult.getEvaluationType());
        kpiA1DetailResultDTO.setEvaluationStartDate(kpiA1DetailResult.getEvaluationStartDate());
        kpiA1DetailResultDTO.setEvaluationEndDate(kpiA1DetailResult.getEvaluationEndDate());
        kpiA1DetailResultDTO.setTotReq(kpiA1DetailResult.getTotReq());
        kpiA1DetailResultDTO.setReqTimeout(kpiA1DetailResult.getReqTimeout());
        kpiA1DetailResultDTO.setTimeoutPercentage(kpiA1DetailResult.getTimeoutPercentage());
        kpiA1DetailResultDTO.setOutcome(kpiA1DetailResult.getOutcome());
        kpiA1DetailResultDTO.setKpiA1ResultId(kpiA1DetailResult.getKpiA1Result()!=null ? kpiA1DetailResult.getKpiA1Result().getId() : null);
        return kpiA1DetailResultDTO;
    }


    @Override
    public int deleteAllByInstanceModule(long instanceModuleId) {
        return kpiA1DetailResultRepository.deleteAllByInstanceModuleId(instanceModuleId);
    }

    @Override
    public List<KpiA1DetailResultDTO> findByInstanceModuleId(long instanceModuleId) {
        return kpiA1DetailResultRepository.selectByInstanceModuleId(instanceModuleId)
            .stream()
            .map(KpiA1DetailResultServiceImpl::getKpiA1DetailResultDTO)
            .collect(Collectors.toList());
    }

}
