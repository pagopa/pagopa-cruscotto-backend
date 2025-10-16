package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.PagopaApiLogDrilldown;
import com.nexigroup.pagopa.cruscotto.repository.PagopaApiLogDrilldownRepository;
import com.nexigroup.pagopa.cruscotto.service.PagopaApiLogDrilldownService;
import com.nexigroup.pagopa.cruscotto.service.dto.PagopaAPILogDTO;
import com.nexigroup.pagopa.cruscotto.service.mapper.PagopaApiLogDrilldownMapper;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link PagopaApiLogDrilldown}.
 */
@Service
@Transactional
public class PagopaApiLogDrilldownServiceImpl implements PagopaApiLogDrilldownService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PagopaApiLogDrilldownServiceImpl.class);

    private final PagopaApiLogDrilldownRepository pagopaApiLogDrilldownRepository;
    private final PagopaApiLogDrilldownMapper pagopaApiLogDrilldownMapper;

    public PagopaApiLogDrilldownServiceImpl(
        PagopaApiLogDrilldownRepository pagopaApiLogDrilldownRepository,
        PagopaApiLogDrilldownMapper pagopaApiLogDrilldownMapper
    ) {
        this.pagopaApiLogDrilldownRepository = pagopaApiLogDrilldownRepository;
        this.pagopaApiLogDrilldownMapper = pagopaApiLogDrilldownMapper;
    }

    @Override
    public PagopaAPILogDTO save(PagopaAPILogDTO pagopaAPILogDTO) {
        LOGGER.debug("Request to save PagopaApiLogDrilldown : {}", pagopaAPILogDTO);
        PagopaApiLogDrilldown pagopaApiLogDrilldown = pagopaApiLogDrilldownMapper.toEntity(pagopaAPILogDTO);
        pagopaApiLogDrilldown = pagopaApiLogDrilldownRepository.save(pagopaApiLogDrilldown);
        return pagopaApiLogDrilldownMapper.toDto(pagopaApiLogDrilldown);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PagopaAPILogDTO> findByKpiB4AnalyticDataId(Long analyticDataId) {
        LOGGER.debug("Request to get all PagopaApiLogDrilldown by KpiB4AnalyticData ID: {}", analyticDataId);
        return pagopaApiLogDrilldownRepository.findByKpiB4AnalyticDataId(analyticDataId)
            .stream()
            .map(pagopaApiLogDrilldownMapper::toDto)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PagopaAPILogDTO> findByInstanceIdAndAnalysisDate(Long instanceId, LocalDate analysisDate) {
        LOGGER.debug("Request to get all PagopaApiLogDrilldown by instance ID: {} and analysis date: {}", instanceId, analysisDate);
        return pagopaApiLogDrilldownRepository.findByInstanceIdAndAnalysisDate(instanceId, analysisDate)
            .stream()
            .map(pagopaApiLogDrilldownMapper::toDto)
            .collect(Collectors.toList());
    }

    @Override
    public int deleteAllByInstanceModuleId(Long instanceModuleId) {
        LOGGER.debug("Request to delete all PagopaApiLogDrilldown by instance module ID: {}", instanceModuleId);
        return pagopaApiLogDrilldownRepository.deleteAllByInstanceModuleId(instanceModuleId);
    }

    @Override
    public int deleteByInstanceModuleIdAndAnalysisDate(Long instanceModuleId, LocalDate analysisDate) {
        LOGGER.debug("Request to delete PagopaApiLogDrilldown by instance module ID: {} and analysis date: {}", instanceModuleId, analysisDate);
        return pagopaApiLogDrilldownRepository.deleteByInstanceModuleIdAndAnalysisDate(instanceModuleId, analysisDate);
    }
}