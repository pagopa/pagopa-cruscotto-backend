package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.*;
import com.nexigroup.pagopa.cruscotto.repository.InstanceModuleRepository;
import com.nexigroup.pagopa.cruscotto.repository.InstanceRepository;
import com.nexigroup.pagopa.cruscotto.repository.KpiA2AnalyticDataRepository;
import com.nexigroup.pagopa.cruscotto.repository.KpiA2DetailResultRepository;
import com.nexigroup.pagopa.cruscotto.service.KpiA2AnalyticDataService;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiA1AnalyticDataDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiA2AnalyticDataDTO;
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
 * Service Implementation for managing {@link KpiA2AnalyticData}.
 */

@Service
@Transactional
public class KpiA2AnalyticDataServiceImpl implements KpiA2AnalyticDataService {

    private static final Logger LOGGER = LoggerFactory.getLogger(KpiA2AnalyticDataServiceImpl.class);

    private final InstanceRepository instanceRepository;

    private final InstanceModuleRepository instanceModuleRepository;

    private final KpiA2AnalyticDataRepository kpiA2AnalyticDataRepository;

    private final KpiA2DetailResultRepository kpiA2DetailResultRepository;

    private final QueryBuilder queryBuilder;

    public KpiA2AnalyticDataServiceImpl(
        InstanceRepository instanceRepository,
        InstanceModuleRepository instanceModuleRepository,
        KpiA2AnalyticDataRepository kpiA2AnalyticDataRepository,
        KpiA2DetailResultRepository kpiA2DetailResultRepository,
        QueryBuilder queryBuilder
    ) {
        this.instanceRepository = instanceRepository;
        this.instanceModuleRepository = instanceModuleRepository;
        this.kpiA2AnalyticDataRepository = kpiA2AnalyticDataRepository;
        this.kpiA2DetailResultRepository = kpiA2DetailResultRepository;
        this.queryBuilder = queryBuilder;
    }

    /**
     * Save kpiA2AnalyticData and return saved entity as DTO.
     *
     * @param kpiA2AnalyticDataDTO the entity to save.
     * @return saved KpiA2AnalyticDataDTO
     */
    @Override
    public KpiA2AnalyticDataDTO save(KpiA2AnalyticDataDTO kpiA2AnalyticDataDTO) {

        Instance instance = instanceRepository
                .findById(kpiA2AnalyticDataDTO.getInstanceId())
                .orElseThrow(() -> new IllegalArgumentException("Instance not found"));

        InstanceModule instanceModule = instanceModuleRepository
                .findById(kpiA2AnalyticDataDTO.getInstanceModuleId())
                .orElseThrow(() -> new IllegalArgumentException("InstanceModule not found"));

        KpiA2DetailResult kpiA2DetailResult = kpiA2DetailResultRepository
                .findById(kpiA2AnalyticDataDTO.getKpiA2DetailResultId())
                .orElseThrow(() -> new IllegalArgumentException("KpiA2DetailResult not found"));

        KpiA2AnalyticData kpiA2AnalyticData = getkpiA2AnalyticData(kpiA2AnalyticDataDTO, instance, instanceModule,
                kpiA2DetailResult);

        KpiA2AnalyticData saved = kpiA2AnalyticDataRepository.save(kpiA2AnalyticData);

        
        kpiA2AnalyticDataDTO.setId(saved.getId());

        return kpiA2AnalyticDataDTO;
    }

    private static @NotNull KpiA2AnalyticData getkpiA2AnalyticData(
        KpiA2AnalyticDataDTO kpiA2AnalyticDataDTO,
        Instance instance,
        InstanceModule instanceModule,
        KpiA2DetailResult kpiA2DetailResult
    ) {
        KpiA2AnalyticData kpiA2AnalyticData = new KpiA2AnalyticData();
        kpiA2AnalyticData.setInstance(instance);
        kpiA2AnalyticData.setInstanceModule(instanceModule);
        kpiA2AnalyticData.setAnalysisDate(kpiA2AnalyticDataDTO.getAnalysisDate());
        kpiA2AnalyticData.setEvaluationDate(kpiA2AnalyticDataDTO.getEvaluationDate());
        kpiA2AnalyticData.setTotPayments(kpiA2AnalyticDataDTO.getTotPayments());
        kpiA2AnalyticData.setTotIncorrectPayments(kpiA2AnalyticDataDTO.getTotIncorrectPayments());
        kpiA2AnalyticData.setKpiA2DetailResult(kpiA2DetailResult);

        return kpiA2AnalyticData;
    }

    @Override
    public int deleteAllByInstanceModule(long instanceModuleId) {
        return kpiA2AnalyticDataRepository.deleteAllByInstanceModuleId(instanceModuleId);
    }

    @Override
    public List<KpiA2AnalyticDataDTO> findByDetailResultId(long detailResultId) {
        final QKpiA2AnalyticData qKpiA2AnalyticData = QKpiA2AnalyticData.kpiA2AnalyticData;

        JPQLQuery<KpiA2AnalyticDataDTO> query = queryBuilder
            .createQuery()
            .from(qKpiA2AnalyticData)
            .where(qKpiA2AnalyticData.kpiA2DetailResult.id.eq(detailResultId))
            .select(
                Projections.fields(
                    KpiA2AnalyticDataDTO.class,
                    qKpiA2AnalyticData.id.as("id"),
                    qKpiA2AnalyticData.instance.id.as("instanceId"),
                    qKpiA2AnalyticData.instanceModule.id.as("instanceModuleId"),
                    qKpiA2AnalyticData.analysisDate.as("analysisDate"),
                    qKpiA2AnalyticData.evaluationDate.as("evaluationDate"),
                    qKpiA2AnalyticData.totPayments.as("totPayments"),
                    qKpiA2AnalyticData.totIncorrectPayments.as("totIncorrectPayments"),
                    qKpiA2AnalyticData.kpiA2DetailResult.id.as("kpiA1DetailResultId")
                )
            );

        return query.fetch();
    }
}
