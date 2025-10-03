package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.KpiB3AnalyticData;
import com.nexigroup.pagopa.cruscotto.domain.PagopaNumeroStandin;
import com.nexigroup.pagopa.cruscotto.repository.KpiB3AnalyticDataRepository;
import com.nexigroup.pagopa.cruscotto.repository.PagopaNumeroStandinRepository;
import com.nexigroup.pagopa.cruscotto.service.PagopaNumeroStandinService;
import com.nexigroup.pagopa.cruscotto.service.dto.PagopaNumeroStandinDTO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Service Implementation for managing {@link PagopaNumeroStandin}.
 * 
 * Provides operations for retrieving raw Stand-In data used in KPI B.3 drilldown analysis.
 */
@Service
@Transactional
public class PagopaNumeroStandinServiceImpl implements PagopaNumeroStandinService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PagopaNumeroStandinServiceImpl.class);

    private final KpiB3AnalyticDataRepository kpiB3AnalyticDataRepository;
    private final PagopaNumeroStandinRepository pagopaNumeroStandinRepository;

    public PagopaNumeroStandinServiceImpl(
        KpiB3AnalyticDataRepository kpiB3AnalyticDataRepository,
        PagopaNumeroStandinRepository pagopaNumeroStandinRepository
    ) {
        this.kpiB3AnalyticDataRepository = kpiB3AnalyticDataRepository;
        this.pagopaNumeroStandinRepository = pagopaNumeroStandinRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PagopaNumeroStandinDTO> findByAnalyticDataId(Long analyticDataId) {
        LOGGER.debug("Request to get PagopaNumeroStandin data for KpiB3AnalyticData ID: {}", analyticDataId);

        try {
            // Find the analytic data record
            Optional<KpiB3AnalyticData> analyticDataOpt = kpiB3AnalyticDataRepository.findById(analyticDataId);
            
            if (analyticDataOpt.isEmpty()) {
                LOGGER.warn("KpiB3AnalyticData with ID {} not found", analyticDataId);
                return new ArrayList<>();
            }

            KpiB3AnalyticData analyticData = analyticDataOpt.get();
            String eventId = analyticData.getEventId();

            if (eventId == null) {
                LOGGER.warn("No eventId found in KpiB3AnalyticData with ID {}", analyticDataId);
                return new ArrayList<>();
            }

            // Convert eventId back to Long and find the corresponding PagopaNumeroStandin record
            try {
                Long pagopaId = Long.valueOf(eventId);
                Optional<PagopaNumeroStandin> pagopaDataOpt = pagopaNumeroStandinRepository.findById(pagopaId);

                if (pagopaDataOpt.isPresent()) {
                    List<PagopaNumeroStandinDTO> result = List.of(convertToDTO(pagopaDataOpt.get()));
                    LOGGER.debug("Found {} PagopaNumeroStandin records for analytic data ID {}", result.size(), analyticDataId);
                    return result;
                } else {
                    LOGGER.warn("PagopaNumeroStandin with ID {} not found (referenced by KpiB3AnalyticData {})", pagopaId, analyticDataId);
                    return new ArrayList<>();
                }

            } catch (NumberFormatException e) {
                LOGGER.error("Invalid eventId format '{}' in KpiB3AnalyticData {}: {}", eventId, analyticDataId, e.getMessage());
                return new ArrayList<>();
            }

        } catch (Exception e) {
            LOGGER.error("Error retrieving PagopaNumeroStandin data for analytic data ID {}: {}", analyticDataId, e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    @Override
    public PagopaNumeroStandinDTO convertToDTO(PagopaNumeroStandin entity) {
        if (entity == null) {
            return null;
        }

        PagopaNumeroStandinDTO dto = new PagopaNumeroStandinDTO();
        dto.setId(entity.getId());
        dto.setStationCode(entity.getStationCode());
        dto.setIntervalStart(entity.getIntervalStart());
        dto.setIntervalEnd(entity.getIntervalEnd());
        dto.setStandInCount(entity.getStandInCount());
        dto.setEventType(entity.getEventType());
        dto.setDataDate(entity.getDataDate());
        dto.setLoadTimestamp(entity.getLoadTimestamp());

        return dto;
    }
}