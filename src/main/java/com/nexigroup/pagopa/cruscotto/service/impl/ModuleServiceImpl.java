package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.Module;
import com.nexigroup.pagopa.cruscotto.domain.QKpiConfiguration;
import com.nexigroup.pagopa.cruscotto.domain.QModule;
import com.nexigroup.pagopa.cruscotto.repository.ModuleRepository;
import com.nexigroup.pagopa.cruscotto.service.ModuleService;
import com.nexigroup.pagopa.cruscotto.service.dto.ModuleDTO;
import com.nexigroup.pagopa.cruscotto.service.mapper.ModuleMapper;
import com.nexigroup.pagopa.cruscotto.service.qdsl.QdslUtility;
import com.nexigroup.pagopa.cruscotto.service.qdsl.QueryBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPQLQuery;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Module}.
 */
@Service
@Transactional
public class ModuleServiceImpl implements ModuleService {

    private final Logger log = LoggerFactory.getLogger(ModuleServiceImpl.class);

    private final ModuleRepository moduleRepository;

    private final ModuleMapper moduleMapper;

    private final QueryBuilder queryBuilder;

    public ModuleServiceImpl(ModuleRepository moduleRepository, ModuleMapper moduleMapper, QueryBuilder queryBuilder) {
        this.moduleRepository = moduleRepository;
        this.moduleMapper = moduleMapper;
        this.queryBuilder = queryBuilder;
    }

    /**
     * Get all the module
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Override
    public Page<ModuleDTO> findAll(Pageable pageable) {
        log.debug("Request to get all module");

        QModule qModule = QModule.module;

        JPQLQuery<Module> query = queryBuilder
            .<Module>createQuery()
            .from(qModule);


        long total = query.fetchCount();

        JPQLQuery<ModuleDTO> jpqlQuery = query.select(createModuleProjection());

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

        List<ModuleDTO> results = jpqlQuery.fetch();

        return new PageImpl<>(results, pageable, total);
    }

    /**
     * Retrieves a ModuleDTO object by its unique identifier.
     *
     * @param id the unique identifier of the Module entity to retrieve
     * @return an {@link Optional} containing the found ModuleDTO if it exists, or an empty {@link Optional} if not found
     */
    @Override
    public Optional<ModuleDTO> findOne(Long id) {
        return moduleRepository.findById(id).map(moduleMapper::toDto);
    }

    /**
     * Deletes the Module entity with the specified ID from the repository.
     *
     * @param id the ID of the Module entity to be deleted
     */
    @Override
    public void delete(Long id) {
        moduleRepository.deleteById(id);
    }

    /**
     * Retrieves a paginated list of ModuleDTO entities that do not have associated configurations.
     *
     * @param pageable the pagination information, including the page number and size
     * @return a page of ModuleDTO entities that do not include any configuration
     */
    @Override
    public Page<ModuleDTO> findAllWithoutConfiguration(Pageable pageable) {
        log.debug("Request to get all Module without configuration");

        JPQLQuery<Module> jpql = queryBuilder
            .<Module>createQuery()
            .from(QModule.module)
            .leftJoin(QKpiConfiguration.kpiConfiguration)
            .on(QKpiConfiguration.kpiConfiguration.module.id.eq(QModule.module.id))
            .where(QKpiConfiguration.kpiConfiguration.id.isNull());

        long size = jpql.fetchCount();

        JPQLQuery<ModuleDTO> jpqlSelected = jpql.select(createModuleProjection());

        jpqlSelected.offset(pageable.getOffset());
        jpqlSelected.limit(pageable.getPageSize());

        pageable
            .getSortOr(Sort.by(Sort.Direction.ASC, "code"))
            .forEach(order -> {
                jpqlSelected.orderBy(
                    new OrderSpecifier<>(
                        order.isAscending() ? Order.ASC : Order.DESC,
                        Expressions.stringPath(order.getProperty()),
                        QdslUtility.toQueryDslNullHandling(order.getNullHandling())
                    )
                );
            });

        List<ModuleDTO> list = jpqlSelected.fetch();

        return new PageImpl<>(list, pageable, size);
    }
    private com.querydsl.core.types.Expression<ModuleDTO> createModuleProjection() {
        return Projections.fields(
            ModuleDTO.class,
            QModule.module.id.as("id"),
            QModule.module.code.as("code"),
            QModule.module.name.as("name"),
            QModule.module.description.as("description"),
            QModule.module.analysisType.as("analysisType"),
            QModule.module.allowManualOutcome.as("allowManualOutcome"),
            QModule.module.status.as("status"),
            QModule.module.configExcludePlannedShutdown.as("configExcludePlannedShutdown"),
            QModule.module.configExcludeUnplannedShutdown.as("configExcludeUnplannedShutdown"),
            QModule.module.configEligibilityThreshold.as("configEligibilityThreshold"),
            QModule.module.configTolerance.as("configTolerance"),
            QModule.module.configAverageTimeLimit.as("configAverageTimeLimit"),
            QModule.module.configEvaluationType.as("configEvaluationType")
        );
    }

}
