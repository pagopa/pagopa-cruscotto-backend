package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.Instance;
import com.nexigroup.pagopa.cruscotto.domain.InstanceModule;
import com.nexigroup.pagopa.cruscotto.domain.KpiB5Result;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import com.nexigroup.pagopa.cruscotto.repository.InstanceModuleRepository;
import com.nexigroup.pagopa.cruscotto.repository.InstanceRepository;
import com.nexigroup.pagopa.cruscotto.repository.KpiB5ResultRepository;
import com.nexigroup.pagopa.cruscotto.service.KpiB5ResultService;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB5ResultDTO;

import java.util.List;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link KpiB5Result}.
 */
@Service
@Transactional
public class KpiB5ResultServiceImpl implements KpiB5ResultService {

    private static final Logger LOGGER = LoggerFactory.getLogger(KpiB5ResultServiceImpl.class);

    private final InstanceRepository instanceRepository;

    private final InstanceModuleRepository instanceModuleRepository;

    private final KpiB5ResultRepository kpiB5ResultRepository;

    public KpiB5ResultServiceImpl(
        InstanceRepository instanceRepository,
        InstanceModuleRepository instanceModuleRepository,
        KpiB5ResultRepository kpiB5ResultRepository
    ) {
        this.instanceRepository = instanceRepository;
        this.instanceModuleRepository = instanceModuleRepository;
        this.kpiB5ResultRepository = kpiB5ResultRepository;
    }

    /**
     * Save kpiB5Result.
     *
     * @param kpiB5ResultDTO the entity to save.
     */
    @Override
    public KpiB5ResultDTO save(KpiB5ResultDTO kpiB5ResultDTO) {
        Instance instance = instanceRepository
            .findById(kpiB5ResultDTO.getInstanceId())
            .orElseThrow(() -> new IllegalArgumentException("Instance not found"));

        InstanceModule instanceModule = instanceModuleRepository
            .findById(kpiB5ResultDTO.getInstanceModuleId())
            .orElseThrow(() -> new IllegalArgumentException("InstanceModule not found"));

        KpiB5Result kpiB5Result = getKpiB5Result(kpiB5ResultDTO, instance, instanceModule);

        kpiB5Result = kpiB5ResultRepository.save(kpiB5Result);

        kpiB5ResultDTO.setId(kpiB5Result.getId());

        return kpiB5ResultDTO;
    }

    @NotNull
    private static KpiB5Result getKpiB5Result(
        KpiB5ResultDTO kpiB5ResultDTO,
        Instance instance,
        InstanceModule instanceModule
    ) {
        KpiB5Result kpiB5Result = new KpiB5Result();
        kpiB5Result.setInstance(instance);
        kpiB5Result.setInstanceModule(instanceModule);
        kpiB5Result.setAnalysisDate(kpiB5ResultDTO.getAnalysisDate());
        kpiB5Result.setThresholdIndex(kpiB5ResultDTO.getEligibilityThreshold());
        kpiB5Result.setToleranceIndex(kpiB5ResultDTO.getTolerance());
        kpiB5Result.setOutcome(kpiB5ResultDTO.getOutcome());

        return kpiB5Result;
    }

    @Override
    public int deleteAllByInstanceModule(long instanceModuleId) {
        return kpiB5ResultRepository.deleteAllByInstanceModuleId(instanceModuleId);
    }

    @Override
    public void updateKpiB5ResultOutcome(long id, OutcomeStatus outcomeStatus) {
        kpiB5ResultRepository
            .findById(id)
            .ifPresent(kpiB5Result -> {
                kpiB5Result.setOutcome(outcomeStatus);
                kpiB5ResultRepository.save(kpiB5Result);
            });
    }

    @Override
    @Transactional(readOnly = true)
    public List<KpiB5ResultDTO> findByInstanceModuleId(long instanceModuleId) {
        LOGGER.debug("Request to get KpiB5Results by instanceModuleId: {}", instanceModuleId);
        return kpiB5ResultRepository
            .findAllByInstanceModuleIdOrderByAnalysisDateDesc(instanceModuleId)
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    private KpiB5ResultDTO convertToDTO(KpiB5Result kpiB5Result) {
        KpiB5ResultDTO dto = new KpiB5ResultDTO();
        dto.setId(kpiB5Result.getId());
        dto.setInstanceId(kpiB5Result.getInstance().getId());
        dto.setInstanceModuleId(kpiB5Result.getInstanceModule().getId());
        dto.setAnalysisDate(kpiB5Result.getAnalysisDate());
        dto.setEligibilityThreshold(kpiB5Result.getThresholdIndex());
        dto.setTolerance(kpiB5Result.getToleranceIndex());
        dto.setOutcome(kpiB5Result.getOutcome());
        return dto;
    }
}