package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.*;
import com.nexigroup.pagopa.cruscotto.repository.*;
import com.nexigroup.pagopa.cruscotto.service.KpiB1AnalyticDataService;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB1AnalyticDataDTO;
import com.nexigroup.pagopa.cruscotto.service.qdsl.QueryBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPQLQuery;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link KpiB1AnalyticData}.
 */
@Service
@Transactional
public class KpiB1AnalyticDataServiceImpl implements KpiB1AnalyticDataService {

    private final InstanceRepository instanceRepository;

    private final InstanceModuleRepository instanceModuleRepository;

    private final KpiB1AnalyticDataRepository kpiB1AnalyticDataRepository;

    private final KpiB1DetailResultRepository kpiB1DetailResultRepository;

    private final QueryBuilder queryBuilder;

    public KpiB1AnalyticDataServiceImpl(
        InstanceRepository instanceRepository,
        InstanceModuleRepository instanceModuleRepository,
        KpiB1AnalyticDataRepository kpiB1AnalyticDataRepository,
        KpiB1DetailResultRepository kpiB1DetailResultRepository,
        QueryBuilder queryBuilder
    ) {
        this.instanceRepository = instanceRepository;
        this.instanceModuleRepository = instanceModuleRepository;
        this.kpiB1AnalyticDataRepository = kpiB1AnalyticDataRepository;
        this.kpiB1DetailResultRepository = kpiB1DetailResultRepository;
        this.queryBuilder = queryBuilder;
    }

    /**
     * Save kpiB1AnalyticData.
     *
     * @param kpiB1AnalyticDataDTO the entity to save.
     */
    @Override
    public KpiB1AnalyticDataDTO save(KpiB1AnalyticDataDTO kpiB1AnalyticDataDTO) {
        Instance instance = instanceRepository
            .findById(kpiB1AnalyticDataDTO.getInstanceId())
            .orElseThrow(() -> new IllegalArgumentException("Instance not found"));

        InstanceModule instanceModule = instanceModuleRepository
            .findById(kpiB1AnalyticDataDTO.getInstanceModuleId())
            .orElseThrow(() -> new IllegalArgumentException("InstanceModule not found"));

        KpiB1DetailResult kpiB1DetailResult = kpiB1DetailResultRepository
            .findById(kpiB1AnalyticDataDTO.getKpiB1DetailResultId())
            .orElseThrow(() -> new IllegalArgumentException("KpiB1DetailResult not found"));

        KpiB1AnalyticData kpiB1AnalyticData = getKpiB1AnalyticData(
            kpiB1AnalyticDataDTO,
            instance,
            instanceModule,
            kpiB1DetailResult
        );

        kpiB1AnalyticData = kpiB1AnalyticDataRepository.save(kpiB1AnalyticData);

        kpiB1AnalyticDataDTO.setId(kpiB1AnalyticData.getId());

        return kpiB1AnalyticDataDTO;
    }

    /**
     * Save all kpiB1AnalyticData.
     *
     * @param kpiB1AnalyticDataDTOS the entities to save.
     */
    @Override
    public void saveAll(List<KpiB1AnalyticDataDTO> kpiB1AnalyticDataDTOS) {
        for (KpiB1AnalyticDataDTO kpiB1AnalyticDataDTO : kpiB1AnalyticDataDTOS) {
            Instance instance = instanceRepository
                .findById(kpiB1AnalyticDataDTO.getInstanceId())
                .orElseThrow(() -> new IllegalArgumentException("Instance not found"));

            InstanceModule instanceModule = instanceModuleRepository
                .findById(kpiB1AnalyticDataDTO.getInstanceModuleId())
                .orElseThrow(() -> new IllegalArgumentException("InstanceModule not found"));

            KpiB1DetailResult kpiB1DetailResult = kpiB1DetailResultRepository
                .findById(kpiB1AnalyticDataDTO.getKpiB1DetailResultId())
                .orElseThrow(() -> new IllegalArgumentException("KpiB1DetailResult not found"));

            KpiB1AnalyticData kpiB1AnalyticData = getKpiB1AnalyticData(
                kpiB1AnalyticDataDTO,
                instance,
                instanceModule,
                kpiB1DetailResult
            );

            kpiB1AnalyticDataRepository.save(kpiB1AnalyticData);
        }
    }

