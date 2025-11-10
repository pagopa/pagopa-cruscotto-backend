package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.KpiC2DetailResult;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import com.nexigroup.pagopa.cruscotto.repository.KpiC2DetailResultRepository;
import com.nexigroup.pagopa.cruscotto.service.KpiC2DetailResultService;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiC2DetailResultDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing KpiC2DetailResult.
 */
@Service
@Transactional
public class KpiC2DetailResultServiceImpl implements KpiC2DetailResultService {

    private static final Logger LOGGER = LoggerFactory.getLogger(KpiC2DetailResultServiceImpl.class);

    private final KpiC2DetailResultRepository kpiC2DetailResultRepository;

    public KpiC2DetailResultServiceImpl(KpiC2DetailResultRepository kpiC2DetailResultRepository) {
        this.kpiC2DetailResultRepository = kpiC2DetailResultRepository;
    }

    @Override
    public KpiC2DetailResultDTO save(KpiC2DetailResultDTO kpiC2DetailResultDTO) {
        // TODO: Implement save logic
        LOGGER.debug("Request to save KpiC2DetailResult: {}", kpiC2DetailResultDTO);
        return kpiC2DetailResultDTO;
    }

    @Override
    public int deleteAllByInstanceModule(long instanceModuleId) {
        // TODO: Implement delete logic
        LOGGER.debug("Request to delete all KpiC2DetailResult by instanceModuleId: {}", instanceModuleId);
        return 0;
    }

    @Override
    public void updateKpiC2DetailResultOutcome(long id, OutcomeStatus outcomeStatus) {
        // TODO: Implement update outcome logic
        LOGGER.debug("Request to update KpiC2DetailResult outcome: {} with status: {}", id, outcomeStatus);
    }

    @Override
    @Transactional(readOnly = true)
    public List<KpiC2DetailResultDTO> findByResultId(long resultId) {
        LOGGER.debug("Request to get KpiC2DetailResults by resultId: {}", resultId);
        return kpiC2DetailResultRepository
            .findAllByResultIdOrderByAnalysisDateDesc(resultId)
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    private KpiC2DetailResultDTO convertToDTO(KpiC2DetailResult kpiC2DetailResult) {
        KpiC2DetailResultDTO dto = new KpiC2DetailResultDTO();
        dto.setId(kpiC2DetailResult.getId());
        dto.setInstanceId(kpiC2DetailResult.getInstanceId());
        dto.setInstanceModuleId(kpiC2DetailResult.getInstanceModuleId());

        dto.setKpiC2ResultId(kpiC2DetailResult.getKpiC2Result().getId());
        dto.setAnalysisDate(kpiC2DetailResult.getAnalysisDate());
        dto.setEvaluationType(kpiC2DetailResult.getEvaluationType());
        dto.setEvaluationStartDate(kpiC2DetailResult.getEvaluationStartDate());
        dto.setEvaluationEndDate(kpiC2DetailResult.getEvaluationEndDate());
        dto.setTotalInstitution(kpiC2DetailResult.getTotalInstitution());
        dto.setTotalInstitutionSend(kpiC2DetailResult.getTotalInstitutionSend());
        dto.setPercentInstitutionSend(kpiC2DetailResult.getPercentInstitutionSend());

        dto.setTotalPayment(kpiC2DetailResult.getTotalPayment());
        dto.setTotalNotification(kpiC2DetailResult.getTotalNotification());
        dto.setPercentEntiOk(kpiC2DetailResult.getPercentEntiOk());
        dto.setOutcome(kpiC2DetailResult.getOutcome());
        return dto;
    }
}
