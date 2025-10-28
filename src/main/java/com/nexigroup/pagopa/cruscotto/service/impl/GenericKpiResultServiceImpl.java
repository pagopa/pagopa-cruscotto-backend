package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.KpiResult;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.ModuleCode;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import com.nexigroup.pagopa.cruscotto.repository.KpiResultRepository;
import com.nexigroup.pagopa.cruscotto.service.GenericKpiResultService;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiResultDTO;
import com.nexigroup.pagopa.cruscotto.service.mapper.KpiResultMapper;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing KPI Results generically.
 */
@Service
@Transactional
public class GenericKpiResultServiceImpl implements GenericKpiResultService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GenericKpiResultServiceImpl.class);

    private final KpiResultRepository kpiResultRepository;
    private final KpiResultMapper kpiResultMapper;

    public GenericKpiResultServiceImpl(KpiResultRepository kpiResultRepository, KpiResultMapper kpiResultMapper) {
        this.kpiResultRepository = kpiResultRepository;
        this.kpiResultMapper = kpiResultMapper;
    }

    @Override
    public KpiResultDTO save(KpiResultDTO kpiResultDTO) {
        LOGGER.debug("Request to save KpiResult : {}", kpiResultDTO);
        KpiResult kpiResult = kpiResultMapper.toEntity(kpiResultDTO);
        kpiResult = kpiResultRepository.save(kpiResult);
        return kpiResultMapper.toDto(kpiResult);
    }

    @Override
    public KpiResultDTO update(KpiResultDTO kpiResultDTO) {
        LOGGER.debug("Request to update KpiResult : {}", kpiResultDTO);
        KpiResult kpiResult = kpiResultMapper.toEntity(kpiResultDTO);
        kpiResult = kpiResultRepository.save(kpiResult);
        return kpiResultMapper.toDto(kpiResult);
    }

    @Override
    public Optional<KpiResultDTO> partialUpdate(KpiResultDTO kpiResultDTO) {
        LOGGER.debug("Request to partially update KpiResult : {}", kpiResultDTO);
        return kpiResultRepository
            .findById(kpiResultDTO.getId())
            .map(existingKpiResult -> {
                kpiResultMapper.partialUpdate(existingKpiResult, kpiResultDTO);
                return existingKpiResult;
            })
            .map(kpiResultRepository::save)
            .map(kpiResultMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<KpiResultDTO> findAll(ModuleCode moduleCode) {
        LOGGER.debug("Request to get all KpiResults for module: {}", moduleCode);
        return kpiResultRepository.findByModuleCode(moduleCode)
            .stream()
            .map(kpiResultMapper::toDto)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<KpiResultDTO> findOne(Long id) {
        LOGGER.debug("Request to get KpiResult : {}", id);
        return kpiResultRepository.findById(id).map(kpiResultMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOGGER.debug("Request to delete KpiResult : {}", id);
        kpiResultRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<KpiResultDTO> findByInstanceModuleId(ModuleCode moduleCode, Long instanceModuleId) {
        LOGGER.debug("Request to get KpiResults by instanceModuleId: {} for module: {}", instanceModuleId, moduleCode);
        return kpiResultRepository.findByModuleCodeAndInstanceModuleId(moduleCode, instanceModuleId)
            .stream()
            .map(kpiResultMapper::toDto)
            .toList();
    }

    @Override
    public void deleteAllByInstanceModuleId(ModuleCode moduleCode, Long instanceModuleId) {
        LOGGER.debug("Request to delete all KpiResults by instanceModuleId: {} for module: {}", instanceModuleId, moduleCode);
        kpiResultRepository.deleteByModuleCodeAndInstanceModuleId(moduleCode, instanceModuleId);
    }

    @Override
    public void updateOutcome(Long id, OutcomeStatus outcome) {
        LOGGER.debug("Request to update outcome for KpiResult: {} to {}", id, outcome);
        kpiResultRepository.findById(id).ifPresent(kpiResult -> {
            kpiResult.setOutcome(outcome);
            kpiResultRepository.save(kpiResult);
        });
    }

    @Override
    @Transactional(readOnly = true)
    public List<KpiResultDTO> findByInstanceModuleIdAndOutcome(ModuleCode moduleCode, Long instanceModuleId, OutcomeStatus outcome) {
        LOGGER.debug("Request to get KpiResults by instanceModuleId: {} and outcome: {} for module: {}", instanceModuleId, outcome, moduleCode);
        return kpiResultRepository.findByModuleCodeAndInstanceModuleIdAndOutcome(moduleCode, instanceModuleId, outcome)
            .stream()
            .map(kpiResultMapper::toDto)
            .toList();
    }
}