    private static @NotNull KpiB1AnalyticData getKpiB1AnalyticData(
        KpiB1AnalyticDataDTO kpiB1AnalyticDataDTO,
        Instance instance,
        InstanceModule instanceModule,
        KpiB1DetailResult kpiB1DetailResult
    ) {
        KpiB1AnalyticData kpiB1AnalyticData = new KpiB1AnalyticData();
        kpiB1AnalyticData.setInstance(instance);
        kpiB1AnalyticData.setInstanceModule(instanceModule);
        kpiB1AnalyticData.setAnalysisDate(kpiB1AnalyticDataDTO.getAnalysisDate());
        kpiB1AnalyticData.setDataDate(kpiB1AnalyticDataDTO.getDataDate());
        
        // Map DTO fields to domain entity fields (field names now match)
        if (kpiB1AnalyticDataDTO.getInstitutionCount() != null) {
            kpiB1AnalyticData.setInstitutionCount(kpiB1AnalyticDataDTO.getInstitutionCount());
        }
        
        if (kpiB1AnalyticDataDTO.getTransactionCount() != null) {
            kpiB1AnalyticData.setTransactionCount(kpiB1AnalyticDataDTO.getTransactionCount());
        }
        
        kpiB1AnalyticData.setKpiB1DetailResult(kpiB1DetailResult);

        return kpiB1AnalyticData;
    }

    @Override
    public int deleteAllByInstanceModule(long instanceModuleId) {
        return kpiB1AnalyticDataRepository.deleteAllByInstanceModuleId(instanceModuleId);
    }

    @Override
    public List<KpiB1AnalyticDataDTO> findByDetailResultId(long detailResultId) {
        final QKpiB1AnalyticData qKpiB1AnalyticData = QKpiB1AnalyticData.kpiB1AnalyticData;

        JPQLQuery<KpiB1AnalyticDataDTO> query = queryBuilder
            .createQuery()
            .from(qKpiB1AnalyticData)
            .where(qKpiB1AnalyticData.kpiB1DetailResult.id.eq(detailResultId))
            .orderBy(qKpiB1AnalyticData.dataDate.asc(),
                     qKpiB1AnalyticData.id.asc())
            .select(
                Projections.fields(
                    KpiB1AnalyticDataDTO.class,
                    qKpiB1AnalyticData.id.as("id"),
                    qKpiB1AnalyticData.instance.id.as("instanceId"),
                    qKpiB1AnalyticData.instanceModule.id.as("instanceModuleId"),
                    qKpiB1AnalyticData.analysisDate.as("analysisDate"),
                    qKpiB1AnalyticData.dataDate.as("dataDate"),
                    qKpiB1AnalyticData.institutionCount.as("institutionCount"),
                    qKpiB1AnalyticData.transactionCount.as("transactionCount"),
                    qKpiB1AnalyticData.kpiB1DetailResult.id.as("kpiB1DetailResultId")
                )
            );

        return query.fetch();
    }

    /**
     * Find all KpiB1AnalyticDataDTO by instanceModuleId.
     * @param instanceModuleId the instance module id
     * @return list of KpiB1AnalyticDataDTO
     */
    @Override
    public List<KpiB1AnalyticDataDTO> findByInstanceModuleId(Long instanceModuleId) {
        final QKpiB1AnalyticData qKpiB1AnalyticData = QKpiB1AnalyticData.kpiB1AnalyticData;

        JPQLQuery<KpiB1AnalyticDataDTO> query = queryBuilder
            .createQuery()
            .from(qKpiB1AnalyticData)
            .where(qKpiB1AnalyticData.instanceModule.id.eq(instanceModuleId))
            .orderBy(qKpiB1AnalyticData.dataDate.asc(),
                     qKpiB1AnalyticData.id.asc())
            .select(
                Projections.fields(
                    KpiB1AnalyticDataDTO.class,
                    qKpiB1AnalyticData.id.as("id"),
                    qKpiB1AnalyticData.instance.id.as("instanceId"),
                    qKpiB1AnalyticData.instanceModule.id.as("instanceModuleId"),
                    qKpiB1AnalyticData.analysisDate.as("analysisDate"),
                    qKpiB1AnalyticData.dataDate.as("dataDate"),
                    qKpiB1AnalyticData.institutionCount.as("institutionCount"),
                    qKpiB1AnalyticData.transactionCount.as("transactionCount"),
                    qKpiB1AnalyticData.kpiB1DetailResult.id.as("kpiB1DetailResultId")
                )
            );

        return query.fetch();
    }
}