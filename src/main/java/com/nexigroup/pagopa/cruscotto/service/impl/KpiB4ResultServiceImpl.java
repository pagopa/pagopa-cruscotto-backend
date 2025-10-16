package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.Instance;
import com.nexigroup.pagopa.cruscotto.domain.InstanceModule;
import com.nexigroup.pagopa.cruscotto.domain.KpiB4Result;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import com.nexigroup.pagopa.cruscotto.repository.InstanceModuleRepository;
import com.nexigroup.pagopa.cruscotto.repository.InstanceRepository;
import com.nexigroup.pagopa.cruscotto.repository.KpiB4ResultRepository;
import com.nexigroup.pagopa.cruscotto.service.KpiB4ResultService;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB4ResultDTO;

import java.util.List;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link KpiB4Result}.
 */
@Service
@Transactional
public class KpiB4ResultServiceImpl implements KpiB4ResultService {

    private static final Logger LOGGER = LoggerFactory.getLogger(KpiB4ResultServiceImpl.class);

    private final InstanceRepository instanceRepository;

    private final InstanceModuleRepository instanceModuleRepository;

    private final KpiB4ResultRepository kpiB4ResultRepository;

    public KpiB4ResultServiceImpl(
        InstanceRepository instanceRepository,
        InstanceModuleRepository instanceModuleRepository,
        KpiB4ResultRepository kpiB4ResultRepository
    ) {
        this.instanceRepository = instanceRepository;
        this.instanceModuleRepository = instanceModuleRepository;
        this.kpiB4ResultRepository = kpiB4ResultRepository;
    }

    /**
     * Save kpiB4Result.
     *
     * @param kpiB4ResultDTO the entity to save.
     */
    @Override
    public KpiB4ResultDTO save(KpiB4ResultDTO kpiB4ResultDTO) {
        Instance instance = instanceRepository
            .findById(kpiB4ResultDTO.getInstanceId())
            .orElseThrow(() -> new IllegalArgumentException("Instance not found"));

        InstanceModule instanceModule = instanceModuleRepository
            .findById(kpiB4ResultDTO.getInstanceModuleId())
            .orElseThrow(() -> new IllegalArgumentException("InstanceModule not found"));

        KpiB4Result kpiB4Result = getKpiB4Result(kpiB4ResultDTO, instance, instanceModule);

        kpiB4Result = kpiB4ResultRepository.save(kpiB4Result);

        kpiB4ResultDTO.setId(kpiB4Result.getId());

        return kpiB4ResultDTO;
    }

    @NotNull
    private static KpiB4Result getKpiB4Result(
        KpiB4ResultDTO kpiB4ResultDTO,
        Instance instance,
        InstanceModule instanceModule
    ) {
        KpiB4Result kpiB4Result = new KpiB4Result();
        kpiB4Result.setInstance(instance);
        kpiB4Result.setInstanceModule(instanceModule);
        kpiB4Result.setAnalysisDate(kpiB4ResultDTO.getAnalysisDate());
        kpiB4Result.setExcludePlannedShutdown(kpiB4ResultDTO.getExcludePlannedShutdown());
        kpiB4Result.setExcludeUnplannedShutdown(kpiB4ResultDTO.getExcludeUnplannedShutdown());
        kpiB4Result.setEligibilityThreshold(kpiB4ResultDTO.getEligibilityThreshold());
        kpiB4Result.setTolerance(kpiB4ResultDTO.getTolerance());
        kpiB4Result.setEvaluationType(kpiB4ResultDTO.getEvaluationType());
        kpiB4Result.setOutcome(kpiB4ResultDTO.getOutcome());

        return kpiB4Result;
    }

    @Override
    public int deleteAllByInstanceModule(long instanceModuleId) {
        return kpiB4ResultRepository.deleteAllByInstanceModuleId(instanceModuleId);
    }

    @Override
    public void updateKpiB4ResultOutcome(long id, OutcomeStatus outcomeStatus) {
        kpiB4ResultRepository
            .findById(id)
            .ifPresent(kpiB4Result -> {
                kpiB4Result.setOutcome(outcomeStatus);
                kpiB4ResultRepository.save(kpiB4Result);
            });
    }

    @Override
    @Transactional(readOnly = true)
    public List<KpiB4ResultDTO> findByInstanceModuleId(long instanceModuleId) {
        LOGGER.debug("Request to get KpiB4Results by instanceModuleId: {}", instanceModuleId);
        return kpiB4ResultRepository
            .findAllByInstanceModuleIdOrderByAnalysisDateDesc(instanceModuleId)
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    private KpiB4ResultDTO convertToDTO(KpiB4Result kpiB4Result) {
        KpiB4ResultDTO dto = new KpiB4ResultDTO();
        dto.setId(kpiB4Result.getId());
        dto.setInstanceId(kpiB4Result.getInstance().getId());
        dto.setInstanceModuleId(kpiB4Result.getInstanceModule().getId());
        dto.setAnalysisDate(kpiB4Result.getAnalysisDate());
        dto.setExcludePlannedShutdown(kpiB4Result.getExcludePlannedShutdown());
        dto.setExcludeUnplannedShutdown(kpiB4Result.getExcludeUnplannedShutdown());
        dto.setEligibilityThreshold(kpiB4Result.getEligibilityThreshold());
        dto.setTolerance(kpiB4Result.getTolerance());
        dto.setEvaluationType(kpiB4Result.getEvaluationType());
        dto.setOutcome(kpiB4Result.getOutcome());
        return dto;
    }
}