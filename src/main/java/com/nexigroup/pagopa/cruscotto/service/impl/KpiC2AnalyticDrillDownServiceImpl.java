package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.KpiC2AnalyticDrillDown;
import com.nexigroup.pagopa.cruscotto.repository.KpiC2AnalyticDrillDownRepository;
import com.nexigroup.pagopa.cruscotto.repository.kpiC2AnalyticDrillDownRepository;
import com.nexigroup.pagopa.cruscotto.service.KpiC2AnalyticDrillDownService;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiC2AnalyticDrillDownDTO;
import com.nexigroup.pagopa.cruscotto.service.mapper.KpiC2AnalyticDrillDownMapper;
import com.nexigroup.pagopa.cruscotto.service.mapper.KpiC2DrillDownMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing {@link KpiC2AnalyticDrillDown}.
 */
@Service
@Transactional
public class KpiC2AnalyticDrillDownServiceImpl implements KpiC2AnalyticDrillDownService {

    private static final Logger LOGGER = LoggerFactory.getLogger(KpiC2AnalyticDrillDownServiceImpl.class);

    private final KpiC2AnalyticDrillDownRepository kpiC2AnalyticDrillDownRepository;
    private final KpiC2DrillDownMapper kpiC2DrillDownMapper;

    public KpiC2AnalyticDrillDownServiceImpl(
        KpiC2AnalyticDrillDownRepository kpiC2AnalyticDrillDownRepository,
        KpiC2DrillDownMapper kpiC2DrillDownMapper
    ) {
        this.kpiC2AnalyticDrillDownRepository = kpiC2AnalyticDrillDownRepository;
        this.kpiC2DrillDownMapper = kpiC2DrillDownMapper;
    }

    @Override
    public KpiC2AnalyticDrillDownDTO save(KpiC2AnalyticDrillDownDTO KpiC2AnalyticDrillDownDTO) {
        LOGGER.debug("Request to save KpiC2AnalyticDrillDown : {}", KpiC2AnalyticDrillDownDTO);
        KpiC2AnalyticDrillDown KpiC2AnalyticDrillDown = kpiC2DrillDownMapper.toEntity(KpiC2AnalyticDrillDownDTO);
        KpiC2AnalyticDrillDown = kpiC2AnalyticDrillDownRepository.save(KpiC2AnalyticDrillDown);
        return kpiC2DrillDownMapper.toDto(KpiC2AnalyticDrillDown);
    }



    @Override
    @Transactional(readOnly = true)
    public List<KpiC2AnalyticDrillDownDTO> findByKpiC2AnalyticDataId(Long analyticDataId) {
        LOGGER.debug("Request to get all KpiC2AnalyticDrillDown by KpiB8AnalyticData ID: {}", analyticDataId);
        return kpiC2AnalyticDrillDownRepository.selectByKpiC2AnalyticDataId(analyticDataId)
            .stream()
            .map(kpiC2DrillDownMapper::toDto)
            .collect(Collectors.toList());
    }


    @Override
    @Transactional(readOnly = true)
    public List<KpiC2AnalyticDrillDownDTO> findByInstanceIdAndAnalysisDate(Long instanceId, LocalDate analysisDate) {
        LOGGER.debug("Request to get all KpiC2AnalyticDrillDown by instance ID: {} and analysis date: {}", instanceId, analysisDate);
        return kpiC2AnalyticDrillDownRepository.findByInstanceIdAndAnalysisDate(instanceId, analysisDate)
            .stream()
            .map(kpiC2DrillDownMapper::toDto)
            .collect(Collectors.toList());
    }

    @Override
    public int deleteAllByInstanceModuleId(Long instanceModuleId) {
        LOGGER.debug("Request to delete all KpiC2AnalyticDrillDown by instance module ID: {}", instanceModuleId);
        return kpiC2AnalyticDrillDownRepository.deleteAllByInstanceModuleId(instanceModuleId);
    }

    @Override
    public int deleteByInstanceModuleIdAndAnalysisDate(Long instanceModuleId, LocalDate analysisDate) {
        LOGGER.debug("Request to delete KpiC2AnalyticDrillDown by instance module ID: {} and analysis date: {}", instanceModuleId, analysisDate);
        return kpiC2AnalyticDrillDownRepository.deleteByInstanceModuleIdAndAnalysisDate(instanceModuleId, analysisDate);
    }
}
