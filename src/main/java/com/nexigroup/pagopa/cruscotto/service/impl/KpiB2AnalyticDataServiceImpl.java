package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.*;
import com.nexigroup.pagopa.cruscotto.repository.*;
import com.nexigroup.pagopa.cruscotto.service.KpiB2AnalyticDataService;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB2AnalyticDataDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB2DetailResultDTO;
import com.nexigroup.pagopa.cruscotto.service.qdsl.QueryBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPQLQuery;
import java.util.List;
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
     * Save kpiB2AnalyticData.
     *
     * @param kpiB2AnalyticDataDTO the entity to save.
     */
    @Override
    public KpiB2AnalyticDataDTO save(KpiB2AnalyticDataDTO kpiB2AnalyticDataDTO) {
        Instance instance = instanceRepository
            .findById(kpiB2AnalyticDataDTO.getInstanceId())
            .orElseThrow(() -> new IllegalArgumentException("Instance not found"));

        InstanceModule instanceModule = instanceModuleRepository
            .findById(kpiB2AnalyticDataDTO.getInstanceModuleId())
            .orElseThrow(() -> new IllegalArgumentException("InstanceModule not found"));

        AnagStation station = anagStationRepository
            .findById(kpiB2AnalyticDataDTO.getStationId())
            .orElseThrow(() -> new IllegalArgumentException("Station not found"));

        KpiB2DetailResult kpiB2DResult = kpiB2DetailResultRepository
            .findById(kpiB2AnalyticDataDTO.getKpiB2DetailResultId())
            .orElseThrow(() -> new IllegalArgumentException("KpiB2DetailResult not found"));

        KpiB2AnalyticData kpiB2AnalyticData = getkpiB2AnalyticData(kpiB2AnalyticDataDTO, instance, instanceModule, station, kpiB2DResult);

        kpiB2AnalyticData = kpiB2AnalyticDataRepository.save(kpiB2AnalyticData);

        kpiB2AnalyticDataDTO.setId(kpiB2AnalyticData.getId());

        return kpiB2AnalyticDataDTO;
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

    @Override
    public int deleteAllByInstanceModule(long instanceModuleId) {
        return kpiB2AnalyticDataRepository.deleteAllByInstanceModuleId(instanceModuleId);
    }

    @Override
    public List<KpiB2AnalyticDataDTO> findByDetailResultId(long detailResultId) {
        final QKpiB2AnalyticData qKpiB2AnalyticData = QKpiB2AnalyticData.kpiB2AnalyticData;
        final QAnagStation qAnagStation = QAnagStation.anagStation;

        JPQLQuery<KpiB2AnalyticDataDTO> query = queryBuilder
            .createQuery()
            .from(qKpiB2AnalyticData)
            .leftJoin(qKpiB2AnalyticData.station, qAnagStation)
            .where(qKpiB2AnalyticData.kpiB2DetailResult.id.eq(detailResultId)) // Filtro per detailResultId
            .select(
                Projections.fields(
                    KpiB2AnalyticDataDTO.class,
                    qKpiB2AnalyticData.id.as("id"),
                    qKpiB2AnalyticData.instance.id.as("instanceId"),
                    qKpiB2AnalyticData.instanceModule.id.as("instanceModuleId"),
                    qKpiB2AnalyticData.analysisDate.as("analysisDate"),
                    qKpiB2AnalyticData.station.id.as("stationId"),
                    qKpiB2AnalyticData.method.as("method"),
                    qKpiB2AnalyticData.evaluationDate.as("evaluationDate"),
                    qKpiB2AnalyticData.totReq.as("totReq"),
                    qKpiB2AnalyticData.reqOk.as("reqOk"),
                    qKpiB2AnalyticData.reqTimeout.as("reqTimeout"),
                    qKpiB2AnalyticData.avgTime.as("avgTime"),
                    qKpiB2AnalyticData.kpiB2DetailResult.id.as("kpiB2DetailResultId"),
                    qAnagStation.name.as("stationName")
                )
            );
        return query.fetch();
    }
}
