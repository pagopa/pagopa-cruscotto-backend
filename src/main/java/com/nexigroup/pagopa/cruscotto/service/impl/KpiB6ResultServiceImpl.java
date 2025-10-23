package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.Instance;
import com.nexigroup.pagopa.cruscotto.domain.InstanceModule;
import com.nexigroup.pagopa.cruscotto.domain.KpiB6Result;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import com.nexigroup.pagopa.cruscotto.repository.InstanceModuleRepository;
import com.nexigroup.pagopa.cruscotto.repository.InstanceRepository;
import com.nexigroup.pagopa.cruscotto.repository.KpiB6ResultRepository;
import com.nexigroup.pagopa.cruscotto.service.KpiB6ResultService;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB6ResultDTO;
import com.nexigroup.pagopa.cruscotto.service.mapper.KpiB6ResultMapper;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link KpiB6Result}.
 */
@Service
@Transactional
public class KpiB6ResultServiceImpl implements KpiB6ResultService {

    private static final Logger LOGGER = LoggerFactory.getLogger(KpiB6ResultServiceImpl.class);

    private final InstanceRepository instanceRepository;

    private final InstanceModuleRepository instanceModuleRepository;

    private final KpiB6ResultRepository kpiB6ResultRepository;

    private final KpiB6ResultMapper kpiB6ResultMapper;

    public KpiB6ResultServiceImpl(
        InstanceRepository instanceRepository,
        InstanceModuleRepository instanceModuleRepository,
        KpiB6ResultRepository kpiB6ResultRepository,
        KpiB6ResultMapper kpiB6ResultMapper
    ) {
        this.instanceRepository = instanceRepository;
        this.instanceModuleRepository = instanceModuleRepository;
        this.kpiB6ResultRepository = kpiB6ResultRepository;
        this.kpiB6ResultMapper = kpiB6ResultMapper;
    }

    @Override
    public KpiB6ResultDTO save(KpiB6ResultDTO kpiB6ResultDTO) {
        LOGGER.debug("Request to save KpiB6Result : {}", kpiB6ResultDTO);
        
        KpiB6Result kpiB6Result = kpiB6ResultMapper.toEntity(kpiB6ResultDTO);
        
        // Set relationships
        if (kpiB6ResultDTO.getInstanceId() != null) {
            Instance instance = instanceRepository.getReferenceById(kpiB6ResultDTO.getInstanceId());
            kpiB6Result.setInstance(instance);
        }
        
        if (kpiB6ResultDTO.getInstanceModuleId() != null) {
            InstanceModule instanceModule = instanceModuleRepository.getReferenceById(kpiB6ResultDTO.getInstanceModuleId());
            kpiB6Result.setInstanceModule(instanceModule);
        }
        
        kpiB6Result = kpiB6ResultRepository.save(kpiB6Result);
        return kpiB6ResultMapper.toDto(kpiB6Result);
    }

    @Override
    public KpiB6ResultDTO update(KpiB6ResultDTO kpiB6ResultDTO) {
        LOGGER.debug("Request to update KpiB6Result : {}", kpiB6ResultDTO);
        
        KpiB6Result kpiB6Result = kpiB6ResultMapper.toEntity(kpiB6ResultDTO);
        
        // Set relationships
        if (kpiB6ResultDTO.getInstanceId() != null) {
            Instance instance = instanceRepository.getReferenceById(kpiB6ResultDTO.getInstanceId());
            kpiB6Result.setInstance(instance);
        }
        
        if (kpiB6ResultDTO.getInstanceModuleId() != null) {
            InstanceModule instanceModule = instanceModuleRepository.getReferenceById(kpiB6ResultDTO.getInstanceModuleId());
            kpiB6Result.setInstanceModule(instanceModule);
        }
        
        kpiB6Result = kpiB6ResultRepository.save(kpiB6Result);
        return kpiB6ResultMapper.toDto(kpiB6Result);
    }

    @Override
    public Optional<KpiB6ResultDTO> partialUpdate(KpiB6ResultDTO kpiB6ResultDTO) {
        LOGGER.debug("Request to partially update KpiB6Result : {}", kpiB6ResultDTO);

        return kpiB6ResultRepository
            .findById(kpiB6ResultDTO.getId())
            .map(existingKpiB6Result -> {
                kpiB6ResultMapper.partialUpdate(existingKpiB6Result, kpiB6ResultDTO);
                return existingKpiB6Result;
            })
            .map(kpiB6ResultRepository::save)
            .map(kpiB6ResultMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<KpiB6ResultDTO> findAll() {
        LOGGER.debug("Request to get all KpiB6Results");
        return kpiB6ResultRepository.findAll().stream()
            .map(kpiB6ResultMapper::toDto)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<KpiB6ResultDTO> findByInstanceModuleId(Long instanceModuleId) {
        LOGGER.debug("Request to get KpiB6Results by instanceModuleId : {}", instanceModuleId);
        return kpiB6ResultRepository.selectByInstanceModuleId(instanceModuleId).stream()
            .map(kpiB6ResultMapper::toDto)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<KpiB6ResultDTO> findOne(Long id) {
        LOGGER.debug("Request to get KpiB6Result : {}", id);
        return kpiB6ResultRepository.findById(id)
            .map(kpiB6ResultMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOGGER.debug("Request to delete KpiB6Result : {}", id);
        kpiB6ResultRepository.deleteById(id);
    }

    @Override
    public int deleteAllByInstanceModuleId(Long instanceModuleId) {
        LOGGER.debug("Request to delete all KpiB6Result by instanceModuleId : {}", instanceModuleId);
        return kpiB6ResultRepository.deleteAllByInstanceModuleId(instanceModuleId);
    }

    @Override
    public void updateOutcome(Long kpiB6ResultId, OutcomeStatus outcome) {
        LOGGER.debug("Request to update outcome for KpiB6Result : {} to {}", kpiB6ResultId, outcome);
        
        kpiB6ResultRepository.findById(kpiB6ResultId).ifPresent(kpiB6Result -> {
            kpiB6Result.setOutcome(outcome);
            kpiB6ResultRepository.save(kpiB6Result);
        });
    }
}