package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.*;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.ModuleCode;
import com.nexigroup.pagopa.cruscotto.repository.KpiConfigurationRepository;
import com.nexigroup.pagopa.cruscotto.service.KpiConfigurationService;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiConfigurationDTO;
import com.nexigroup.pagopa.cruscotto.service.qdsl.QdslUtility;
import com.nexigroup.pagopa.cruscotto.service.qdsl.QueryBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPQLQuery;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementation for managing KPI configurations.
 *
 * The {@code KpiConfigurationServiceImpl} class provides methods to interact
 * with the persisted KPI configurations, such as retrieving specific configurations
 * by module code and fetching all configurations in a pageable format.
 */
@Service
@Transactional
public class KpiConfigurationServiceImpl implements KpiConfigurationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(KpiConfigurationServiceImpl.class);
    public static final String FIELD_ID = "id";
    public static final String FIELD_MODULE_ID = "moduleId";
    public static final String FIELD_MODULE_CODE = "moduleCode";
    public static final String FIELD_EXCLUDE_PLANNED_SHUTDOWN = "excludePlannedShutdown";
    public static final String FIELD_EXCLUDE_UNPLANNED_SHUTDOWN = "excludeUnplannedShutdown";
    public static final String FIELD_ELIGIBILITY_THRESHOLD = "eligibilityThreshold";
    public static final String FIELD_TOLERANCE = "tolerance";
    public static final String FIELD_AVERAGE_TIME_LIMIT = "averageTimeLimit";
    public static final String FIELD_EVALUATION_TYPE = "evaluationType";

    private final QueryBuilder queryBuilder;

    public KpiConfigurationServiceImpl(QueryBuilder queryBuilder) {
        this.queryBuilder = queryBuilder;
    }

    /**
     * Finds and retrieves a KPI configuration based on the provided module code.
     *
     * @param code the code of the module for which the KPI configuration is retrieved
     * @return an {@link Optional} containing the {@link KpiConfigurationDTO} if found, or an empty {@link Optional} if no configuration exists for the specified code
     */
    @Override
    public Optional<KpiConfigurationDTO> findKpiConfigurationByCode(ModuleCode code) {
        QKpiConfiguration qKpiConfiguration = QKpiConfiguration.kpiConfiguration;

        JPQLQuery<KpiConfiguration> jpql = queryBuilder
            .<KpiConfiguration>createQuery()
            .from(qKpiConfiguration)
            .leftJoin(qKpiConfiguration.module, QModule.module)
            .where(qKpiConfiguration.module.code.eq(code.code));

        JPQLQuery<KpiConfigurationDTO> jpqlResponse = jpql.select(createKpiConfigurationProjection(qKpiConfiguration));

        return Optional.ofNullable(jpqlResponse.fetchOne());
    }

    /**
     * Fetches all KPI configurations in a paginated format according to the provided pageable details.
     *
     * @param pageable the pagination and sorting information
     * @return a paginated list of {@link KpiConfigurationDTO} containing the details of KPI configurations
     */
    @Override
    @Transactional(readOnly = true)
    public Page<KpiConfigurationDTO> findAll(Pageable pageable) {
        LOGGER.debug("Request to get all KpiConfigurations");

        QKpiConfiguration qKpiConfiguration = QKpiConfiguration.kpiConfiguration;

        JPQLQuery<KpiConfiguration> query = queryBuilder.<KpiConfiguration>createQuery().from(qKpiConfiguration);

        long total = query.fetchCount();

        JPQLQuery<KpiConfigurationDTO> jpqlQuery = query.select(createKpiConfigurationProjection(qKpiConfiguration));

        jpqlQuery.offset(pageable.getOffset());
        jpqlQuery.limit(pageable.getPageSize());

        pageable
            .getSort()
            .stream()
            .forEach(order -> {
                jpqlQuery.orderBy(
                    new OrderSpecifier<>(
                        order.isAscending() ? com.querydsl.core.types.Order.ASC : com.querydsl.core.types.Order.DESC,
                        Expressions.stringPath(order.getProperty()),
                        QdslUtility.toQueryDslNullHandling(order.getNullHandling())
                    )
                );
            });

        List<KpiConfigurationDTO> results = jpqlQuery.fetch();

        return new PageImpl<>(results, pageable, total);
    }

    private com.querydsl.core.types.Expression<KpiConfigurationDTO> createKpiConfigurationProjection(QKpiConfiguration qKpiConfiguration) {
        return Projections.fields(
            KpiConfigurationDTO.class,
            qKpiConfiguration.id.as(FIELD_ID),
            qKpiConfiguration.module.id.as(FIELD_MODULE_ID),
            qKpiConfiguration.module.code.as(FIELD_MODULE_CODE),
            qKpiConfiguration.excludePlannedShutdown.as(FIELD_EXCLUDE_PLANNED_SHUTDOWN),
            qKpiConfiguration.excludeUnplannedShutdown.as(FIELD_EXCLUDE_UNPLANNED_SHUTDOWN),
            qKpiConfiguration.eligibilityThreshold.as(FIELD_ELIGIBILITY_THRESHOLD),
            qKpiConfiguration.tolerance.as(FIELD_TOLERANCE),
            qKpiConfiguration.averageTimeLimit.as(FIELD_AVERAGE_TIME_LIMIT),
            qKpiConfiguration.evaluationType.as(FIELD_EVALUATION_TYPE)
        );
    }
}
