package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.AnagStation;
import com.nexigroup.pagopa.cruscotto.domain.Instance;
import com.nexigroup.pagopa.cruscotto.domain.InstanceModule;
import com.nexigroup.pagopa.cruscotto.domain.KpiB2AnalyticData;
import com.nexigroup.pagopa.cruscotto.domain.KpiB2DetailResult;
import com.nexigroup.pagopa.cruscotto.repository.AnagStationRepository;
import com.nexigroup.pagopa.cruscotto.repository.InstanceModuleRepository;
import com.nexigroup.pagopa.cruscotto.repository.InstanceRepository;
import com.nexigroup.pagopa.cruscotto.repository.KpiB2AnalyticDataRepository;
import com.nexigroup.pagopa.cruscotto.repository.KpiB2DetailResultRepository;
import com.nexigroup.pagopa.cruscotto.service.KpiB2AnalyticDataService;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB2AnalyticDataDTO;
import com.nexigroup.pagopa.cruscotto.service.qdsl.QueryBuilder;
import java.util.List;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link KpiB2AnalyticData}.
 */
@Service
@Transactional
public class KpiB2AnalyticDataServiceImpl implements KpiB2AnalyticDataService {

    private static final Logger LOGGER = LoggerFactory.getLogger(KpiB2AnalyticDataServiceImpl.class);

    private final AnagStationRepository anagStationRepository;

    private final InstanceRepository instanceRepository;

    private final InstanceModuleRepository instanceModuleRepository;

    private final KpiB2AnalyticDataRepository kpiB2AnalyticDataRepository;

    private final KpiB2DetailResultRepository kpiB2DetailResultRepository;

    private final QueryBuilder queryBuilder;

    public KpiB2AnalyticDataServiceImpl(
        AnagStationRepository anagStationRepository,
        InstanceRepository instanceRepository,
        InstanceModuleRepository instanceModuleRepository,
        KpiB2AnalyticDataRepository kpiB2AnalyticDataRepository,
        KpiB2DetailResultRepository kpiB2DetailResultRepository,
        QueryBuilder queryBuilder
    ) {
        this.anagStationRepository = anagStationRepository;
        this.instanceRepository = instanceRepository;
        this.instanceModuleRepository = instanceModuleRepository;
        this.kpiB2AnalyticDataRepository = kpiB2AnalyticDataRepository;
        this.kpiB2DetailResultRepository = kpiB2DetailResultRepository;
        this.queryBuilder = queryBuilder;
    }

    /**
     * Save all kpiB2AnalyticData.
     *
     * @param kpiB2AnalyticDataDTOS the entities to save.
     */
    @Override
    public void saveAll(List<KpiB2AnalyticDataDTO> kpiB2AnalyticDataDTOS) {
        kpiB2AnalyticDataDTOS.forEach(kpiB2AnalyticDataDTO -> {
            Instance instance = instanceRepository
                .findById(kpiB2AnalyticDataDTO.getInstanceId())
                .orElseThrow(() -> new IllegalArgumentException("Instance not found"));

            InstanceModule instanceModule = instanceModuleRepository
                .findById(kpiB2AnalyticDataDTO.getInstanceModuleId())
                .orElseThrow(() -> new IllegalArgumentException("InstanceModule not found"));

            AnagStation station = anagStationRepository
                .findById(kpiB2AnalyticDataDTO.getStationId())
                .orElseThrow(() -> new IllegalArgumentException("Station not found"));

            KpiB2DetailResult kpiB2DetailResult = kpiB2DetailResultRepository
                .findById(kpiB2AnalyticDataDTO.getKpiB2DetailResultId())
                .orElseThrow(() -> new IllegalArgumentException("KpiB2DetailResult not found"));

            KpiB2AnalyticData kpiB2AnalyticData = getkpiB2AnalyticData(
                kpiB2AnalyticDataDTO,
                instance,
                instanceModule,
                station,
                kpiB2DetailResult
            );

            kpiB2AnalyticDataRepository.save(kpiB2AnalyticData);
        });
    }

