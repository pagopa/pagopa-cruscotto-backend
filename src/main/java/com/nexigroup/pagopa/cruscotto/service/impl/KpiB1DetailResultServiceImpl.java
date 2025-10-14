package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.*;
import com.nexigroup.pagopa.cruscotto.repository.*;
import com.nexigroup.pagopa.cruscotto.service.KpiB1DetailResultService;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB1DetailResultDTO;
import com.nexigroup.pagopa.cruscotto.service.qdsl.QueryBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPQLQuery;
import java.math.BigDecimal;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link KpiB1DetailResult}.
 */
@Service
@Transactional
public class KpiB1DetailResultServiceImpl implements KpiB1DetailResultService {

    private final InstanceRepository instanceRepository;

    private final InstanceModuleRepository instanceModuleRepository;

    private final KpiB1DetailResultRepository kpiB1DetailResultRepository;

    private final KpiB1ResultRepository kpiB1ResultRepository;

    private final QueryBuilder queryBuilder;

    public KpiB1DetailResultServiceImpl(
        InstanceRepository instanceRepository,
        InstanceModuleRepository instanceModuleRepository,
        KpiB1DetailResultRepository kpiB1DetailResultRepository,
        KpiB1ResultRepository kpiB1ResultRepository,
        QueryBuilder queryBuilder
    ) {
        this.instanceRepository = instanceRepository;
        this.instanceModuleRepository = instanceModuleRepository;
        this.kpiB1DetailResultRepository = kpiB1DetailResultRepository;
        this.kpiB1ResultRepository = kpiB1ResultRepository;
        this.queryBuilder = queryBuilder;
    }

    /**
     * Save kpiB1DetailResult.
     *
     * @param kpiB1DetailResultDTO the entity to save.
     */
    @Override
    public KpiB1DetailResultDTO save(KpiB1DetailResultDTO kpiB1DetailResultDTO) {
        Instance instance = instanceRepository
            .findById(kpiB1DetailResultDTO.getInstanceId())
            .orElseThrow(() -> new IllegalArgumentException("Instance not found"));

        InstanceModule instanceModule = instanceModuleRepository
            .findById(kpiB1DetailResultDTO.getInstanceModuleId())
            .orElseThrow(() -> new IllegalArgumentException("InstanceModule not found"));

        KpiB1Result kpiB1Result = kpiB1ResultRepository
            .findById(kpiB1DetailResultDTO.getKpiB1ResultId())
            .orElseThrow(() -> new IllegalArgumentException("KpiB1Result not found"));

        KpiB1DetailResult kpiB1DetailResult = getKpiB1DetailResult(
            kpiB1DetailResultDTO,
            instance,
            instanceModule,
            kpiB1Result
        );

        kpiB1DetailResult = kpiB1DetailResultRepository.save(kpiB1DetailResult);

        kpiB1DetailResultDTO.setId(kpiB1DetailResult.getId());

        return kpiB1DetailResultDTO;
    }

    private static @NotNull KpiB1DetailResult getKpiB1DetailResult(
        KpiB1DetailResultDTO kpiB1DetailResultDTO,
        Instance instance,
        InstanceModule instanceModule,
        KpiB1Result kpiB1Result
    ) {
        KpiB1DetailResult kpiB1DetailResult = new KpiB1DetailResult();
        kpiB1DetailResult.setInstance(instance);
        kpiB1DetailResult.setInstanceModule(instanceModule);
        kpiB1DetailResult.setAnalysisDate(kpiB1DetailResultDTO.getAnalysisDate());
        kpiB1DetailResult.setEvaluationType(kpiB1DetailResultDTO.getEvaluationType());
        kpiB1DetailResult.setEvaluationStartDate(kpiB1DetailResultDTO.getEvaluationStartDate());
        kpiB1DetailResult.setEvaluationEndDate(kpiB1DetailResultDTO.getEvaluationEndDate());
        
        // Map DTO fields to domain entity fields
        // DTO uses totalInstitutions -> domain uses entityCount
        if (kpiB1DetailResultDTO.getTotalInstitutions() != null) {
            kpiB1DetailResult.setEntityCount(kpiB1DetailResultDTO.getTotalInstitutions());
        }
        
        // DTO uses totalTransactions -> domain uses transactionCount
        if (kpiB1DetailResultDTO.getTotalTransactions() != null) {
            kpiB1DetailResult.setTransactionCount(kpiB1DetailResultDTO.getTotalTransactions().longValue());
        }
        
        // Set outcome based on institutionOutcome (transactions are handled separately in job logic)
        kpiB1DetailResult.setOutcome(kpiB1DetailResultDTO.getInstitutionOutcome());
        
        // Set threshold flags (these would be calculated in the job)
        kpiB1DetailResult.setMeetsEntityThreshold(false); // Default, calculated in job
        kpiB1DetailResult.setMeetsTransactionThreshold(false); // Default, calculated in job
        
        // Set partner code (if available from DTO or can be derived)
        kpiB1DetailResult.setPartnerCode("DEFAULT"); // This should be set properly in the job
        
        kpiB1DetailResult.setKpiB1Result(kpiB1Result);

        return kpiB1DetailResult;
    }

