package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.*;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.AnalysisOutcome;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.InstanceStatus;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import com.nexigroup.pagopa.cruscotto.repository.InstanceModuleRepository;
import com.nexigroup.pagopa.cruscotto.service.InstanceModuleService;
import com.nexigroup.pagopa.cruscotto.service.dto.InstanceDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.InstanceModuleDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB2DetailResultDTO;
import com.nexigroup.pagopa.cruscotto.service.qdsl.QueryBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.QBean;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPQLQuery;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
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

    private static final String ID_FIELD = "id";

    private static final String ANALYSIS_TYPE_FILED = "analysisType";

    private static final String INSTANCE_ID_FIELD = "instanceId";

    private static final String MODULE_ID_FIELD = "moduleId";

    private static final String MODULE_CODE_FIELD = "moduleCode";

    private static final String ALLOW_MANUAL_OUTCOME_FIELD = "allowManualOutcome";

    private static final String AUTOMATIC_OUTCOME_DATE_FIELD = "automaticOutcomeDate";

    private static final String AUTOMATIC_OUTCOME_FIELD = "automaticOutcome";

    private static final String MANUAL_OUTCOME_FIELD = "manualOutcome";

    private static final String MANUAL_OUTCOME_DATE_FIELD = "manualOutcomeDate";

    private static final String STATUS_FIELD = "status";

    private static final String ASSIGNED_USER_ID_FIELD = "assignedUserId";

    private final InstanceModuleRepository instanceModuleRepository;

    private final QueryBuilder queryBuilder;

    public InstanceModuleServiceImpl(InstanceModuleRepository instanceModuleRepository, QueryBuilder queryBuilder) {
        this.instanceModuleRepository = instanceModuleRepository;
        this.queryBuilder = queryBuilder;
    }

    @Override
    public Optional<InstanceModuleDTO> findOne(Long instanceId, Long moduleId) {
        QInstanceModule instanceModule = QInstanceModule.instanceModule;
        JPQLQuery<InstanceModule> jpql = queryBuilder
            .<InstanceModule>createQuery()
            .from(instanceModule)
            .where(instanceModule.instance.id.eq(instanceId).and(instanceModule.module.id.eq(moduleId)));

        return Optional.ofNullable(jpql.select(buildInstanceModuleProjection(instanceModule)).fetchOne());
    }

    @Override
    public void updateAutomaticOutcome(Long instanceModuleId, OutcomeStatus automaticOutcome) {
        InstanceModule instanceModule = instanceModuleRepository
            .findById(instanceModuleId)
            .orElseThrow(() -> new IllegalArgumentException("InstanceModule not found"));

        AnalysisOutcome analysisOutcome =
            switch (automaticOutcome) {
                case OK -> AnalysisOutcome.OK;
                case KO -> AnalysisOutcome.KO;
                case STANDBY -> AnalysisOutcome.STANDBY;
                default -> throw new IllegalArgumentException("Invalid automatic outcome");
            };

        instanceModule.setAutomaticOutcome(analysisOutcome);
        instanceModule.setAutomaticOutcomeDate(Instant.now());

        instanceModuleRepository.save(instanceModule);
    }

    @Override
    public List<InstanceModuleDTO> findAllByInstanceId(Long instanceId) {
        QInstanceModule instanceModule = QInstanceModule.instanceModule;
        JPQLQuery<InstanceModuleDTO> jpql = queryBuilder
            .<Instance>createQuery()
            .from(instanceModule)
            .where(instanceModule.instance.id.eq(instanceId))
            .select(buildInstanceModuleProjection(instanceModule))
            .orderBy(new OrderSpecifier<>(Order.ASC, Expressions.stringPath(ANALYSIS_TYPE_FILED)));

        return jpql.fetch();
    }

    private QBean<InstanceModuleDTO> buildInstanceModuleProjection(QInstanceModule instanceModule) {
        return Projections.fields(
            InstanceModuleDTO.class,
            QInstanceModule.instanceModule.id.as(ID_FIELD),
            QInstanceModule.instanceModule.instance.id.as(INSTANCE_ID_FIELD),
            QInstanceModule.instanceModule.module.id.as(MODULE_ID_FIELD),
            QInstanceModule.instanceModule.module.code.as(MODULE_CODE_FIELD),
            QInstanceModule.instanceModule.analysisType.as(ANALYSIS_TYPE_FILED),
            QInstanceModule.instanceModule.allowManualOutcome.as(ALLOW_MANUAL_OUTCOME_FIELD),
            QInstanceModule.instanceModule.automaticOutcomeDate.as(AUTOMATIC_OUTCOME_DATE_FIELD),
            QInstanceModule.instanceModule.automaticOutcome.as(AUTOMATIC_OUTCOME_FIELD),
            QInstanceModule.instanceModule.manualOutcome.as(MANUAL_OUTCOME_FIELD),
            QInstanceModule.instanceModule.status.as(STATUS_FIELD),
            QInstanceModule.instanceModule.manualOutcomeUser.id.as(ASSIGNED_USER_ID_FIELD),
            QInstanceModule.instanceModule.manualOutcomeDate.as(MANUAL_OUTCOME_DATE_FIELD)
        );
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
