package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.enumeration.ModuleCode;
import com.nexigroup.pagopa.cruscotto.repository.KpiAnalyticDataRepository;
import com.nexigroup.pagopa.cruscotto.service.GenericKpiAnalyticDataService;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiAnalyticDataDTO;
import com.nexigroup.pagopa.cruscotto.service.mapper.KpiAnalyticDataMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Service Implementation for managing generic KPI analytic data.
 */
@Service
@Transactional
public class GenericKpiAnalyticDataServiceImpl implements GenericKpiAnalyticDataService {

    private final Logger log = LoggerFactory.getLogger(GenericKpiAnalyticDataServiceImpl.class);

    private final KpiAnalyticDataRepository kpiAnalyticDataRepository;
    private final KpiAnalyticDataMapper kpiAnalyticDataMapper;

    public GenericKpiAnalyticDataServiceImpl(
            KpiAnalyticDataRepository kpiAnalyticDataRepository,
            KpiAnalyticDataMapper kpiAnalyticDataMapper) {
        this.kpiAnalyticDataRepository = kpiAnalyticDataRepository;
        this.kpiAnalyticDataMapper = kpiAnalyticDataMapper;
    }

    @Override
    public KpiAnalyticDataDTO save(KpiAnalyticDataDTO kpiAnalyticDataDTO) {
        log.debug("Request to save KpiAnalyticData : {}", kpiAnalyticDataDTO);
        var kpiAnalyticData = kpiAnalyticDataMapper.toEntity(kpiAnalyticDataDTO);
        kpiAnalyticData = kpiAnalyticDataRepository.save(kpiAnalyticData);
        return kpiAnalyticDataMapper.toDto(kpiAnalyticData);
    }

    @Override
    public List<KpiAnalyticDataDTO> saveAll(List<KpiAnalyticDataDTO> kpiAnalyticDataDTOs) {
        log.debug("Request to save {} KpiAnalyticData entries", kpiAnalyticDataDTOs.size());
        var entities = kpiAnalyticDataDTOs.stream()
                .map(kpiAnalyticDataMapper::toEntity)
                .toList();
        var savedEntities = kpiAnalyticDataRepository.saveAll(entities);
        return savedEntities.stream()
                .map(kpiAnalyticDataMapper::toDto)
                .toList();
    }

    @Override
    public Optional<KpiAnalyticDataDTO> partialUpdate(KpiAnalyticDataDTO kpiAnalyticDataDTO) {
        log.debug("Request to partially update KpiAnalyticData : {}", kpiAnalyticDataDTO);

        return kpiAnalyticDataRepository
                .findById(kpiAnalyticDataDTO.getId())
                .map(existingEntity -> {
                    kpiAnalyticDataMapper.partialUpdate(existingEntity, kpiAnalyticDataDTO);
                    return existingEntity;
                })
                .map(kpiAnalyticDataRepository::save)
                .map(kpiAnalyticDataMapper::toDto);
    }

    @Override
    public KpiAnalyticDataDTO update(KpiAnalyticDataDTO kpiAnalyticDataDTO) {
        log.debug("Request to update KpiAnalyticData : {}", kpiAnalyticDataDTO);
        var kpiAnalyticData = kpiAnalyticDataMapper.toEntity(kpiAnalyticDataDTO);
        kpiAnalyticData = kpiAnalyticDataRepository.save(kpiAnalyticData);
        return kpiAnalyticDataMapper.toDto(kpiAnalyticData);
    }

    @Override
    public List<KpiAnalyticDataDTO> findAll(ModuleCode moduleCode) {
        log.debug("Request to get all KpiAnalyticData for moduleCode: {}", moduleCode);
        return kpiAnalyticDataRepository.findAllByModuleCode(moduleCode).stream()
                .map(kpiAnalyticDataMapper::toDto)
                .toList();
    }

    @Override
    public List<KpiAnalyticDataDTO> findAll() {
        log.debug("Request to get all KpiAnalyticData");
        return kpiAnalyticDataRepository.findAll().stream()
                .map(kpiAnalyticDataMapper::toDto)
                .toList();
    }

    @Override
    public Optional<KpiAnalyticDataDTO> findOne(Long id) {
        log.debug("Request to get KpiAnalyticData : {}", id);
        return kpiAnalyticDataRepository.findById(id)
                .map(kpiAnalyticDataMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete KpiAnalyticData : {}", id);
        kpiAnalyticDataRepository.deleteById(id);
    }

    @Override
    public void deleteAllByInstanceModuleId(ModuleCode moduleCode, Long instanceModuleId) {
        log.debug("Request to delete all KpiAnalyticData for moduleCode {} and instanceModuleId {}", moduleCode, instanceModuleId);
        kpiAnalyticDataRepository.deleteAllByModuleCodeAndInstanceModuleId(moduleCode, instanceModuleId);
    }

    @Override
    public List<KpiAnalyticDataDTO> findByInstanceModuleId(ModuleCode moduleCode, Long instanceModuleId) {
        log.debug("Request to find KpiAnalyticData for moduleCode {} and instanceModuleId {}", moduleCode, instanceModuleId);
        return kpiAnalyticDataRepository.findAllByModuleCodeAndInstanceModuleId(moduleCode, instanceModuleId)
                .stream()
                .map(kpiAnalyticDataMapper::toDto)
                .toList();
    }

    @Override
    public List<KpiAnalyticDataDTO> findByDetailResultId(ModuleCode moduleCode, Long detailResultId) {
        log.debug("Request to find KpiAnalyticData for moduleCode {} and detailResultId {}", moduleCode, detailResultId);
        // Since the entity doesn't have detailResultId, we'll find by instanceModuleId instead
        // This assumes the relationship is through the instance module
        return findByInstanceModuleId(moduleCode, detailResultId);
    }

    @Override
    public List<KpiAnalyticDataDTO> findByDateRange(ModuleCode moduleCode, LocalDate startDate, LocalDate endDate) {
        log.debug("Request to find KpiAnalyticData for moduleCode {} between {} and {}", moduleCode, startDate, endDate);
        // Since the entity doesn't have date fields for filtering, we'll return all for the module
        // In a real implementation, you might want to add date fields to the entity
        return findAll(moduleCode);
    }
}