    private static @NotNull KpiB1DetailResultDTO getKpiB1DetailResultDTO(KpiB1DetailResult kpiB1DetailResult) {
        KpiB1DetailResultDTO kpiB1DetailResultDTO = new KpiB1DetailResultDTO();
        kpiB1DetailResultDTO.setId(kpiB1DetailResult.getId());
        kpiB1DetailResultDTO.setInstanceId(kpiB1DetailResult.getInstance().getId());
        kpiB1DetailResultDTO.setInstanceModuleId(kpiB1DetailResult.getInstanceModule().getId());
        kpiB1DetailResultDTO.setAnalysisDate(kpiB1DetailResult.getAnalysisDate());
        kpiB1DetailResultDTO.setEvaluationType(kpiB1DetailResult.getEvaluationType());
        kpiB1DetailResultDTO.setEvaluationStartDate(kpiB1DetailResult.getEvaluationStartDate());
        kpiB1DetailResultDTO.setEvaluationEndDate(kpiB1DetailResult.getEvaluationEndDate());
        
        // Map domain entity fields to DTO fields
        // Domain entityCount -> DTO totalInstitutions
        if (kpiB1DetailResult.getEntityCount() != null) {
            kpiB1DetailResultDTO.setTotalInstitutions(kpiB1DetailResult.getEntityCount());
        }
        
        // Domain transactionCount -> DTO totalTransactions
        if (kpiB1DetailResult.getTransactionCount() != null) {
            kpiB1DetailResultDTO.setTotalTransactions(kpiB1DetailResult.getTransactionCount().intValue());
        }
        
        // Set both institution and transaction outcomes to the same value for now
        // (This should be properly separated in the job logic)
        kpiB1DetailResultDTO.setInstitutionOutcome(kpiB1DetailResult.getOutcome());
        kpiB1DetailResultDTO.setTransactionOutcome(kpiB1DetailResult.getOutcome());
        
        // Set difference fields (these should be calculated properly in the job)
        kpiB1DetailResultDTO.setInstitutionDifference(0);
        kpiB1DetailResultDTO.setTransactionDifference(0);
        kpiB1DetailResultDTO.setInstitutionDifferencePercentage(BigDecimal.ZERO);
        kpiB1DetailResultDTO.setTransactionDifferencePercentage(BigDecimal.ZERO);
        
        kpiB1DetailResultDTO.setKpiB1ResultId(kpiB1DetailResult.getKpiB1Result().getId());

        return kpiB1DetailResultDTO;
    }

    @Override
    public int deleteAllByInstanceModule(long instanceModuleId) {
        return kpiB1DetailResultRepository.deleteAllByInstanceModuleId(instanceModuleId);
    }

    @Override
    public List<KpiB1DetailResultDTO> findByResultId(long resultId) {
        final QKpiB1DetailResult qKpiB1DetailResult = QKpiB1DetailResult.kpiB1DetailResult;

        JPQLQuery<KpiB1DetailResultDTO> query = queryBuilder
            .createQuery()
            .from(qKpiB1DetailResult)
            .where(qKpiB1DetailResult.kpiB1Result.id.eq(resultId))
            .orderBy(qKpiB1DetailResult.evaluationType.asc(),
                     qKpiB1DetailResult.evaluationStartDate.asc())
            .select(
                Projections.fields(
                    KpiB1DetailResultDTO.class,
                    qKpiB1DetailResult.id.as("id"),
                    qKpiB1DetailResult.instance.id.as("instanceId"),
                    qKpiB1DetailResult.instanceModule.id.as("instanceModuleId"),
                    qKpiB1DetailResult.analysisDate.as("analysisDate"),
                    qKpiB1DetailResult.evaluationType.as("evaluationType"),
                    qKpiB1DetailResult.evaluationStartDate.as("evaluationStartDate"),
                    qKpiB1DetailResult.evaluationEndDate.as("evaluationEndDate"),
                    qKpiB1DetailResult.entityCount.as("totalInstitutions"),
                    qKpiB1DetailResult.transactionCount.as("totalTransactions"),
                    qKpiB1DetailResult.outcome.as("institutionOutcome"),
                    qKpiB1DetailResult.outcome.as("transactionOutcome"),
                    qKpiB1DetailResult.kpiB1Result.id.as("kpiB1ResultId")
                )
            );

        return query.fetch();
    }
}