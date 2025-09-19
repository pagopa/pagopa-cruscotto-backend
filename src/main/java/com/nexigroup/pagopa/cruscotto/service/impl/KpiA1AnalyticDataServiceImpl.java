package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.*;
import com.nexigroup.pagopa.cruscotto.repository.*;
import com.nexigroup.pagopa.cruscotto.repository.AnagStationRepository;
import com.nexigroup.pagopa.cruscotto.repository.InstanceModuleRepository;
import com.nexigroup.pagopa.cruscotto.repository.InstanceRepository;
import com.nexigroup.pagopa.cruscotto.repository.KpiA1AnalyticDataRepository;
import com.nexigroup.pagopa.cruscotto.repository.KpiA1DetailResultRepository;
import com.nexigroup.pagopa.cruscotto.service.KpiA1AnalyticDataService;
import com.nexigroup.pagopa.cruscotto.service.KpiA1AnalyticDataService;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiA1AnalyticDataDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiA1AnalyticDataDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB2AnalyticDataDTO;
import com.nexigroup.pagopa.cruscotto.service.qdsl.QueryBuilder;
import com.nexigroup.pagopa.cruscotto.service.qdsl.QueryBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPQLQuery;
import java.util.List;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link KpiA1AnalyticData}.
 */
@Service
@Transactional
public class KpiA1AnalyticDataServiceImpl implements KpiA1AnalyticDataService {

    private static final Logger LOGGER = LoggerFactory.getLogger(KpiA1AnalyticDataServiceImpl.class);

    private final AnagStationRepository anagStationRepository;

    private final InstanceRepository instanceRepository;

    private final InstanceModuleRepository instanceModuleRepository;

    private final KpiA1AnalyticDataRepository kpiA1AnalyticDataRepository;

    private final KpiA1DetailResultRepository kpiA1DetailResultRepository;

    private final QueryBuilder queryBuilder;

    public KpiA1AnalyticDataServiceImpl(
        AnagStationRepository anagStationRepository,
        InstanceRepository instanceRepository,
        InstanceModuleRepository instanceModuleRepository,
        KpiA1AnalyticDataRepository kpiA1AnalyticDataRepository,
        KpiA1DetailResultRepository kpiA1DetailResultRepository,
        QueryBuilder queryBuilder
    ) {
        this.anagStationRepository = anagStationRepository;
        this.instanceRepository = instanceRepository;
        this.instanceModuleRepository = instanceModuleRepository;
        this.kpiA1AnalyticDataRepository = kpiA1AnalyticDataRepository;
        this.kpiA1DetailResultRepository = kpiA1DetailResultRepository;
        this.queryBuilder = queryBuilder;
    }

    /**
     * Save all kpiA1AnalyticData.
     *
     * @param kpiA1AnalyticDataDTOS the entities to save.
     */
    @Override
    public void saveAll(List<KpiA1AnalyticDataDTO> kpiA1AnalyticDataDTOS) {
        kpiA1AnalyticDataDTOS.forEach(kpiA1AnalyticDataDTO -> {
            Instance instance = instanceRepository
                .findById(kpiA1AnalyticDataDTO.getInstanceId())
                .orElseThrow(() -> new IllegalArgumentException("Instance not found"));

            InstanceModule instanceModule = instanceModuleRepository
                .findById(kpiA1AnalyticDataDTO.getInstanceModuleId())
                .orElseThrow(() -> new IllegalArgumentException("InstanceModule not found"));

            AnagStation station = anagStationRepository
                .findById(kpiA1AnalyticDataDTO.getStationId())
                .orElseThrow(() -> new IllegalArgumentException("Station not found"));

            KpiA1DetailResult kpiA1DetailResult = kpiA1DetailResultRepository
                .findById(kpiA1AnalyticDataDTO.getKpiA1DetailResultId())
                .orElseThrow(() -> new IllegalArgumentException("KpiA1DetailResult not found"));

            KpiA1AnalyticData kpiA1AnalyticData = getkpiA1AnalyticData(
                kpiA1AnalyticDataDTO,
                instance,
                instanceModule,
                station,
                kpiA1DetailResult
            );

            kpiA1AnalyticDataRepository.save(kpiA1AnalyticData);
        });
    }

    private static @NotNull KpiA1AnalyticData getkpiA1AnalyticData(
        KpiA1AnalyticDataDTO kpiA1AnalyticDataDTO,
        Instance instance,
        InstanceModule instanceModule,
        AnagStation station,
        KpiA1DetailResult kpiA1DetailResult
    ) {
        KpiA1AnalyticData kpiA1AnalyticData = new KpiA1AnalyticData();
        kpiA1AnalyticData.setInstance(instance);
        kpiA1AnalyticData.setInstanceModule(instanceModule);
        kpiA1AnalyticData.setAnalysisDate(kpiA1AnalyticDataDTO.getAnalysisDate());
        kpiA1AnalyticData.setStation(station);
        kpiA1AnalyticData.setMethod(kpiA1AnalyticDataDTO.getMethod());
        kpiA1AnalyticData.setEvaluationDate(kpiA1AnalyticDataDTO.getEvaluationDate());
        kpiA1AnalyticData.setTotReq(kpiA1AnalyticDataDTO.getTotReq());
        kpiA1AnalyticData.setReqOk(kpiA1AnalyticDataDTO.getReqOk());
        kpiA1AnalyticData.setReqTimeoutReal(kpiA1AnalyticDataDTO.getReqTimeoutReal());
        kpiA1AnalyticData.setReqTimeoutValid(kpiA1AnalyticDataDTO.getReqTimeoutValid());
        kpiA1AnalyticData.setKpiA1DetailResult(kpiA1DetailResult);

        return kpiA1AnalyticData;
    }

