package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.Instance;
import com.nexigroup.pagopa.cruscotto.domain.InstanceModule;
import com.nexigroup.pagopa.cruscotto.domain.KpiB3AnalyticData;
import com.nexigroup.pagopa.cruscotto.domain.KpiB3DetailResult;
import com.nexigroup.pagopa.cruscotto.repository.InstanceModuleRepository;
import com.nexigroup.pagopa.cruscotto.repository.InstanceRepository;
import com.nexigroup.pagopa.cruscotto.repository.KpiB3AnalyticDataRepository;
import com.nexigroup.pagopa.cruscotto.repository.KpiB3DetailResultRepository;
import com.nexigroup.pagopa.cruscotto.service.KpiB3AnalyticDataService;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB3AnalyticDataDTO;
import com.nexigroup.pagopa.cruscotto.domain.AnagStation;
import com.nexigroup.pagopa.cruscotto.repository.AnagStationRepository;

import java.util.List;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link KpiB3AnalyticData}.
 */
@Service
@Transactional
public class KpiB3AnalyticDataServiceImpl implements KpiB3AnalyticDataService {

    private static final Logger LOGGER = LoggerFactory.getLogger(KpiB3AnalyticDataServiceImpl.class);

    private final InstanceRepository instanceRepository;

    private final InstanceModuleRepository instanceModuleRepository;

    private final KpiB3AnalyticDataRepository kpiB3AnalyticDataRepository;

    private final KpiB3DetailResultRepository kpiB3DetailResultRepository;

    private final AnagStationRepository anagStationRepository;

    public KpiB3AnalyticDataServiceImpl(
        InstanceRepository instanceRepository,
        InstanceModuleRepository instanceModuleRepository,
        KpiB3AnalyticDataRepository kpiB3AnalyticDataRepository,
        KpiB3DetailResultRepository kpiB3DetailResultRepository,
        AnagStationRepository anagStationRepository
    ) {
        this.instanceRepository = instanceRepository;
        this.instanceModuleRepository = instanceModuleRepository;
        this.kpiB3AnalyticDataRepository = kpiB3AnalyticDataRepository;
        this.kpiB3DetailResultRepository = kpiB3DetailResultRepository;
        this.anagStationRepository = anagStationRepository;
    }

    /**
     * Save kpiB3AnalyticData.
     *
     * @param kpiB3AnalyticDataDTO the entity to save.
     * @return saved KpiB3AnalyticDataDTO
     */
    @Override
    public KpiB3AnalyticDataDTO save(KpiB3AnalyticDataDTO kpiB3AnalyticDataDTO) {
        Instance instance = instanceRepository
            .findById(kpiB3AnalyticDataDTO.getInstanceId())
            .orElseThrow(() -> new IllegalArgumentException("Instance not found"));

        InstanceModule instanceModule = instanceModuleRepository
            .findById(kpiB3AnalyticDataDTO.getInstanceModuleId())
            .orElseThrow(() -> new IllegalArgumentException("InstanceModule not found"));

        KpiB3DetailResult kpiB3DetailResult = kpiB3DetailResultRepository
            .findById(kpiB3AnalyticDataDTO.getKpiB3DetailResultId())
            .orElseThrow(() -> new IllegalArgumentException("KpiB3DetailResult not found"));

        KpiB3AnalyticData kpiB3AnalyticData = getKpiB3AnalyticData(kpiB3AnalyticDataDTO, instance, instanceModule, kpiB3DetailResult);

        KpiB3AnalyticData saved = kpiB3AnalyticDataRepository.save(kpiB3AnalyticData);

        kpiB3AnalyticDataDTO.setId(saved.getId());

        return kpiB3AnalyticDataDTO;
    }

