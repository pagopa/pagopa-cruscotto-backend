package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.Instance;
import com.nexigroup.pagopa.cruscotto.domain.InstanceModule;
import com.nexigroup.pagopa.cruscotto.domain.KpiB3DetailResult;
import com.nexigroup.pagopa.cruscotto.domain.KpiB3Result;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;

import com.nexigroup.pagopa.cruscotto.repository.InstanceModuleRepository;
import com.nexigroup.pagopa.cruscotto.repository.InstanceRepository;
import com.nexigroup.pagopa.cruscotto.repository.KpiB3DetailResultRepository;
import com.nexigroup.pagopa.cruscotto.repository.KpiB3ResultRepository;
import com.nexigroup.pagopa.cruscotto.service.KpiB3DetailResultService;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB3DetailResultDTO;


import java.util.List;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link KpiB3DetailResult}.
 */
@Service
@Transactional
public class KpiB3DetailResultServiceImpl implements KpiB3DetailResultService {

    private static final Logger LOGGER = LoggerFactory.getLogger(KpiB3DetailResultServiceImpl.class);

    private final InstanceRepository instanceRepository;

    private final InstanceModuleRepository instanceModuleRepository;

    private final KpiB3DetailResultRepository kpiB3DetailResultRepository;

    private final KpiB3ResultRepository kpiB3ResultRepository;

    public KpiB3DetailResultServiceImpl(
        InstanceRepository instanceRepository,
        InstanceModuleRepository instanceModuleRepository,
        KpiB3DetailResultRepository kpiB3DetailResultRepository,
        KpiB3ResultRepository kpiB3ResultRepository
    ) {
        this.instanceRepository = instanceRepository;
        this.instanceModuleRepository = instanceModuleRepository;
        this.kpiB3DetailResultRepository = kpiB3DetailResultRepository;
        this.kpiB3ResultRepository = kpiB3ResultRepository;
    }

    /**
     * Save kpiB3DetailResult.
     *
     * @param kpiB3DetailResultDTO the entity to save.
     */
    @Override
    public KpiB3DetailResultDTO save(KpiB3DetailResultDTO kpiB3DetailResultDTO) {
        Instance instance = instanceRepository
            .findById(kpiB3DetailResultDTO.getInstanceId())
            .orElseThrow(() -> new IllegalArgumentException("Instance not found"));

        InstanceModule instanceModule = instanceModuleRepository
            .findById(kpiB3DetailResultDTO.getInstanceModuleId())
            .orElseThrow(() -> new IllegalArgumentException("InstanceModule not found"));

        KpiB3Result kpiB3Result = kpiB3ResultRepository
            .findById(kpiB3DetailResultDTO.getKpiB3ResultId())
            .orElseThrow(() -> new IllegalArgumentException("KpiB3Result not found"));

        KpiB3DetailResult kpiB3DetailResult = getKpiB3DetailResult(kpiB3DetailResultDTO, instance, instanceModule, kpiB3Result);

        kpiB3DetailResult = kpiB3DetailResultRepository.save(kpiB3DetailResult);

        kpiB3DetailResultDTO.setId(kpiB3DetailResult.getId());

        return kpiB3DetailResultDTO;
    }

    @NotNull
    private KpiB3DetailResult getKpiB3DetailResult(
        KpiB3DetailResultDTO kpiB3DetailResultDTO,
        Instance instance,
        InstanceModule instanceModule,
        KpiB3Result kpiB3Result
    ) {
        KpiB3DetailResult kpiB3DetailResult = new KpiB3DetailResult();
        kpiB3DetailResult.setInstance(instance);
        kpiB3DetailResult.setInstanceModule(instanceModule);
        // No anagStation field - this entity stores partner-level aggregated data
        kpiB3DetailResult.setKpiB3Result(kpiB3Result);
        kpiB3DetailResult.setAnalysisDate(kpiB3DetailResultDTO.getAnalysisDate());
        kpiB3DetailResult.setEvaluationType(kpiB3DetailResultDTO.getEvaluationType());
        kpiB3DetailResult.setEvaluationStartDate(kpiB3DetailResultDTO.getEvaluationStartDate());
        kpiB3DetailResult.setEvaluationEndDate(kpiB3DetailResultDTO.getEvaluationEndDate());
        kpiB3DetailResult.setTotalStandIn(kpiB3DetailResultDTO.getTotalStandIn());
        kpiB3DetailResult.setOutcome(kpiB3DetailResultDTO.getOutcome());

        return kpiB3DetailResult;
    }

    @Override
    public int deleteAllByInstanceModule(long instanceModuleId) {
        return kpiB3DetailResultRepository.deleteAllByInstanceModuleId(instanceModuleId);
    }

    @Override
    public void updateKpiB3DetailResultOutcome(long id, OutcomeStatus outcomeStatus) {
        kpiB3DetailResultRepository
            .findById(id)
            .ifPresent(kpiB3DetailResult -> {
                kpiB3DetailResult.setOutcome(outcomeStatus);
                kpiB3DetailResultRepository.save(kpiB3DetailResult);
            });
    }

    @Override
    @Transactional(readOnly = true)
    public List<KpiB3DetailResultDTO> findByResultId(long resultId) {
        LOGGER.debug("Request to get KpiB3DetailResults by resultId: {}", resultId);
        return kpiB3DetailResultRepository
            .findAllByResultIdOrderByAnalysisDateDesc(resultId)
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    private KpiB3DetailResultDTO convertToDTO(KpiB3DetailResult kpiB3DetailResult) {
        KpiB3DetailResultDTO dto = new KpiB3DetailResultDTO();
        dto.setId(kpiB3DetailResult.getId());
        dto.setInstanceId(kpiB3DetailResult.getInstance().getId());
        dto.setInstanceModuleId(kpiB3DetailResult.getInstanceModule().getId());
        // No anagStationId - this entity represents partner-level aggregated data
        dto.setAnagStationId(null);
        dto.setKpiB3ResultId(kpiB3DetailResult.getKpiB3Result().getId());
        dto.setAnalysisDate(kpiB3DetailResult.getAnalysisDate());
        dto.setEvaluationType(kpiB3DetailResult.getEvaluationType());
        dto.setEvaluationStartDate(kpiB3DetailResult.getEvaluationStartDate());
        dto.setEvaluationEndDate(kpiB3DetailResult.getEvaluationEndDate());
        dto.setTotalStandIn(kpiB3DetailResult.getTotalStandIn());
        dto.setOutcome(kpiB3DetailResult.getOutcome());
        return dto;
    }


}