    private static @NotNull KpiB2AnalyticData getkpiB2AnalyticData(
        KpiB2AnalyticDataDTO kpiB2AnalyticDataDTO,
        Instance instance,
        InstanceModule instanceModule,
        AnagStation station,
        KpiB2DetailResult kpiB2DetailResult
    ) {
        KpiB2AnalyticData kpiB2AnalyticData = new KpiB2AnalyticData();
        kpiB2AnalyticData.setInstance(instance);
        kpiB2AnalyticData.setInstanceModule(instanceModule);
        kpiB2AnalyticData.setAnalysisDate(kpiB2AnalyticDataDTO.getAnalysisDate());
        kpiB2AnalyticData.setStation(station);
        kpiB2AnalyticData.setMethod(kpiB2AnalyticDataDTO.getMethod());
        kpiB2AnalyticData.setEvaluationDate(kpiB2AnalyticDataDTO.getEvaluationDate());
        kpiB2AnalyticData.setTotReq(kpiB2AnalyticDataDTO.getTotReq());
        kpiB2AnalyticData.setReqOk(kpiB2AnalyticDataDTO.getReqOk());
        kpiB2AnalyticData.setReqTimeout(kpiB2AnalyticDataDTO.getReqTimeout());
        kpiB2AnalyticData.setAvgTime(kpiB2AnalyticDataDTO.getAvgTime());
        kpiB2AnalyticData.setKpiB2DetailResult(kpiB2DetailResult);

        return kpiB2AnalyticData;
    }

    private static @NotNull KpiB2AnalyticDataDTO getkpiB2AnalyticDataDTO(KpiB2AnalyticData kpiB2AnalyticData) {
        KpiB2AnalyticDataDTO kpiB2AnalyticDataDTO = new KpiB2AnalyticDataDTO();
        kpiB2AnalyticDataDTO.setId(kpiB2AnalyticData.getId());
        kpiB2AnalyticDataDTO.setInstanceId(kpiB2AnalyticData.getInstance() != null ? kpiB2AnalyticData.getInstance().getId() : null);
        kpiB2AnalyticDataDTO.setInstanceModuleId(
            kpiB2AnalyticData.getInstanceModule() != null ? kpiB2AnalyticData.getInstanceModule().getId() : null
        );
        kpiB2AnalyticDataDTO.setAnalysisDate(kpiB2AnalyticData.getAnalysisDate());
        kpiB2AnalyticDataDTO.setStationId(kpiB2AnalyticData.getStation() != null ? kpiB2AnalyticData.getStation().getId() : null);
        kpiB2AnalyticDataDTO.setMethod(kpiB2AnalyticData.getMethod());
        kpiB2AnalyticDataDTO.setEvaluationDate(kpiB2AnalyticData.getEvaluationDate());
        kpiB2AnalyticDataDTO.setTotReq(kpiB2AnalyticData.getTotReq());
        kpiB2AnalyticDataDTO.setReqOk(kpiB2AnalyticData.getReqOk());
        kpiB2AnalyticDataDTO.setReqTimeout(kpiB2AnalyticData.getReqTimeout());
        kpiB2AnalyticDataDTO.setAvgTime(kpiB2AnalyticData.getAvgTime());
        kpiB2AnalyticDataDTO.setKpiB2DetailResultId(
            kpiB2AnalyticData.getKpiB2DetailResult() != null ? kpiB2AnalyticData.getKpiB2DetailResult().getId() : null
        );
        return kpiB2AnalyticDataDTO;
    }

    @Override
    public int deleteAllByInstanceModule(long instanceModuleId) {
        return kpiB2AnalyticDataRepository.deleteAllByInstanceModuleId(instanceModuleId);
    }

    @Override
    public List<KpiB2AnalyticDataDTO> findByDetailResultId(long detailResultId) {
        return kpiB2AnalyticDataRepository
            .selectByDetailResultId(detailResultId)
            .stream()
            .map(KpiB2AnalyticDataServiceImpl::getkpiB2AnalyticDataDTO)
            .collect(Collectors.toList());
    }
}
