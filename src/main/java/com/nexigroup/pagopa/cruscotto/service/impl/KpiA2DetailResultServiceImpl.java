package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.*;
import com.nexigroup.pagopa.cruscotto.repository.*;
import com.nexigroup.pagopa.cruscotto.service.KpiA2DetailResultService;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiA2DetailResultDTO;
import com.nexigroup.pagopa.cruscotto.service.qdsl.QueryBuilder;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing {@link KpiA2DetailResult}.
 */
@Service
@Transactional
public class KpiA2DetailResultServiceImpl implements KpiA2DetailResultService {

    private static final Logger LOGGER = LoggerFactory.getLogger(KpiA2DetailResultServiceImpl.class);

    private final AnagStationRepository anagStationRepository;

    private final InstanceRepository instanceRepository;

    private final InstanceModuleRepository instanceModuleRepository;

    private final KpiA2DetailResultRepository kpiA2DetailResultRepository;

    private final KpiA2ResultRepository kpiA2ResultRepository;

    private final QueryBuilder queryBuilder;


    public KpiA2DetailResultServiceImpl(
        AnagStationRepository anagStationRepository,
        InstanceRepository instanceRepository,
        InstanceModuleRepository instanceModuleRepository,
        KpiA2DetailResultRepository kpiA2DetailResultRepository,
        KpiA2ResultRepository kpiA2ResultRepository,
        QueryBuilder queryBuilder
    ) {
        this.anagStationRepository = anagStationRepository;
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
        kpiA2DetailResult.setTotIncorrectPayments(kpiA2DetailResult.getTotIncorrectPayments());
        kpiA2DetailResult.setErrorPercentage(kpiA2DetailResultDTO.getErrorPercentage());
        kpiA2DetailResult.setOutcome(kpiA2DetailResultDTO.getOutcome());
        kpiA2DetailResult.setKpiA2Result(kpiA2Result);

        return kpiA2DetailResult;
    }

    private static @NotNull KpiA2DetailResultDTO getKpiA2DetailResultDTO(KpiA2DetailResult kpiA2DetailResult) {
        KpiA2DetailResultDTO kpiA2DetailResultDTO = new KpiA2DetailResultDTO();
        kpiA2DetailResultDTO.setId(kpiA2DetailResult.getId());
        kpiA2DetailResultDTO.setInstanceId(kpiA2DetailResult.getInstance()!=null ? kpiA2DetailResult.getInstance().getId() : null);
        kpiA2DetailResultDTO.setInstanceModuleId(kpiA2DetailResult.getInstanceModule()!=null ? kpiA2DetailResult.getInstanceModule().getId() : null);
        kpiA2DetailResultDTO.setAnalysisDate(kpiA2DetailResult.getAnalysisDate());
        kpiA2DetailResultDTO.setEvaluationStartDate(kpiA2DetailResult.getEvaluationStartDate());
        kpiA2DetailResultDTO.setEvaluationEndDate(kpiA2DetailResult.getEvaluationEndDate());
        kpiA2DetailResultDTO.setTotPayments(kpiA2DetailResult.getTotPayments());
        kpiA2DetailResultDTO.setTotIncorrectPayments(kpiA2DetailResult.getTotIncorrectPayments());
        kpiA2DetailResultDTO.setErrorPercentage(kpiA2DetailResult.getErrorPercentage());
        kpiA2DetailResultDTO.setOutcome(kpiA2DetailResult.getOutcome());
        kpiA2DetailResultDTO.setKpiA2ResultId(kpiA2DetailResult.getKpiA2Result()!=null ? kpiA2DetailResult.getKpiA2Result().getId() : null);
        return kpiA2DetailResultDTO;
    }


    @Override
    public int deleteAllByInstanceModule(long instanceModuleId) {
        return kpiA2DetailResultRepository.deleteAllByInstanceModuleId(instanceModuleId);
    }

    @Override
    public List<KpiA2DetailResultDTO> findByInstanceModuleId(long instanceModuleId) {
        return kpiA2DetailResultRepository.selectByInstanceModuleId(instanceModuleId)
            .stream()
            .map(KpiA2DetailResultServiceImpl::getKpiA2DetailResultDTO)
            .collect(Collectors.toList());
    }

}
