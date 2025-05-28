package com.nexigroup.pagopa.cruscotto.service.impl;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nexigroup.pagopa.cruscotto.domain.AnagStation;
import com.nexigroup.pagopa.cruscotto.domain.Instance;
import com.nexigroup.pagopa.cruscotto.domain.InstanceModule;
import com.nexigroup.pagopa.cruscotto.domain.KpiB9AnalyticData;
import com.nexigroup.pagopa.cruscotto.domain.KpiB9DetailResult;
import com.nexigroup.pagopa.cruscotto.repository.AnagStationRepository;
import com.nexigroup.pagopa.cruscotto.repository.InstanceModuleRepository;
import com.nexigroup.pagopa.cruscotto.repository.InstanceRepository;
import com.nexigroup.pagopa.cruscotto.repository.KpiB9AnalyticDataRepository;
import com.nexigroup.pagopa.cruscotto.repository.KpiB9DetailResultRepository;
import com.nexigroup.pagopa.cruscotto.service.KpiB9AnalyticDataService;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB9AnalyticDataDTO;
import com.nexigroup.pagopa.cruscotto.service.qdsl.QueryBuilder;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing {@link KpiB9AnalyticData}.
 */
@Service
@Transactional
public class KpiB9AnalyticDataServiceImpl implements KpiB9AnalyticDataService {

    private static final Logger LOGGER = LoggerFactory.getLogger(KpiB9AnalyticDataServiceImpl.class);
    

    private final AnagStationRepository anagStationRepository;

    private final InstanceRepository instanceRepository;

    private final InstanceModuleRepository instanceModuleRepository;

    private final KpiB9AnalyticDataRepository kpiB9AnalyticDataRepository;

    private final KpiB9DetailResultRepository kpiB9DetailResultRepository;

    private final QueryBuilder queryBuilder;
    

    public KpiB9AnalyticDataServiceImpl(AnagStationRepository anagStationRepository, InstanceRepository instanceRepository,
    									InstanceModuleRepository instanceModuleRepository, KpiB9AnalyticDataRepository kpiB9AnalyticDataRepository,
    									KpiB9DetailResultRepository kpiB9DetailResultRepository, QueryBuilder queryBuilder) {
        this.anagStationRepository = anagStationRepository;
        this.instanceRepository = instanceRepository;
        this.instanceModuleRepository = instanceModuleRepository;
        this.kpiB9AnalyticDataRepository = kpiB9AnalyticDataRepository;
        this.kpiB9DetailResultRepository = kpiB9DetailResultRepository;
        this.queryBuilder = queryBuilder;
    }

    /**
     * Save all kpiB9AnalyticData.
     *
     * @param kpiB9AnalyticDataDTOS the entities to save.
     */
    @Override
    public void saveAll(List<KpiB9AnalyticDataDTO> kpiB9AnalyticDataDTOS) {
    	
        kpiB9AnalyticDataDTOS.forEach(kpiB9AnalyticDataDTO -> {
            Instance instance = instanceRepository.findById(kpiB9AnalyticDataDTO.getInstanceId())
            									  .orElseThrow(() -> new IllegalArgumentException("Instance not found"));

            InstanceModule instanceModule = instanceModuleRepository.findById(kpiB9AnalyticDataDTO.getInstanceModuleId())
            														.orElseThrow(() -> new IllegalArgumentException("InstanceModule not found"));

            AnagStation station = anagStationRepository.findById(kpiB9AnalyticDataDTO.getStationId())
            										   .orElseThrow(() -> new IllegalArgumentException("Station not found"));

            KpiB9DetailResult kpiB9DetailResult = kpiB9DetailResultRepository.findById(kpiB9AnalyticDataDTO.getKpiB9DetailResultId())
            																 .orElseThrow(() -> new IllegalArgumentException("KpiB9DetailResult not found"));

            KpiB9AnalyticData kpiB9AnalyticData = getkpiB9AnalyticData(kpiB9AnalyticDataDTO, instance, instanceModule, station, kpiB9DetailResult);

            kpiB9AnalyticDataRepository.save(kpiB9AnalyticData);
        });
    }

