package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.*;
import com.nexigroup.pagopa.cruscotto.domain.Module;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.AnalysisType;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.ModuleStatus;
import com.nexigroup.pagopa.cruscotto.repository.ModuleRepository;
import com.nexigroup.pagopa.cruscotto.security.SecurityUtils;
import com.nexigroup.pagopa.cruscotto.service.GenericServiceException;
import com.nexigroup.pagopa.cruscotto.service.ModuleService;
import com.nexigroup.pagopa.cruscotto.service.bean.ModuleRequestBean;
import com.nexigroup.pagopa.cruscotto.service.dto.ModuleDTO;
import com.nexigroup.pagopa.cruscotto.service.mapper.ModuleMapper;
import com.nexigroup.pagopa.cruscotto.service.qdsl.QdslUtility;
import com.nexigroup.pagopa.cruscotto.service.qdsl.QueryBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPQLQuery;
import java.time.Instant;
import java.time.ZonedDateTime;
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

    private static final String CURRENT_USER_LOGIN_NOT_FOUND = "Current user login not found";
    private static final String MODULE = "module";

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
            .from(qModule)
            .where(qModule.deleted.eq(false).and(qModule.deletedDate.isNull()));
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
     * Logically deletes the Module entity with the specified ID from the repository.
     *
     * @param id the ID of the Module entity to be deleted
     */
    @Override
    public boolean deleteModule(Long id) {
        Module module = moduleRepository
            .findOneByIdAndNotDeleted(id)
            .orElseThrow(() -> new GenericServiceException(String.format("Module with id %s not exist", id), MODULE, "module.notExists"));

        if (module.getAnalysisType().equals(AnalysisType.MANUALE)) {
            module.setDeleted(Boolean.TRUE);
            module.setDeletedDate(ZonedDateTime.now());
            module.setStatus(ModuleStatus.NON_ATTIVO);
            moduleRepository.save(module);
            log.debug("Logically deleted module: {}", module);
            return true;
        }

        return false;
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

        QModule module = QModule.module;
        JPQLQuery<Module> jpql = queryBuilder
            .<Module>createQuery()
            .from(module)
            .leftJoin(QKpiConfiguration.kpiConfiguration)
            .on(QKpiConfiguration.kpiConfiguration.module.id.eq(module.id))
            .where(QKpiConfiguration.kpiConfiguration.id.isNull().and(module.deleted.eq(false)).and(module.deletedDate.isNull()));

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

    /**
     * Save a new module.
     *
     * @param moduleToCreate the entity to save.
     * @return the persisted entity.
     */
    @Override
    public ModuleDTO saveNew(ModuleRequestBean moduleToCreate) {
        String loginUtenteLoggato = SecurityUtils.getCurrentUserLogin()
            .orElseThrow(() -> new RuntimeException(CURRENT_USER_LOGIN_NOT_FOUND));

        //Un modulo con lo stesso codice non deve già esistere
        moduleRepository
            .findByCode(moduleToCreate.getCode())
            .filter(module -> module.getCode().equals(moduleToCreate.getCode()))
            .ifPresent(module -> {
                throw new GenericServiceException(
                    String.format(
                        "Module cannot be cannot be created. The code %s is already assigned to an existing module",
                        moduleToCreate.getCode()
                    ),
                    "module",
                    "module.cannotBeCreated"
                );
            });

        Module module = new Module();
        module.setCode(moduleToCreate.getCode());
        module.setName(moduleToCreate.getName());
        module.setDescription(moduleToCreate.getDescription());
        module.setAnalysisType(moduleToCreate.getAnalysisType());
        module.setAllowManualOutcome(moduleToCreate.getAllowManualOutcome());
        module.setStatus(moduleToCreate.getStatus());
        module.setCreatedBy(loginUtenteLoggato);
        module.setCreatedDate(Instant.now());
        module.setConfigExcludePlannedShutdown(moduleToCreate.getConfigExcludePlannedShutdown());
        module.setConfigExcludeUnplannedShutdown(moduleToCreate.getConfigExcludeUnplannedShutdown());
        module.setConfigEligibilityThreshold(moduleToCreate.getConfigEligibilityThreshold());
        module.setConfigTolerance(moduleToCreate.getConfigTolerance());
        module.setConfigAverageTimeLimit(moduleToCreate.getConfigAverageTimeLimit());
        module.setConfigEvaluationType(moduleToCreate.getConfigEvaluationType());
        module.setConfigTransactionCount(moduleToCreate.getConfigTransactionCount());
        module.setConfigTransactionTolerance(moduleToCreate.getConfigTransactionTolerance());
        module.setConfigInstitutionCount(moduleToCreate.getConfigInstitutionCount());
        module.setConfigInstitutionTolerance(moduleToCreate.getConfigInstitutionTolerance());

        module = moduleRepository.save(module);

        log.info("Creation of module with identification {} by user {}", module.getId(), loginUtenteLoggato);

        return moduleMapper.toDto(module);
    }

    /**
     * Update a module.
     *
     * @param moduleToUpdate the entity to update.
     * @return the persisted entity.
     */
    @Override
    public ModuleDTO update(ModuleRequestBean moduleToUpdate) {
        return Optional.of(moduleRepository.findById(moduleToUpdate.getId()))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .map(module -> {
                String loginUtenteLoggato = SecurityUtils.getCurrentUserLogin()
                    .orElseThrow(() -> new RuntimeException(CURRENT_USER_LOGIN_NOT_FOUND));

                if (!module.getCode().equals(moduleToUpdate.getCode())) {
                    throw new GenericServiceException(
                        String.format("Module cannot be cannot be updated. The module code %s cannot be modified", module.getCode()),
                        "module",
                        "module.cannotBeUpdated"
                    );
                }

                module.setCode(moduleToUpdate.getCode());
                module.setName(moduleToUpdate.getName());
                module.setDescription(moduleToUpdate.getDescription());
                module.setAnalysisType(moduleToUpdate.getAnalysisType());
                module.setAllowManualOutcome(moduleToUpdate.getAllowManualOutcome());
                module.setStatus(moduleToUpdate.getStatus());
                module.setConfigExcludePlannedShutdown(moduleToUpdate.getConfigExcludePlannedShutdown());
                module.setConfigExcludeUnplannedShutdown(moduleToUpdate.getConfigExcludeUnplannedShutdown());
                module.setConfigEligibilityThreshold(moduleToUpdate.getConfigEligibilityThreshold());
                module.setConfigTolerance(moduleToUpdate.getConfigTolerance());
                module.setConfigAverageTimeLimit(moduleToUpdate.getConfigAverageTimeLimit());
                module.setConfigEvaluationType(moduleToUpdate.getConfigEvaluationType());
                module.setConfigTransactionCount(moduleToUpdate.getConfigTransactionCount());
                module.setConfigTransactionTolerance(moduleToUpdate.getConfigTransactionTolerance());
                module.setConfigInstitutionCount(moduleToUpdate.getConfigInstitutionCount());
                module.setConfigInstitutionTolerance(moduleToUpdate.getConfigInstitutionTolerance());
                moduleRepository.save(module);

                log.info("Updating of module with identification {} by user {}", module.getId(), loginUtenteLoggato);

                return module;
            })
            .map(moduleMapper::toDto)
            .orElseThrow(() ->
                new GenericServiceException(
                    String.format("Module with id %s not exist", moduleToUpdate.getId()),
                    MODULE,
                    "module.notExists"
                )
            );
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
            QModule.module.configEvaluationType.as("configEvaluationType"),
            QModule.module.configTransactionCount.as("configTransactionCount"),
            QModule.module.configTransactionTolerance.as("configTransactionTolerance"),
            QModule.module.configInstitutionCount.as("configInstitutionCount"),
            QModule.module.configInstitutionTolerance.as("configInstitutionTolerance")
        );
    }
}
