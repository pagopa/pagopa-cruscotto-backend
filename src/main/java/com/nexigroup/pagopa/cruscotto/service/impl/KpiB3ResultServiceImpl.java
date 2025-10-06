package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.Instance;
import com.nexigroup.pagopa.cruscotto.domain.InstanceModule;
import com.nexigroup.pagopa.cruscotto.domain.KpiB3Result;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import com.nexigroup.pagopa.cruscotto.repository.InstanceModuleRepository;
import com.nexigroup.pagopa.cruscotto.repository.InstanceRepository;
import com.nexigroup.pagopa.cruscotto.repository.KpiB3ResultRepository;
import com.nexigroup.pagopa.cruscotto.service.KpiB3ResultService;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB3ResultDTO;


import java.util.List;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link KpiB3Result}.
 */
@Service
@Transactional
public class KpiB3ResultServiceImpl implements KpiB3ResultService {

    private static final Logger LOGGER = LoggerFactory.getLogger(KpiB3ResultServiceImpl.class);

    private final InstanceRepository instanceRepository;

    private final InstanceModuleRepository instanceModuleRepository;

    private final KpiB3ResultRepository kpiB3ResultRepository;

    public KpiB3ResultServiceImpl(
        InstanceRepository instanceRepository,
        InstanceModuleRepository instanceModuleRepository,
        KpiB3ResultRepository kpiB3ResultRepository
    ) {
        this.instanceRepository = instanceRepository;
        this.instanceModuleRepository = instanceModuleRepository;
        this.kpiB3ResultRepository = kpiB3ResultRepository;
    }

    /**
     * Save kpiB3Result.
     *
     * @param kpiB3ResultDTO the entity to save.
     */
    @Override
    public KpiB3ResultDTO save(KpiB3ResultDTO kpiB3ResultDTO) {
        Instance instance = instanceRepository
            .findById(kpiB3ResultDTO.getInstanceId())
            .orElseThrow(() -> new IllegalArgumentException("Instance not found"));

        InstanceModule instanceModule = instanceModuleRepository
            .findById(kpiB3ResultDTO.getInstanceModuleId())
            .orElseThrow(() -> new IllegalArgumentException("InstanceModule not found"));

        KpiB3Result kpiB3Result = getKpiB3Result(kpiB3ResultDTO, instance, instanceModule);

        kpiB3Result = kpiB3ResultRepository.save(kpiB3Result);

        kpiB3ResultDTO.setId(kpiB3Result.getId());

        return kpiB3ResultDTO;
    }

    @NotNull
    private static KpiB3Result getKpiB3Result(
        KpiB3ResultDTO kpiB3ResultDTO,
        Instance instance,
        InstanceModule instanceModule
    ) {
        KpiB3Result kpiB3Result = new KpiB3Result();
        kpiB3Result.setInstance(instance);
        kpiB3Result.setInstanceModule(instanceModule);
        kpiB3Result.setAnalysisDate(kpiB3ResultDTO.getAnalysisDate());
        kpiB3Result.setExcludePlannedShutdown(kpiB3ResultDTO.getExcludePlannedShutdown());
        kpiB3Result.setExcludeUnplannedShutdown(kpiB3ResultDTO.getExcludeUnplannedShutdown());
        kpiB3Result.setEligibilityThreshold(kpiB3ResultDTO.getEligibilityThreshold());
        kpiB3Result.setTolerance(kpiB3ResultDTO.getTolerance());
        kpiB3Result.setEvaluationType(kpiB3ResultDTO.getEvaluationType());
        kpiB3Result.setOutcome(kpiB3ResultDTO.getOutcome());

        return kpiB3Result;
    }

    @Override
    public int deleteAllByInstanceModule(long instanceModuleId) {
        return kpiB3ResultRepository.deleteAllByInstanceModuleId(instanceModuleId);
    }

    @Override
    public void updateKpiB3ResultOutcome(long id, OutcomeStatus outcomeStatus) {
        kpiB3ResultRepository
            .findById(id)
            .ifPresent(kpiB3Result -> {
                kpiB3Result.setOutcome(outcomeStatus);
                kpiB3ResultRepository.save(kpiB3Result);
            });
    }

    @Override
    @Transactional(readOnly = true)
    public List<KpiB3ResultDTO> findByInstanceModuleId(long instanceModuleId) {
        LOGGER.debug("Request to get KpiB3Results by instanceModuleId: {}", instanceModuleId);
        return kpiB3ResultRepository
            .findAllByInstanceModuleIdOrderByAnalysisDateDesc(instanceModuleId)
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    private KpiB3ResultDTO convertToDTO(KpiB3Result kpiB3Result) {
        KpiB3ResultDTO dto = new KpiB3ResultDTO();
        dto.setId(kpiB3Result.getId());
        dto.setInstanceId(kpiB3Result.getInstance().getId());
        dto.setInstanceModuleId(kpiB3Result.getInstanceModule().getId());
        dto.setAnalysisDate(kpiB3Result.getAnalysisDate());
        dto.setExcludePlannedShutdown(kpiB3Result.getExcludePlannedShutdown());
        dto.setExcludeUnplannedShutdown(kpiB3Result.getExcludeUnplannedShutdown());
        dto.setEligibilityThreshold(kpiB3Result.getEligibilityThreshold());
        dto.setTolerance(kpiB3Result.getTolerance());
        dto.setEvaluationType(kpiB3Result.getEvaluationType());
        dto.setOutcome(kpiB3Result.getOutcome());
        return dto;
    }
}