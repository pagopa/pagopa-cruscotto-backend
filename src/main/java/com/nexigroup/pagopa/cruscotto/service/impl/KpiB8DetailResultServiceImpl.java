package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.KpiB8DetailResult;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import com.nexigroup.pagopa.cruscotto.repository.KpiB8DetailResultRepository;
import com.nexigroup.pagopa.cruscotto.service.KpiB8DetailResultService;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB8DetailResultDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing KpiB8DetailResult.
 */
@Service
@Transactional
public class KpiB8DetailResultServiceImpl implements KpiB8DetailResultService {

    private static final Logger LOGGER = LoggerFactory.getLogger(KpiB8DetailResultServiceImpl.class);

    private final KpiB8DetailResultRepository kpiB8DetailResultRepository;

    public KpiB8DetailResultServiceImpl(KpiB8DetailResultRepository kpiB8DetailResultRepository) {
        this.kpiB8DetailResultRepository = kpiB8DetailResultRepository;
    }

    @Override
    public KpiB8DetailResultDTO save(KpiB8DetailResultDTO kpiB8DetailResultDTO) {
        // TODO: Implement save logic
        LOGGER.debug("Request to save KpiB8DetailResult: {}", kpiB8DetailResultDTO);
        return kpiB8DetailResultDTO;
    }

    @Override
    public int deleteAllByInstanceModule(long instanceModuleId) {
        // TODO: Implement delete logic
        LOGGER.debug("Request to delete all KpiB8DetailResult by instanceModuleId: {}", instanceModuleId);
        return 0;
    }

    @Override
    public void updateKpiB8DetailResultOutcome(long id, OutcomeStatus outcomeStatus) {
        // TODO: Implement update outcome logic
        LOGGER.debug("Request to update KpiB8DetailResult outcome: {} with status: {}", id, outcomeStatus);
    }

    @Override
    @Transactional(readOnly = true)
    public List<KpiB8DetailResultDTO> findByResultId(long resultId) {
        LOGGER.debug("Request to get KpiB8DetailResults by resultId: {}", resultId);
        return kpiB8DetailResultRepository
            .findAllByResultIdOrderByAnalysisDateDesc(resultId)
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    private KpiB8DetailResultDTO convertToDTO(KpiB8DetailResult kpiB8DetailResult) {
        KpiB8DetailResultDTO dto = new KpiB8DetailResultDTO();
        dto.setId(kpiB8DetailResult.getId());
        dto.setInstanceId(kpiB8DetailResult.getInstanceId());
        dto.setInstanceModuleId(kpiB8DetailResult.getInstanceModuleId());
        dto.setKpiB8ResultId(kpiB8DetailResult.getKpiB8Result().getId());
        dto.setAnalysisDate(kpiB8DetailResult.getAnalysisDate());
        dto.setEvaluationType(kpiB8DetailResult.getEvaluationType());
        dto.setEvaluationStartDate(kpiB8DetailResult.getEvaluationStartDate());
        dto.setEvaluationEndDate(kpiB8DetailResult.getEvaluationEndDate());
        dto.setReqKO(kpiB8DetailResult.getReqKO());
        dto.setTotReq(kpiB8DetailResult.getTotReq());
        dto.setPerKO(kpiB8DetailResult.getPerKO());
        dto.setOutcome(kpiB8DetailResult.getOutcome());
        return dto;
    }
}
