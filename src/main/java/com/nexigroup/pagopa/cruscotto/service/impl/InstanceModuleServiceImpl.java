package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.*;
import com.nexigroup.pagopa.cruscotto.service.InstanceModuleService;
import com.nexigroup.pagopa.cruscotto.service.dto.InstanceModuleDTO;
import com.nexigroup.pagopa.cruscotto.service.qdsl.QueryBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPQLQuery;
import java.util.Optional;
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

    private final QueryBuilder queryBuilder;

    public InstanceModuleServiceImpl(QueryBuilder queryBuilder) {
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
                        QInstanceModule.instanceModule.analysisDate.as("analysisDate"),
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
}
