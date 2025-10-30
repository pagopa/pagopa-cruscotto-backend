package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.Instance;
import com.nexigroup.pagopa.cruscotto.domain.KpiC2AnalyticData;
import com.nexigroup.pagopa.cruscotto.repository.*;
import com.nexigroup.pagopa.cruscotto.service.KpiC2AnalyticDataService;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiC2AnalyticDataDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing {@link KpiC2AnalyticData}.
 */
@Service
@Transactional
public class KpiC2AnalyticDataServiceImpl implements KpiC2AnalyticDataService {

    private static final Logger LOGGER = LoggerFactory.getLogger(KpiC2AnalyticDataServiceImpl.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final InstanceRepository instanceRepository;
    private final InstanceModuleRepository instanceModuleRepository;
    private final KpiC2AnalyticDataRepository kpiC2AnalyticDataRepository;
    private final KpiC2DetailResultRepository kpiC2DetailResultRepository;
    private final AnagStationRepository anagStationRepository;

    public KpiC2AnalyticDataServiceImpl(
        InstanceRepository instanceRepository,
        InstanceModuleRepository instanceModuleRepository,
        KpiC2AnalyticDataRepository kpiC2AnalyticDataRepository,
        KpiC2DetailResultRepository kpiC2DetailResultRepository,
        AnagStationRepository anagStationRepository
    ) {
        this.instanceRepository = instanceRepository;
        this.instanceModuleRepository = instanceModuleRepository;
        this.kpiC2AnalyticDataRepository = kpiC2AnalyticDataRepository;
        this.kpiC2DetailResultRepository = kpiC2DetailResultRepository;
        this.anagStationRepository = anagStationRepository;
    }

    @Override
    public KpiC2AnalyticDataDTO save(KpiC2AnalyticDataDTO kpiC2AnalyticDataDTO) {
        LOGGER.debug("Request to save KpiC2AnalyticData: {}", kpiC2AnalyticDataDTO);
        // TODO: Implement save logic if needed
        return kpiC2AnalyticDataDTO;
    }

    @Override
    public int deleteAllByInstanceModule(long instanceModuleId) {
        LOGGER.debug("Request to delete all KpiC2AnalyticData by instanceModuleId: {}", instanceModuleId);
        // TODO: Implement delete logic if needed
        return 0;
    }

    @Override
    @Transactional(readOnly = true)
    public List<KpiC2AnalyticDataDTO> findByDetailResultId(long detailResultId) {
        LOGGER.debug("Request to get KpiC2AnalyticData by detailResultId: {}", detailResultId);
        return kpiC2AnalyticDataRepository
            .findAllByDetailResultIdOrderByEvaluationDateDesc(detailResultId)
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    /**
     * Converts KpiC2AnalyticData entity to DTO.
     */
    private KpiC2AnalyticDataDTO convertToDTO(KpiC2AnalyticData kpiC2AnalyticData) {
        KpiC2AnalyticDataDTO dto = new KpiC2AnalyticDataDTO();
        dto.setId(kpiC2AnalyticData.getId());
        dto.setInstanceId(kpiC2AnalyticData.getInstanceId());
        dto.setAnalysisDate(kpiC2AnalyticData.getAnalysisDate());
        dto.setEvaluationDate(kpiC2AnalyticData.getEvaluationDate());
        dto.setTotReq(kpiC2AnalyticData.getTotReq());
        dto.setReqKO(kpiC2AnalyticData.getReqKO());
        dto.setKpiC2DetailResultId(kpiC2AnalyticData.getKpiC2DetailResult().getId());

        // Additional fields from instance for API output
        Instance instance = kpiC2AnalyticData.getInstance();
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
