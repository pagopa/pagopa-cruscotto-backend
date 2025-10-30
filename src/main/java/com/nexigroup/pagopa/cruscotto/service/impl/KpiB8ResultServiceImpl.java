package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.Instance;
import com.nexigroup.pagopa.cruscotto.domain.InstanceModule;
import com.nexigroup.pagopa.cruscotto.domain.KpiB8Result;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import com.nexigroup.pagopa.cruscotto.repository.InstanceModuleRepository;
import com.nexigroup.pagopa.cruscotto.repository.InstanceRepository;
import com.nexigroup.pagopa.cruscotto.repository.KpiB8ResultRepository;
import com.nexigroup.pagopa.cruscotto.service.KpiB8ResultService;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB8ResultDTO;

import java.util.List;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link KpiB8Result}.
 */
@Service
@Transactional
public class KpiB8ResultServiceImpl implements KpiB8ResultService {

    private static final Logger LOGGER = LoggerFactory.getLogger(KpiB8ResultServiceImpl.class);

    private final InstanceRepository instanceRepository;

    private final InstanceModuleRepository instanceModuleRepository;

    private final KpiB8ResultRepository kpiB8ResultRepository;

    public KpiB8ResultServiceImpl(
        InstanceRepository instanceRepository,
        InstanceModuleRepository instanceModuleRepository,
        KpiB8ResultRepository kpiB8ResultRepository
    ) {
        this.instanceRepository = instanceRepository;
        this.instanceModuleRepository = instanceModuleRepository;
        this.kpiB8ResultRepository = kpiB8ResultRepository;
    }

    /**
     * Save kpiB8Result.
     *
     * @param kpiB8ResultDTO the entity to save.
     */
    @Override
    public KpiB8ResultDTO save(KpiB8ResultDTO kpiB8ResultDTO) {
        Instance instance = instanceRepository
            .findById(kpiB8ResultDTO.getInstanceId())
            .orElseThrow(() -> new IllegalArgumentException("Instance not found"));

        InstanceModule instanceModule = instanceModuleRepository
            .findById(kpiB8ResultDTO.getInstanceModuleId())
            .orElseThrow(() -> new IllegalArgumentException("InstanceModule not found"));

        KpiB8Result kpiB8Result = getKpiB8Result(kpiB8ResultDTO, instance, instanceModule);

        kpiB8Result = kpiB8ResultRepository.save(kpiB8Result);

        kpiB8ResultDTO.setId(kpiB8Result.getId());

        return kpiB8ResultDTO;
    }

    @NotNull
    private static KpiB8Result getKpiB8Result(
        KpiB8ResultDTO kpiB8ResultDTO,
        Instance instance,
        InstanceModule instanceModule
    ) {
        KpiB8Result kpiB8Result = new KpiB8Result();
        kpiB8Result.setInstance(instance);
        kpiB8Result.setInstanceModule(instanceModule);
        kpiB8Result.setAnalysisDate(kpiB8ResultDTO.getAnalysisDate());
        //kpiB8Result.setExcludePlannedShutdown(kpiB8ResultDTO.getExcludePlannedShutdown());
        //kpiB8Result.setExcludeUnplannedShutdown(kpiB8ResultDTO.getExcludeUnplannedShutdown());
        kpiB8Result.setEligibilityThreshold(kpiB8ResultDTO.getEligibilityThreshold());
        kpiB8Result.setTolerance(kpiB8ResultDTO.getTolerance());
        kpiB8Result.setEvaluationType(kpiB8ResultDTO.getEvaluationType());
        kpiB8Result.setOutcome(kpiB8ResultDTO.getOutcome());

        return kpiB8Result;
    }

    @Override
    public int deleteAllByInstanceModule(long instanceModuleId) {
        return kpiB8ResultRepository.deleteAllByInstanceModuleId(instanceModuleId);
    }

    @Override
    public void updateKpiB8ResultOutcome(long id, OutcomeStatus outcomeStatus) {
        kpiB8ResultRepository
            .findById(id)
            .ifPresent(kpiB8Result -> {
                kpiB8Result.setOutcome(outcomeStatus);
                kpiB8ResultRepository.save(kpiB8Result);
            });
    }

    @Override
    @Transactional(readOnly = true)
    public List<KpiB8ResultDTO> findByInstanceModuleId(long instanceModuleId) {
        LOGGER.debug("Request to get KpiB8Results by instanceModuleId: {}", instanceModuleId);
        return kpiB8ResultRepository
            .findAllByInstanceModuleIdOrderByAnalysisDateDesc(instanceModuleId)
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    private KpiB8ResultDTO convertToDTO(KpiB8Result kpiB8Result) {
        KpiB8ResultDTO dto = new KpiB8ResultDTO();
        dto.setId(kpiB8Result.getId());
        dto.setInstanceId(kpiB8Result.getInstance().getId());
        dto.setInstanceModuleId(kpiB8Result.getInstanceModule().getId());
        dto.setAnalysisDate(kpiB8Result.getAnalysisDate());
        //dto.setExcludePlannedShutdown(kpiB8Result.getExcludePlannedShutdown());
        //dto.setExcludeUnplannedShutdown(kpiB8Result.getExcludeUnplannedShutdown());
        dto.setEligibilityThreshold(kpiB8Result.getEligibilityThreshold());
        dto.setTolerance(kpiB8Result.getTolerance());
        dto.setEvaluationType(kpiB8Result.getEvaluationType());
        dto.setOutcome(kpiB8Result.getOutcome());
        return dto;
    }
}
