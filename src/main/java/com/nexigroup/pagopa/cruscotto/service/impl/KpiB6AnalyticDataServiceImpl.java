package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.Instance;
import com.nexigroup.pagopa.cruscotto.domain.InstanceModule;
import com.nexigroup.pagopa.cruscotto.domain.KpiB6AnalyticData;
import com.nexigroup.pagopa.cruscotto.domain.KpiB6DetailResult;
import com.nexigroup.pagopa.cruscotto.repository.InstanceModuleRepository;
import com.nexigroup.pagopa.cruscotto.repository.InstanceRepository;
import com.nexigroup.pagopa.cruscotto.repository.KpiB6AnalyticDataRepository;
import com.nexigroup.pagopa.cruscotto.repository.KpiB6DetailResultRepository;
import com.nexigroup.pagopa.cruscotto.service.KpiB6AnalyticDataService;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB6AnalyticDataDTO;
import com.nexigroup.pagopa.cruscotto.service.mapper.KpiB6AnalyticDataMapper;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link KpiB6AnalyticData}.
 */
@Service
@Transactional
public class KpiB6AnalyticDataServiceImpl implements KpiB6AnalyticDataService {

    private static final Logger LOGGER = LoggerFactory.getLogger(KpiB6AnalyticDataServiceImpl.class);

    private final InstanceRepository instanceRepository;
    private final InstanceModuleRepository instanceModuleRepository;
    private final KpiB6DetailResultRepository kpiB6DetailResultRepository;
    private final KpiB6AnalyticDataRepository kpiB6AnalyticDataRepository;
    private final KpiB6AnalyticDataMapper kpiB6AnalyticDataMapper;

    public KpiB6AnalyticDataServiceImpl(
        InstanceRepository instanceRepository,
        InstanceModuleRepository instanceModuleRepository,
        KpiB6DetailResultRepository kpiB6DetailResultRepository,
        KpiB6AnalyticDataRepository kpiB6AnalyticDataRepository,
        KpiB6AnalyticDataMapper kpiB6AnalyticDataMapper
    ) {
        this.instanceRepository = instanceRepository;
        this.instanceModuleRepository = instanceModuleRepository;
        this.kpiB6DetailResultRepository = kpiB6DetailResultRepository;
        this.kpiB6AnalyticDataRepository = kpiB6AnalyticDataRepository;
        this.kpiB6AnalyticDataMapper = kpiB6AnalyticDataMapper;
    }

    @Override
    public KpiB6AnalyticDataDTO save(KpiB6AnalyticDataDTO kpiB6AnalyticDataDTO) {
        LOGGER.debug("Request to save KpiB6AnalyticData : {}", kpiB6AnalyticDataDTO);
        
        KpiB6AnalyticData kpiB6AnalyticData = kpiB6AnalyticDataMapper.toEntity(kpiB6AnalyticDataDTO);
        
        // Set relationships
        if (kpiB6AnalyticDataDTO.getInstanceId() != null) {
            Instance instance = instanceRepository.getReferenceById(kpiB6AnalyticDataDTO.getInstanceId());
            kpiB6AnalyticData.setInstance(instance);
        }
        
        if (kpiB6AnalyticDataDTO.getInstanceModuleId() != null) {
            InstanceModule instanceModule = instanceModuleRepository.getReferenceById(kpiB6AnalyticDataDTO.getInstanceModuleId());
            kpiB6AnalyticData.setInstanceModule(instanceModule);
        }
        
        if (kpiB6AnalyticDataDTO.getKpiB6DetailResultId() != null) {
            KpiB6DetailResult kpiB6DetailResult = kpiB6DetailResultRepository.getReferenceById(kpiB6AnalyticDataDTO.getKpiB6DetailResultId());
            kpiB6AnalyticData.setKpiB6DetailResult(kpiB6DetailResult);
        }
        
        kpiB6AnalyticData = kpiB6AnalyticDataRepository.save(kpiB6AnalyticData);
        return kpiB6AnalyticDataMapper.toDto(kpiB6AnalyticData);
    }

    @Override
    public List<KpiB6AnalyticDataDTO> saveAll(List<KpiB6AnalyticDataDTO> kpiB6AnalyticDataDTOs) {
        LOGGER.debug("Request to save {} KpiB6AnalyticData", kpiB6AnalyticDataDTOs.size());
        
        return kpiB6AnalyticDataDTOs.stream()
            .map(this::save)
            .collect(Collectors.toList());
    }

    @Override
    public KpiB6AnalyticDataDTO update(KpiB6AnalyticDataDTO kpiB6AnalyticDataDTO) {
        LOGGER.debug("Request to update KpiB6AnalyticData : {}", kpiB6AnalyticDataDTO);
        return save(kpiB6AnalyticDataDTO);
    }

    @Override
    public Optional<KpiB6AnalyticDataDTO> partialUpdate(KpiB6AnalyticDataDTO kpiB6AnalyticDataDTO) {
        LOGGER.debug("Request to partially update KpiB6AnalyticData : {}", kpiB6AnalyticDataDTO);
        
        return kpiB6AnalyticDataRepository
            .findById(kpiB6AnalyticDataDTO.getId())
            .map(existingEntity -> {
                kpiB6AnalyticDataMapper.partialUpdate(existingEntity, kpiB6AnalyticDataDTO);
                return existingEntity;
            })
            .map(kpiB6AnalyticDataRepository::save)
            .map(kpiB6AnalyticDataMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<KpiB6AnalyticDataDTO> findAll() {
        LOGGER.debug("Request to get all KpiB6AnalyticData");
        return kpiB6AnalyticDataRepository.findAll().stream()
            .map(kpiB6AnalyticDataMapper::toDto)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<KpiB6AnalyticDataDTO> findByDetailResultId(Long detailResultId) {
        LOGGER.debug("Request to get KpiB6AnalyticData by detailResultId : {}", detailResultId);
        return kpiB6AnalyticDataRepository.findAllByKpiB6DetailResultId(detailResultId).stream()
            .map(kpiB6AnalyticDataMapper::toDto)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<KpiB6AnalyticDataDTO> findOne(Long id) {
        LOGGER.debug("Request to get KpiB6AnalyticData : {}", id);
        return kpiB6AnalyticDataRepository.findById(id)
            .map(kpiB6AnalyticDataMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<KpiB6AnalyticDataDTO> findAllByKpiB6DetailResultId(Long kpiB6DetailResultId) {
        LOGGER.debug("Request to get all KpiB6AnalyticData by kpiB6DetailResultId : {}", kpiB6DetailResultId);
        return kpiB6AnalyticDataRepository.findAllByKpiB6DetailResultId(kpiB6DetailResultId).stream()
            .map(kpiB6AnalyticDataMapper::toDto)
            .collect(Collectors.toList());
    }

    @Override
    public void delete(Long id) {
        LOGGER.debug("Request to delete KpiB6AnalyticData : {}", id);
        kpiB6AnalyticDataRepository.deleteById(id);
    }

    @Override
    public int deleteAllByInstanceModuleId(Long instanceModuleId) {
        LOGGER.debug("Request to delete all KpiB6AnalyticData by instanceModuleId : {}", instanceModuleId);
        return kpiB6AnalyticDataRepository.deleteAllByInstanceModuleId(instanceModuleId);
    }
}