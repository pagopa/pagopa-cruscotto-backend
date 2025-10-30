package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.enumeration.ModuleCode;
import com.nexigroup.pagopa.cruscotto.repository.KpiDetailResultRepository;
import com.nexigroup.pagopa.cruscotto.service.GenericKpiDetailResultService;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiDetailResultDTO;
import com.nexigroup.pagopa.cruscotto.service.mapper.KpiDetailResultMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service Implementation for managing generic KPI detail results.
 */
@Service
@Transactional
public class GenericKpiDetailResultServiceImpl implements GenericKpiDetailResultService {

    private final Logger log = LoggerFactory.getLogger(GenericKpiDetailResultServiceImpl.class);

    private final KpiDetailResultRepository kpiDetailResultRepository;
    private final KpiDetailResultMapper kpiDetailResultMapper;

    public GenericKpiDetailResultServiceImpl(
            KpiDetailResultRepository kpiDetailResultRepository,
            KpiDetailResultMapper kpiDetailResultMapper) {
        this.kpiDetailResultRepository = kpiDetailResultRepository;
        this.kpiDetailResultMapper = kpiDetailResultMapper;
    }

    @Override
    public KpiDetailResultDTO save(KpiDetailResultDTO kpiDetailResultDTO) {
        log.debug("Request to save KpiDetailResult : {}", kpiDetailResultDTO);
        var kpiDetailResult = kpiDetailResultMapper.toEntity(kpiDetailResultDTO);
        kpiDetailResult = kpiDetailResultRepository.save(kpiDetailResult);
        return kpiDetailResultMapper.toDto(kpiDetailResult);
    }

    @Override
    public List<KpiDetailResultDTO> saveAll(List<KpiDetailResultDTO> kpiDetailResultDTOs) {
        log.debug("Request to save {} KpiDetailResults", kpiDetailResultDTOs.size());
        var entities = kpiDetailResultDTOs.stream()
                .map(kpiDetailResultMapper::toEntity)
                .toList();
        var savedEntities = kpiDetailResultRepository.saveAll(entities);
        return savedEntities.stream()
                .map(kpiDetailResultMapper::toDto)
                .toList();
    }

    @Override
    public Optional<KpiDetailResultDTO> partialUpdate(KpiDetailResultDTO kpiDetailResultDTO) {
        log.debug("Request to partially update KpiDetailResult : {}", kpiDetailResultDTO);

        return kpiDetailResultRepository
                .findById(kpiDetailResultDTO.getId())
                .map(existingEntity -> {
                    kpiDetailResultMapper.partialUpdate(kpiDetailResultDTO, existingEntity);
                    return existingEntity;
                })
                .map(kpiDetailResultRepository::save)
                .map(kpiDetailResultMapper::toDto);
    }

    @Override
    public KpiDetailResultDTO update(KpiDetailResultDTO kpiDetailResultDTO) {
        log.debug("Request to update KpiDetailResult : {}", kpiDetailResultDTO);
        var kpiDetailResult = kpiDetailResultMapper.toEntity(kpiDetailResultDTO);
        kpiDetailResult = kpiDetailResultRepository.save(kpiDetailResult);
        return kpiDetailResultMapper.toDto(kpiDetailResult);
    }

    @Override
    public List<KpiDetailResultDTO> findAll(ModuleCode moduleCode) {
        log.debug("Request to get all KpiDetailResults for moduleCode: {}", moduleCode);
        return kpiDetailResultRepository.findAllByModuleCode(moduleCode).stream()
                .map(kpiDetailResultMapper::toDto)
                .toList();
    }

    @Override
    public List<KpiDetailResultDTO> findAll() {
        log.debug("Request to get all KpiDetailResults");
        return kpiDetailResultRepository.findAll().stream()
                .map(kpiDetailResultMapper::toDto)
                .toList();
    }

    @Override
    public Optional<KpiDetailResultDTO> findOne(Long id) {
        log.debug("Request to get KpiDetailResult : {}", id);
        return kpiDetailResultRepository.findById(id)
                .map(kpiDetailResultMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete KpiDetailResult : {}", id);
        kpiDetailResultRepository.deleteById(id);
    }

    @Override
    public void deleteAllByInstanceModuleId(ModuleCode moduleCode, Long instanceModuleId) {
        log.debug("Request to delete all KpiDetailResults for moduleCode {} and instanceModuleId {}", moduleCode, instanceModuleId);
        kpiDetailResultRepository.deleteAllByModuleCodeAndInstanceModuleId(moduleCode, instanceModuleId);
    }

    @Override
    public List<KpiDetailResultDTO> findByInstanceModuleId(ModuleCode moduleCode, Long instanceModuleId) {
        log.debug("Request to find KpiDetailResults for moduleCode {} and instanceModuleId {}", moduleCode, instanceModuleId);
        return kpiDetailResultRepository.findAllByModuleCodeAndInstanceModuleId(moduleCode, instanceModuleId)
                .stream()
                .map(kpiDetailResultMapper::toDto)
                .toList();
    }

    @Override
    public List<KpiDetailResultDTO> findByKpiResultId(ModuleCode moduleCode, Long kpiResultId) {
        log.debug("Request to find KpiDetailResults for moduleCode {} and kpiResultId {}", moduleCode, kpiResultId);
        return kpiDetailResultRepository.findAllByModuleCodeAndKpiResultId(moduleCode, kpiResultId)
                .stream()
                .map(kpiDetailResultMapper::toDto)
                .toList();
    }
}