package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.KpiB4DetailResult;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import com.nexigroup.pagopa.cruscotto.repository.KpiB4DetailResultRepository;
import com.nexigroup.pagopa.cruscotto.service.KpiB4DetailResultService;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB4DetailResultDTO;

import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing KpiB4DetailResult.
 */
@Service
@Transactional
public class KpiB4DetailResultServiceImpl implements KpiB4DetailResultService {

    private static final Logger LOGGER = LoggerFactory.getLogger(KpiB4DetailResultServiceImpl.class);

    private final KpiB4DetailResultRepository kpiB4DetailResultRepository;

    public KpiB4DetailResultServiceImpl(KpiB4DetailResultRepository kpiB4DetailResultRepository) {
        this.kpiB4DetailResultRepository = kpiB4DetailResultRepository;
    }

    @Override
    public KpiB4DetailResultDTO save(KpiB4DetailResultDTO kpiB4DetailResultDTO) {
        // TODO: Implement save logic
        LOGGER.debug("Request to save KpiB4DetailResult: {}", kpiB4DetailResultDTO);
        return kpiB4DetailResultDTO;
    }

    @Override
    public int deleteAllByInstanceModule(long instanceModuleId) {
        // TODO: Implement delete logic
        LOGGER.debug("Request to delete all KpiB4DetailResult by instanceModuleId: {}", instanceModuleId);
        return 0;
    }

    @Override
    public void updateKpiB4DetailResultOutcome(long id, OutcomeStatus outcomeStatus) {
        // TODO: Implement update outcome logic
        LOGGER.debug("Request to update KpiB4DetailResult outcome: {} with status: {}", id, outcomeStatus);
    }

    @Override
    @Transactional(readOnly = true)
    public List<KpiB4DetailResultDTO> findByResultId(long resultId) {
        LOGGER.debug("Request to get KpiB4DetailResults by resultId: {}", resultId);
        return kpiB4DetailResultRepository
            .findAllByResultIdOrderByAnalysisDateDesc(resultId)
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    private KpiB4DetailResultDTO convertToDTO(KpiB4DetailResult kpiB4DetailResult) {
        KpiB4DetailResultDTO dto = new KpiB4DetailResultDTO();
        dto.setId(kpiB4DetailResult.getId());
        dto.setInstanceId(kpiB4DetailResult.getInstanceId());
        dto.setInstanceModuleId(kpiB4DetailResult.getInstanceModuleId());
        dto.setAnagStationId(kpiB4DetailResult.getAnagStationId());
        dto.setKpiB4ResultId(kpiB4DetailResult.getKpiB4Result().getId());
        dto.setAnalysisDate(kpiB4DetailResult.getAnalysisDate());
        dto.setEvaluationType(kpiB4DetailResult.getEvaluationType());
        dto.setEvaluationStartDate(kpiB4DetailResult.getEvaluationStartDate());
        dto.setEvaluationEndDate(kpiB4DetailResult.getEvaluationEndDate());
        dto.setSumTotGpd(kpiB4DetailResult.getSumTotGpd());
        dto.setSumTotCp(kpiB4DetailResult.getSumTotCp());
        dto.setPerApiCp(kpiB4DetailResult.getPerApiCp());
        dto.setOutcome(kpiB4DetailResult.getOutcome());
        return dto;
    }
}