    private @NotNull KpiB3AnalyticData getKpiB3AnalyticData(
        KpiB3AnalyticDataDTO kpiB3AnalyticDataDTO,
        Instance instance,
        InstanceModule instanceModule,
        KpiB3DetailResult kpiB3DetailResult
    ) {
        AnagStation anagStation = anagStationRepository
            .findById(kpiB3AnalyticDataDTO.getAnagStationId())
            .orElseThrow(() -> new IllegalArgumentException("AnagStation not found"));

        KpiB3AnalyticData kpiB3AnalyticData = new KpiB3AnalyticData();
        kpiB3AnalyticData.setInstance(instance);
        kpiB3AnalyticData.setInstanceModule(instanceModule);
        kpiB3AnalyticData.setAnagStation(anagStation);
        kpiB3AnalyticData.setKpiB3DetailResult(kpiB3DetailResult);
        kpiB3AnalyticData.setEventId(kpiB3AnalyticDataDTO.getEventId());
        kpiB3AnalyticData.setEventType(kpiB3AnalyticDataDTO.getEventType());
        kpiB3AnalyticData.setEventTimestamp(kpiB3AnalyticDataDTO.getEventTimestamp());
        kpiB3AnalyticData.setStandInCount(kpiB3AnalyticDataDTO.getStandInCount());

        return kpiB3AnalyticData;
    }

    @Override
    public void saveAll(List<KpiB3AnalyticDataDTO> kpiB3AnalyticDataDTOList) {
        LOGGER.debug("Request to save {} KpiB3AnalyticDataDTOs", kpiB3AnalyticDataDTOList.size());

        List<KpiB3AnalyticData> kpiB3AnalyticDataList = kpiB3AnalyticDataDTOList
            .stream()
            .map(dto -> {
                Instance instance = instanceRepository
                    .findById(dto.getInstanceId())
                    .orElseThrow(() -> new IllegalArgumentException("Instance not found"));

                InstanceModule instanceModule = instanceModuleRepository
                    .findById(dto.getInstanceModuleId())
                    .orElseThrow(() -> new IllegalArgumentException("InstanceModule not found"));

                KpiB3DetailResult kpiB3DetailResult = kpiB3DetailResultRepository
                    .findById(dto.getKpiB3DetailResultId())
                    .orElseThrow(() -> new IllegalArgumentException("KpiB3DetailResult not found"));

                return this.getKpiB3AnalyticData(dto, instance, instanceModule, kpiB3DetailResult);
            })
            .collect(Collectors.toList());

        kpiB3AnalyticDataRepository.saveAll(kpiB3AnalyticDataList);
    }

    @Override
    public int deleteAllByInstanceModule(long instanceModuleId) {
        return kpiB3AnalyticDataRepository.deleteAllByInstanceModuleId(instanceModuleId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<KpiB3AnalyticDataDTO> findByDetailResultId(long detailResultId) {
        LOGGER.debug("Request to get KpiB3AnalyticData by detailResultId: {}", detailResultId);
        return kpiB3AnalyticDataRepository
            .findAllByDetailResultIdOrderByEventTimestampDesc(detailResultId)
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    private KpiB3AnalyticDataDTO convertToDTO(KpiB3AnalyticData kpiB3AnalyticData) {
        KpiB3AnalyticDataDTO dto = new KpiB3AnalyticDataDTO();
        dto.setId(kpiB3AnalyticData.getId());
        dto.setInstanceId(kpiB3AnalyticData.getInstance().getId());
        dto.setInstanceModuleId(kpiB3AnalyticData.getInstanceModule().getId());
        dto.setAnagStationId(kpiB3AnalyticData.getAnagStation().getId());
        dto.setKpiB3DetailResultId(kpiB3AnalyticData.getKpiB3DetailResult().getId());
        dto.setEventId(kpiB3AnalyticData.getEventId());
        dto.setEventType(kpiB3AnalyticData.getEventType());
        dto.setEventTimestamp(kpiB3AnalyticData.getEventTimestamp());
        dto.setStandInCount(kpiB3AnalyticData.getStandInCount());
        return dto;
    }
}