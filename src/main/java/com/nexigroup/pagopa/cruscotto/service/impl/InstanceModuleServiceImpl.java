package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.AnagStation;
import com.nexigroup.pagopa.cruscotto.domain.Instance;
import com.nexigroup.pagopa.cruscotto.domain.InstanceModule;
import com.nexigroup.pagopa.cruscotto.domain.KpiB2DetailResult;
import com.nexigroup.pagopa.cruscotto.domain.QInstanceModule;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.AnalysisOutcome;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import com.nexigroup.pagopa.cruscotto.repository.InstanceModuleRepository;
import com.nexigroup.pagopa.cruscotto.service.InstanceModuleService;
import com.nexigroup.pagopa.cruscotto.service.dto.InstanceModuleDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB2DetailResultDTO;
import com.nexigroup.pagopa.cruscotto.service.qdsl.QueryBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPQLQuery;
import java.time.Instant;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link InstanceModule}.
 */
@Service
@Transactional
public class InstanceModuleServiceImpl implements InstanceModuleService {

    private static final Logger LOGGER = LoggerFactory.getLogger(InstanceModuleServiceImpl.class);

    private final InstanceModuleRepository instanceModuleRepository;

    private final QueryBuilder queryBuilder;

    public InstanceModuleServiceImpl(InstanceModuleRepository instanceModuleRepository, QueryBuilder queryBuilder) {
        this.instanceModuleRepository = instanceModuleRepository;
        this.queryBuilder = queryBuilder;
    }

    @Override
    public Optional<InstanceModuleDTO> findOne(Long instanceId, Long moduleId) {
        JPQLQuery<InstanceModule> jpql = queryBuilder
            .<InstanceModule>createQuery()
            .from(QInstanceModule.instanceModule)
            .where(QInstanceModule.instanceModule.instance.id.eq(instanceId).and(QInstanceModule.instanceModule.module.id.eq(moduleId)));

        return Optional.ofNullable(
            jpql
                .select(
                    Projections.fields(
                        InstanceModuleDTO.class,
                        QInstanceModule.instanceModule.id.as("id"),
                        QInstanceModule.instanceModule.instance.id.as("instanceId"),
                        QInstanceModule.instanceModule.module.id.as("moduleId"),
                        QInstanceModule.instanceModule.module.code.as("moduleCode"),
                        QInstanceModule.instanceModule.analysisType.as("analysisType"),
                        QInstanceModule.instanceModule.allowManualOutcome.as("allowManualOutcome"),
                        QInstanceModule.instanceModule.automaticOutcomeDate.as("automaticOutcomeDate"),
                        QInstanceModule.instanceModule.automaticOutcome.as("automaticOutcome"),
                        QInstanceModule.instanceModule.manualOutcome.as("manualOutcome"),
                        QInstanceModule.instanceModule.status.as("status"),
                        QInstanceModule.instanceModule.manualOutcomeUser.id.as("assignedUserId"),
                        QInstanceModule.instanceModule.manualOutcomeDate.as("manualOutcomeDate")
                    )
                )
                .fetchOne()
        );
    }

    @Override
    public void updateAutomaticOutcome(Long instanceModuleId, OutcomeStatus automaticOutcome) {
        InstanceModule instanceModule = instanceModuleRepository
            .findById(instanceModuleId)
            .orElseThrow(() -> new IllegalArgumentException("InstanceModule not found"));
        AnalysisOutcome analysisOutcome;

        switch (automaticOutcome) {
            case OK:
                analysisOutcome = AnalysisOutcome.OK;
                break;
            case KO:
                analysisOutcome = AnalysisOutcome.KO;
                break;
            case STANDBY:
                analysisOutcome = AnalysisOutcome.STANDBY;
                break;
            default:
                throw new IllegalArgumentException("Invalid automatic outcome");
        }

        instanceModule.setAutomaticOutcome(analysisOutcome);
        instanceModule.setAutomaticOutcomeDate(Instant.now());

        instanceModuleRepository.save(instanceModule);
    }

    private static @NotNull KpiB2DetailResult getKpiB2DetailResult(
        KpiB2DetailResultDTO kpiB2DetailResultDTO,
        Instance instance,
        InstanceModule instanceModule,
        AnagStation station
    ) {
        KpiB2DetailResult kpiB2DetailResult = new KpiB2DetailResult();
        kpiB2DetailResult.setInstance(instance);
        kpiB2DetailResult.setInstanceModule(instanceModule);
        kpiB2DetailResult.setAnalysisDate(kpiB2DetailResultDTO.getAnalysisDate());
        kpiB2DetailResult.setStation(station);
        kpiB2DetailResult.setMethod(kpiB2DetailResultDTO.getMethod());
        kpiB2DetailResult.setEvaluationType(kpiB2DetailResultDTO.getEvaluationType());
        kpiB2DetailResult.setEvaluationStartDate(kpiB2DetailResultDTO.getEvaluationStartDate());
        kpiB2DetailResult.setEvaluationEndDate(kpiB2DetailResultDTO.getEvaluationEndDate());
        kpiB2DetailResult.setTotReq(kpiB2DetailResultDTO.getTotReq());
        kpiB2DetailResult.setAvgTime(kpiB2DetailResultDTO.getAvgTime());
        kpiB2DetailResult.setOverTimeLimit(kpiB2DetailResultDTO.getOverTimeLimit());
        kpiB2DetailResult.setOutcome(kpiB2DetailResultDTO.getOutcome());

        return kpiB2DetailResult;
    }
}