    private static @NotNull KpiB9AnalyticData getkpiB9AnalyticData(KpiB9AnalyticDataDTO kpiB9AnalyticDataDTO, Instance instance,
    															   InstanceModule instanceModule, AnagStation station, KpiB9DetailResult kpiB9DetailResult) {
    	
        KpiB9AnalyticData kpiB9AnalyticData = new KpiB9AnalyticData();
        kpiB9AnalyticData.setInstance(instance);
        kpiB9AnalyticData.setInstanceModule(instanceModule);
        kpiB9AnalyticData.setAnalysisDate(kpiB9AnalyticDataDTO.getAnalysisDate());
        kpiB9AnalyticData.setStation(station);
        kpiB9AnalyticData.setEvaluationDate(kpiB9AnalyticDataDTO.getEvaluationDate());
        kpiB9AnalyticData.setTotRes(kpiB9AnalyticDataDTO.getTotRes());
        kpiB9AnalyticData.setResOk(kpiB9AnalyticDataDTO.getResOk());
        kpiB9AnalyticData.setResKoReal(kpiB9AnalyticDataDTO.getResKoReal());
        kpiB9AnalyticData.setResKoValid(kpiB9AnalyticDataDTO.getResKoValid());
        kpiB9AnalyticData.setKpiB9DetailResult(kpiB9DetailResult);

        return kpiB9AnalyticData;
    }

    private static @NotNull KpiB9AnalyticDataDTO getkpiB9AnalyticDataDTO(KpiB9AnalyticData kpiB9AnalyticData) {
    	
        KpiB9AnalyticDataDTO kpiB9AnalyticDataDTO = new KpiB9AnalyticDataDTO();
        kpiB9AnalyticDataDTO.setId(kpiB9AnalyticData.getId());
        kpiB9AnalyticDataDTO.setInstanceId(kpiB9AnalyticData.getInstance() != null ? kpiB9AnalyticData.getInstance().getId() : null);
        kpiB9AnalyticDataDTO.setInstanceModuleId(kpiB9AnalyticData.getInstanceModule() != null ? kpiB9AnalyticData.getInstanceModule().getId() : null);
        kpiB9AnalyticDataDTO.setAnalysisDate(kpiB9AnalyticData.getAnalysisDate());
        kpiB9AnalyticDataDTO.setStationId(kpiB9AnalyticData.getStation() != null ? kpiB9AnalyticData.getStation().getId() : null);
        kpiB9AnalyticDataDTO.setEvaluationDate(kpiB9AnalyticData.getEvaluationDate());
        kpiB9AnalyticDataDTO.setTotRes(kpiB9AnalyticData.getTotRes());
        kpiB9AnalyticDataDTO.setResOk(kpiB9AnalyticData.getResOk());
        kpiB9AnalyticDataDTO.setResKoReal(kpiB9AnalyticData.getResKoReal());
        kpiB9AnalyticDataDTO.setResKoValid(kpiB9AnalyticData.getResKoValid());
        kpiB9AnalyticDataDTO.setKpiB9DetailResultId(kpiB9AnalyticData.getKpiB9DetailResult() != null ? kpiB9AnalyticData.getKpiB9DetailResult().getId() : null);
        return kpiB9AnalyticDataDTO;
    }

    @Override
    public int deleteAllByInstanceModule(long instanceModuleId) {
        return kpiB9AnalyticDataRepository.deleteAllByInstanceModuleId(instanceModuleId);
    }

    @Override
    public List<KpiB9AnalyticDataDTO> findByInstanceModuleId(long instanceModuleId) {
        return kpiB9AnalyticDataRepository.selectByInstanceModuleId(instanceModuleId)
        								  .stream()
        								  .map(KpiB9AnalyticDataServiceImpl::getkpiB9AnalyticDataDTO)
        								  .collect(Collectors.toList());
    }
}
