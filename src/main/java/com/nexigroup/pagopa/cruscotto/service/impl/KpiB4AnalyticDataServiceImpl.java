package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.Instance;
import com.nexigroup.pagopa.cruscotto.domain.KpiB4AnalyticData;
import com.nexigroup.pagopa.cruscotto.repository.InstanceRepository;
import com.nexigroup.pagopa.cruscotto.repository.InstanceModuleRepository;
import com.nexigroup.pagopa.cruscotto.repository.KpiB4AnalyticDataRepository;
import com.nexigroup.pagopa.cruscotto.repository.KpiB4DetailResultRepository;
import com.nexigroup.pagopa.cruscotto.repository.AnagStationRepository;
import com.nexigroup.pagopa.cruscotto.service.KpiB4AnalyticDataService;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB4AnalyticDataDTO;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link KpiB4AnalyticData}.
 */
@Service
@Transactional
public class KpiB4AnalyticDataServiceImpl implements KpiB4AnalyticDataService {

    private static final Logger LOGGER = LoggerFactory.getLogger(KpiB4AnalyticDataServiceImpl.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final InstanceRepository instanceRepository;
    private final InstanceModuleRepository instanceModuleRepository;
    private final KpiB4AnalyticDataRepository kpiB4AnalyticDataRepository;
    private final KpiB4DetailResultRepository kpiB4DetailResultRepository;
    private final AnagStationRepository anagStationRepository;

    public KpiB4AnalyticDataServiceImpl(
        InstanceRepository instanceRepository,
        InstanceModuleRepository instanceModuleRepository,
        KpiB4AnalyticDataRepository kpiB4AnalyticDataRepository,
        KpiB4DetailResultRepository kpiB4DetailResultRepository,
        AnagStationRepository anagStationRepository
    ) {
        this.instanceRepository = instanceRepository;
        this.instanceModuleRepository = instanceModuleRepository;
        this.kpiB4AnalyticDataRepository = kpiB4AnalyticDataRepository;
        this.kpiB4DetailResultRepository = kpiB4DetailResultRepository;
        this.anagStationRepository = anagStationRepository;
    }

    @Override
    public KpiB4AnalyticDataDTO save(KpiB4AnalyticDataDTO kpiB4AnalyticDataDTO) {
        LOGGER.debug("Request to save KpiB4AnalyticData: {}", kpiB4AnalyticDataDTO);
        // TODO: Implement save logic if needed
        return kpiB4AnalyticDataDTO;
    }

    @Override
    public int deleteAllByInstanceModule(long instanceModuleId) {
        LOGGER.debug("Request to delete all KpiB4AnalyticData by instanceModuleId: {}", instanceModuleId);
        // TODO: Implement delete logic if needed
        return 0;
    }

    @Override
    @Transactional(readOnly = true)
    public List<KpiB4AnalyticDataDTO> findByDetailResultId(long detailResultId) {
        LOGGER.debug("Request to get KpiB4AnalyticData by detailResultId: {}", detailResultId);
        return kpiB4AnalyticDataRepository
            .findAllByDetailResultIdOrderByEvaluationDateDesc(detailResultId)
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    /**
     * Converts KpiB4AnalyticData entity to DTO.
     */
    private KpiB4AnalyticDataDTO convertToDTO(KpiB4AnalyticData kpiB4AnalyticData) {
        KpiB4AnalyticDataDTO dto = new KpiB4AnalyticDataDTO();
        dto.setId(kpiB4AnalyticData.getId());
        dto.setInstanceId(kpiB4AnalyticData.getInstanceId());
        dto.setAnalysisDate(kpiB4AnalyticData.getAnalysisDate());
        dto.setEvaluationDate(kpiB4AnalyticData.getEvaluationDate());
        dto.setNumRequestGpd(kpiB4AnalyticData.getNumRequestGpd());
        dto.setNumRequestCp(kpiB4AnalyticData.getNumRequestCp());
        dto.setKpiB4DetailResultId(kpiB4AnalyticData.getKpiB4DetailResult().getId());
        
        // Additional fields from instance for API output
        Instance instance = kpiB4AnalyticData.getInstance();
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