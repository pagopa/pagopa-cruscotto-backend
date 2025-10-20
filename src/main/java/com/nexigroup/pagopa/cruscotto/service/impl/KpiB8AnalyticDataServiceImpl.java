package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.Instance;
import com.nexigroup.pagopa.cruscotto.domain.KpiB4AnalyticData;
import com.nexigroup.pagopa.cruscotto.domain.KpiB8AnalyticData;
import com.nexigroup.pagopa.cruscotto.repository.*;
import com.nexigroup.pagopa.cruscotto.service.KpiB4AnalyticDataService;
import com.nexigroup.pagopa.cruscotto.service.KpiB8AnalyticDataService;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB4AnalyticDataDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB8AnalyticDataDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing {@link KpiB4AnalyticData}.
 */
@Service
@Transactional
public class KpiB8AnalyticDataServiceImpl implements KpiB8AnalyticDataService {

    private static final Logger LOGGER = LoggerFactory.getLogger(KpiB8AnalyticDataServiceImpl.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final InstanceRepository instanceRepository;
    private final InstanceModuleRepository instanceModuleRepository;
    private final KpiB8AnalyticDataRepository kpiB8AnalyticDataRepository;
    private final KpiB8DetailResultRepository kpiB8DetailResultRepository;
    private final AnagStationRepository anagStationRepository;

    public KpiB8AnalyticDataServiceImpl(
        InstanceRepository instanceRepository,
        InstanceModuleRepository instanceModuleRepository,
        KpiB8AnalyticDataRepository kpiB8AnalyticDataRepository,
        KpiB8DetailResultRepository kpiB8DetailResultRepository,
        AnagStationRepository anagStationRepository
    ) {
        this.instanceRepository = instanceRepository;
        this.instanceModuleRepository = instanceModuleRepository;
        this.kpiB8AnalyticDataRepository = kpiB8AnalyticDataRepository;
        this.kpiB8DetailResultRepository = kpiB8DetailResultRepository;
        this.anagStationRepository = anagStationRepository;
    }

    @Override
    public KpiB8AnalyticDataDTO save(KpiB8AnalyticDataDTO kpiB8AnalyticDataDTO) {
        LOGGER.debug("Request to save KpiB8AnalyticData: {}", kpiB8AnalyticDataDTO);
        // TODO: Implement save logic if needed
        return kpiB8AnalyticDataDTO;
    }

    @Override
    public int deleteAllByInstanceModule(long instanceModuleId) {
        LOGGER.debug("Request to delete all KpiB8AnalyticData by instanceModuleId: {}", instanceModuleId);
        // TODO: Implement delete logic if needed
        return 0;
    }

    @Override
    @Transactional(readOnly = true)
    public List<KpiB8AnalyticDataDTO> findByDetailResultId(long detailResultId) {
        LOGGER.debug("Request to get KpiB8AnalyticData by detailResultId: {}", detailResultId);
        return kpiB8AnalyticDataRepository
            .findAllByDetailResultIdOrderByEvaluationDateDesc(detailResultId)
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    /**
     * Converts KpiB8AnalyticData entity to DTO.
     */
    private KpiB8AnalyticDataDTO convertToDTO(KpiB8AnalyticData kpiB8AnalyticData) {
        KpiB8AnalyticDataDTO dto = new KpiB8AnalyticDataDTO();
        dto.setId(kpiB8AnalyticData.getId());
        dto.setInstanceId(kpiB8AnalyticData.getInstanceId());
        dto.setAnalysisDate(kpiB8AnalyticData.getAnalysisDate());
        dto.setEvaluationDate(kpiB8AnalyticData.getEvaluationDate());
        dto.setTotReq(kpiB8AnalyticData.getTotReq());
        dto.setReqKO(kpiB8AnalyticData.getReqKO());
        dto.setKpiB8DetailResultId(kpiB8AnalyticData.getKpiB8DetailResult().getId());

        // Additional fields from instance for API output
        Instance instance = kpiB8AnalyticData.getInstance();
        if (instance != null) {
            // Format analysis period as "dd/MM/yyyy - dd/MM/yyyy"
            if (instance.getAnalysisPeriodStartDate() != null && instance.getAnalysisPeriodEndDate() != null) {
                String analysisDatePeriod = instance.getAnalysisPeriodStartDate().format(DATE_FORMATTER) +
                                          " - " +
                                          instance.getAnalysisPeriodEndDate().format(DATE_FORMATTER);
                dto.setAnalysisDatePeriod(analysisDatePeriod);
            }
        }

        return dto;
    }
}
