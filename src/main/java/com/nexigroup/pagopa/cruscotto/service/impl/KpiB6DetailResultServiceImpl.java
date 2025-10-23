package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.Instance;
import com.nexigroup.pagopa.cruscotto.domain.InstanceModule;
import com.nexigroup.pagopa.cruscotto.domain.KpiB6DetailResult;
import com.nexigroup.pagopa.cruscotto.domain.KpiB6Result;
import com.nexigroup.pagopa.cruscotto.repository.InstanceModuleRepository;
import com.nexigroup.pagopa.cruscotto.repository.InstanceRepository;
import com.nexigroup.pagopa.cruscotto.repository.KpiB6DetailResultRepository;
import com.nexigroup.pagopa.cruscotto.repository.KpiB6ResultRepository;
import com.nexigroup.pagopa.cruscotto.service.KpiB6DetailResultService;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB6DetailResultDTO;
import com.nexigroup.pagopa.cruscotto.service.mapper.KpiB6DetailResultMapper;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link KpiB6DetailResult}.
 */
@Service
@Transactional
public class KpiB6DetailResultServiceImpl implements KpiB6DetailResultService {

    private static final Logger LOGGER = LoggerFactory.getLogger(KpiB6DetailResultServiceImpl.class);

    private final InstanceRepository instanceRepository;
    private final InstanceModuleRepository instanceModuleRepository;
    private final KpiB6ResultRepository kpiB6ResultRepository;
    private final KpiB6DetailResultRepository kpiB6DetailResultRepository;
    private final KpiB6DetailResultMapper kpiB6DetailResultMapper;

    public KpiB6DetailResultServiceImpl(
        InstanceRepository instanceRepository,
        InstanceModuleRepository instanceModuleRepository,
        KpiB6ResultRepository kpiB6ResultRepository,
        KpiB6DetailResultRepository kpiB6DetailResultRepository,
        KpiB6DetailResultMapper kpiB6DetailResultMapper
    ) {
        this.instanceRepository = instanceRepository;
        this.instanceModuleRepository = instanceModuleRepository;
        this.kpiB6ResultRepository = kpiB6ResultRepository;
        this.kpiB6DetailResultRepository = kpiB6DetailResultRepository;
        this.kpiB6DetailResultMapper = kpiB6DetailResultMapper;
    }

    @Override
    public KpiB6DetailResultDTO save(KpiB6DetailResultDTO kpiB6DetailResultDTO) {
        LOGGER.debug("Request to save KpiB6DetailResult : {}", kpiB6DetailResultDTO);
        
        KpiB6DetailResult kpiB6DetailResult = kpiB6DetailResultMapper.toEntity(kpiB6DetailResultDTO);
        
        // Set relationships
        if (kpiB6DetailResultDTO.getInstanceId() != null) {
            Instance instance = instanceRepository.getReferenceById(kpiB6DetailResultDTO.getInstanceId());
            kpiB6DetailResult.setInstance(instance);
        }
        
        if (kpiB6DetailResultDTO.getInstanceModuleId() != null) {
            InstanceModule instanceModule = instanceModuleRepository.getReferenceById(kpiB6DetailResultDTO.getInstanceModuleId());
            kpiB6DetailResult.setInstanceModule(instanceModule);
        }
        
        if (kpiB6DetailResultDTO.getKpiB6ResultId() != null) {
            KpiB6Result kpiB6Result = kpiB6ResultRepository.getReferenceById(kpiB6DetailResultDTO.getKpiB6ResultId());
            kpiB6DetailResult.setKpiB6Result(kpiB6Result);
        }
        
        kpiB6DetailResult = kpiB6DetailResultRepository.save(kpiB6DetailResult);
        return kpiB6DetailResultMapper.toDto(kpiB6DetailResult);
    }

    @Override
    public List<KpiB6DetailResultDTO> saveAll(List<KpiB6DetailResultDTO> kpiB6DetailResultDTOs) {
        LOGGER.debug("Request to save {} KpiB6DetailResults", kpiB6DetailResultDTOs.size());
        
        return kpiB6DetailResultDTOs.stream()
            .map(this::save)
            .collect(Collectors.toList());
    }

    @Override
    public KpiB6DetailResultDTO update(KpiB6DetailResultDTO kpiB6DetailResultDTO) {
        LOGGER.debug("Request to update KpiB6DetailResult : {}", kpiB6DetailResultDTO);
        return save(kpiB6DetailResultDTO);
    }

    @Override
    public Optional<KpiB6DetailResultDTO> partialUpdate(KpiB6DetailResultDTO kpiB6DetailResultDTO) {
        LOGGER.debug("Request to partially update KpiB6DetailResult : {}", kpiB6DetailResultDTO);
        
        return kpiB6DetailResultRepository
            .findById(kpiB6DetailResultDTO.getId())
            .map(existingEntity -> {
                kpiB6DetailResultMapper.partialUpdate(existingEntity, kpiB6DetailResultDTO);
                return existingEntity;
            })
            .map(kpiB6DetailResultRepository::save)
            .map(kpiB6DetailResultMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<KpiB6DetailResultDTO> findAll() {
        LOGGER.debug("Request to get all KpiB6DetailResults");
        return kpiB6DetailResultRepository.findAll().stream()
            .map(kpiB6DetailResultMapper::toDto)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<KpiB6DetailResultDTO> findOne(Long id) {
        LOGGER.debug("Request to get KpiB6DetailResult : {}", id);
        return kpiB6DetailResultRepository.findById(id)
            .map(kpiB6DetailResultMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<KpiB6DetailResultDTO> findAllByKpiB6ResultId(Long kpiB6ResultId) {
        LOGGER.debug("Request to get all KpiB6DetailResults by kpiB6ResultId : {}", kpiB6ResultId);
        return kpiB6DetailResultRepository.findAllByKpiB6ResultId(kpiB6ResultId).stream()
            .map(kpiB6DetailResultMapper::toDto)
            .collect(Collectors.toList());
    }

    @Override
    public void delete(Long id) {
        LOGGER.debug("Request to delete KpiB6DetailResult : {}", id);
        kpiB6DetailResultRepository.deleteById(id);
    }

    @Override
    public int deleteAllByInstanceModule(long instanceModuleId) {
        LOGGER.debug("Request to delete all KpiB6DetailResult by instanceModuleId : {}", instanceModuleId);
        return kpiB6DetailResultRepository.deleteAllByInstanceModuleId(instanceModuleId);
    }
}