    private static @NotNull KpiA1AnalyticDataDTO getkpiA1AnalyticDataDTO(KpiA1AnalyticData kpiA1AnalyticData) {
        KpiA1AnalyticDataDTO kpiA1AnalyticDataDTO = new KpiA1AnalyticDataDTO();
        kpiA1AnalyticDataDTO.setId(kpiA1AnalyticData.getId());
        kpiA1AnalyticDataDTO.setInstanceId(kpiA1AnalyticData.getInstance() != null ? kpiA1AnalyticData.getInstance().getId() : null);
        kpiA1AnalyticDataDTO.setInstanceModuleId(
            kpiA1AnalyticData.getInstanceModule() != null ? kpiA1AnalyticData.getInstanceModule().getId() : null
        );
        kpiA1AnalyticDataDTO.setAnalysisDate(kpiA1AnalyticData.getAnalysisDate());
        kpiA1AnalyticDataDTO.setStationId(kpiA1AnalyticData.getStation() != null ? kpiA1AnalyticData.getStation().getId() : null);
        kpiA1AnalyticDataDTO.setMethod(kpiA1AnalyticData.getMethod());
        kpiA1AnalyticDataDTO.setEvaluationDate(kpiA1AnalyticData.getEvaluationDate());
        kpiA1AnalyticDataDTO.setTotReq(kpiA1AnalyticData.getTotReq());
        kpiA1AnalyticDataDTO.setReqOk(kpiA1AnalyticData.getReqOk());
        kpiA1AnalyticDataDTO.setReqTimeoutReal(kpiA1AnalyticData.getReqTimeoutReal());
        kpiA1AnalyticDataDTO.setReqTimeoutValid(kpiA1AnalyticData.getReqTimeoutValid());
        kpiA1AnalyticDataDTO.setKpiA1DetailResultId(
            kpiA1AnalyticData.getKpiA1DetailResult() != null ? kpiA1AnalyticData.getKpiA1DetailResult().getId() : null
        );
        return kpiA1AnalyticDataDTO;
    }

    @Override
    public int deleteAllByInstanceModule(long instanceModuleId) {
        return kpiA1AnalyticDataRepository.deleteAllByInstanceModuleId(instanceModuleId);
    }

    @Override
    public List<KpiA1AnalyticDataDTO> findByDetailResultId(long detailResultId) {
        final QKpiA1AnalyticData qKpiA1AnalyticData = QKpiA1AnalyticData.kpiA1AnalyticData;
        final QAnagStation qAnagStation = QAnagStation.anagStation;

        JPQLQuery<KpiA1AnalyticDataDTO> query = queryBuilder
            .createQuery()
            .from(qKpiA1AnalyticData)
            .leftJoin(qKpiA1AnalyticData.station, qAnagStation)
            .where(qKpiA1AnalyticData.kpiA1DetailResult.id.eq(detailResultId))
            .select(
                Projections.fields(
                    KpiA1AnalyticDataDTO.class,
                    qKpiA1AnalyticData.id.as("id"),
                    qKpiA1AnalyticData.instance.id.as("instanceId"),
                    qKpiA1AnalyticData.instanceModule.id.as("instanceModuleId"),
                    qKpiA1AnalyticData.analysisDate.as("analysisDate"),
                    qKpiA1AnalyticData.station.id.as("stationId"),
                    qKpiA1AnalyticData.method.as("method"),
                    qKpiA1AnalyticData.evaluationDate.as("evaluationDate"),
                    qKpiA1AnalyticData.totReq.as("totReq"),
                    qKpiA1AnalyticData.reqOk.as("reqOk"),
                    qKpiA1AnalyticData.reqTimeoutReal.as("reqTimeoutReal"),
                    qKpiA1AnalyticData.reqTimeoutValid.as("reqTimeoutValid"),
                    qKpiA1AnalyticData.kpiA1DetailResult.id.as("kpiA1DetailResultId"),
                    qAnagStation.name.as("stationName")
                )
            );

        return query.fetch();
    }

    /**
     * Save kpiA1AnalyticData.
     *
     * @param kpiA1AnalyticDataDTO the entity to save.
     */
    @Override
    public KpiA1AnalyticDataDTO save(KpiA1AnalyticDataDTO kpiA1AnalyticDataDTO) {
        Instance instance = instanceRepository
                .findById(kpiA1AnalyticDataDTO.getInstanceId())
                .orElseThrow(() -> new IllegalArgumentException("Instance not found"));

            InstanceModule instanceModule = instanceModuleRepository
                .findById(kpiA1AnalyticDataDTO.getInstanceModuleId())
                .orElseThrow(() -> new IllegalArgumentException("InstanceModule not found"));

            AnagStation station = anagStationRepository
                .findById(kpiA1AnalyticDataDTO.getStationId())
                .orElseThrow(() -> new IllegalArgumentException("Station not found"));

            KpiA1DetailResult kpiA1DetailResult = kpiA1DetailResultRepository
                .findById(kpiA1AnalyticDataDTO.getKpiA1DetailResultId())
                .orElseThrow(() -> new IllegalArgumentException("KpiA1DetailResult not found"));

            KpiA1AnalyticData kpiA1AnalyticData = getkpiA1AnalyticData(
                kpiA1AnalyticDataDTO,
                instance,
                instanceModule,
                station,
                kpiA1DetailResult
            );

            kpiA1AnalyticData = kpiA1AnalyticDataRepository.save(kpiA1AnalyticData);

        kpiA1AnalyticDataDTO.setId(kpiA1AnalyticData.getId());

        return kpiA1AnalyticDataDTO;
    }
}
