package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.Instance;
import com.nexigroup.pagopa.cruscotto.domain.InstanceModule;
import com.nexigroup.pagopa.cruscotto.domain.KpiC2Result;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import com.nexigroup.pagopa.cruscotto.repository.InstanceModuleRepository;
import com.nexigroup.pagopa.cruscotto.repository.InstanceRepository;
import com.nexigroup.pagopa.cruscotto.repository.KpiC2ResultRepository;
import com.nexigroup.pagopa.cruscotto.service.KpiC2ResultService;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiC2ResultDTO;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing {@link KpiC2Result}.
 */
@Service
@Transactional
public class KpiC2ResultServiceImpl implements KpiC2ResultService {

    private static final Logger LOGGER = LoggerFactory.getLogger(KpiC2ResultServiceImpl.class);

    private final InstanceRepository instanceRepository;

    private final InstanceModuleRepository instanceModuleRepository;

    private final KpiC2ResultRepository kpiC2ResultRepository;

    public KpiC2ResultServiceImpl(
        InstanceRepository instanceRepository,
        InstanceModuleRepository instanceModuleRepository,
        KpiC2ResultRepository kpiC2ResultRepository
    ) {
        this.instanceRepository = instanceRepository;
        this.instanceModuleRepository = instanceModuleRepository;
        this.kpiC2ResultRepository = kpiC2ResultRepository;
    }

    /**
     * Save kpiC2Result.
     *
     * @param kpiC2ResultDTO the entity to save.
     */
    @Override
    public KpiC2ResultDTO save(KpiC2ResultDTO kpiC2ResultDTO) {
        Instance instance = instanceRepository
            .findById(kpiC2ResultDTO.getInstanceId())
            .orElseThrow(() -> new IllegalArgumentException("Instance not found"));

        InstanceModule instanceModule = instanceModuleRepository
            .findById(kpiC2ResultDTO.getInstanceModuleId())
            .orElseThrow(() -> new IllegalArgumentException("InstanceModule not found"));

        KpiC2Result kpiC2Result = getKpiC2Result(kpiC2ResultDTO, instance, instanceModule);

        kpiC2Result = kpiC2ResultRepository.save(kpiC2Result);

        kpiC2ResultDTO.setId(kpiC2Result.getId());

        return kpiC2ResultDTO;
    }

    @NotNull
    private static KpiC2Result getKpiC2Result(
        KpiC2ResultDTO kpiC2ResultDTO,
        Instance instance,
        InstanceModule instanceModule
    ) {
        KpiC2Result kpiC2Result = new KpiC2Result();
        kpiC2Result.setInstance(instance);
        kpiC2Result.setInstanceModule(instanceModule);
        kpiC2Result.setAnalysisDate(kpiC2ResultDTO.getAnalysisDate());
        //kpiC2Result.setExcludePlannedShutdown(kpiC2ResultDTO.getExcludePlannedShutdown());
        //kpiC2Result.setExcludeUnplannedShutdown(kpiC2ResultDTO.getExcludeUnplannedShutdown());
        kpiC2Result.setEligibilityThreshold(kpiC2ResultDTO.getEligibilityThreshold());
        kpiC2Result.setTolerance(kpiC2ResultDTO.getTolerance());
        kpiC2Result.setEvaluationType(kpiC2ResultDTO.getEvaluationType());
        kpiC2Result.setOutcome(kpiC2ResultDTO.getOutcome());

        return kpiC2Result;
    }

    @Override
    public int deleteAllByInstanceModule(long instanceModuleId) {
        return kpiC2ResultRepository.deleteAllByInstanceModuleId(instanceModuleId);
    }

    @Override
    public void updateKpiC2ResultOutcome(long id, OutcomeStatus outcomeStatus) {
        kpiC2ResultRepository
            .findById(id)
            .ifPresent(kpiC2Result -> {
                kpiC2Result.setOutcome(outcomeStatus);
                kpiC2ResultRepository.save(kpiC2Result);
            });
    }

    @Override
    @Transactional(readOnly = true)
    public List<KpiC2ResultDTO> findByInstanceModuleId(long instanceModuleId) {
        LOGGER.debug("Request to get KpiC2Results by instanceModuleId: {}", instanceModuleId);
        return kpiC2ResultRepository
            .findAllByInstanceModuleIdOrderByAnalysisDateDesc(instanceModuleId)
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    private KpiC2ResultDTO convertToDTO(KpiC2Result kpiC2Result) {
        KpiC2ResultDTO dto = new KpiC2ResultDTO();
        dto.setId(kpiC2Result.getId());
        dto.setInstanceId(kpiC2Result.getInstance().getId());
        dto.setInstanceModuleId(kpiC2Result.getInstanceModule().getId());
        dto.setAnalysisDate(kpiC2Result.getAnalysisDate());
        //dto.setExcludePlannedShutdown(kpiC2Result.getExcludePlannedShutdown());
        //dto.setExcludeUnplannedShutdown(kpiC2Result.getExcludeUnplannedShutdown());
        dto.setEligibilityThreshold(kpiC2Result.getEligibilityThreshold());
        dto.setTolerance(kpiC2Result.getTolerance());
        dto.setEvaluationType(kpiC2Result.getEvaluationType());
        dto.setOutcome(kpiC2Result.getOutcome());
        return dto;
    }
}
