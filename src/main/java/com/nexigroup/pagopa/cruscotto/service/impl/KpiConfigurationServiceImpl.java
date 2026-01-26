package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.*;
import com.nexigroup.pagopa.cruscotto.domain.Module;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.ModuleCode;
import com.nexigroup.pagopa.cruscotto.repository.KpiConfigurationRepository;
import com.nexigroup.pagopa.cruscotto.repository.ModuleRepository;
import com.nexigroup.pagopa.cruscotto.security.SecurityUtils;
import com.nexigroup.pagopa.cruscotto.service.GenericServiceException;
import com.nexigroup.pagopa.cruscotto.service.KpiConfigurationService;
import com.nexigroup.pagopa.cruscotto.service.bean.KpiConfigurationRequestBean;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiConfigurationDTO;
import com.nexigroup.pagopa.cruscotto.service.mapper.KpiConfigurationMapper;
import com.nexigroup.pagopa.cruscotto.service.qdsl.QdslUtility;
import com.nexigroup.pagopa.cruscotto.service.qdsl.QueryBuilder;
import com.nexigroup.pagopa.cruscotto.service.util.UserUtils;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPQLQuery;
import java.text.DecimalFormat;
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
    public static final String KPI_CONFIGURATION = "kpiConfiguration";
    public static final String FIELD_ID = "id";
    public static final String FIELD_MODULE_ID = "moduleId";
    public static final String FIELD_MODULE_CODE = "moduleCode";
    public static final String FIELD_MODULE_NAME = "moduleName";
    public static final String FIELD_EXCLUDE_PLANNED_SHUTDOWN = "excludePlannedShutdown";
    public static final String FIELD_EXCLUDE_UNPLANNED_SHUTDOWN = "excludeUnplannedShutdown";
    public static final String FIELD_ELIGIBILITY_THRESHOLD = "eligibilityThreshold";
    public static final String FIELD_TOLERANCE = "tolerance";
    public static final String FIELD_AVERAGE_TIME_LIMIT = "averageTimeLimit";
    public static final String FIELD_EVALUATION_TYPE = "evaluationType";
    public static final String FIELD_TRANSACTION_COUNT = "transactionCount";
    public static final String FIELD_INSTITUTION_COUNT = "institutionCount";
    public static final String FIELD_TRANSACTION_TOLERANCE = "transactionTolerance";
    public static final String FIELD_INSTITUTION_TOLERANCE = "institutionTolerance";
    public static final String FIELD_NOTIFICATION_TOLERANCE = "notificationTolerance";
    public static final String FIELD_CONFIG_EXCLUDE_PLANNED_SHUTDOWN = "configExcludePlannedShutdown";
    public static final String FIELD_CONFIG_EXCLUDE_UNPLANNED_SHUTDOWN = "configExcludeUnplannedShutdown";
    public static final String FIELD_CONFIG_ELIGIBILITY_THRESHOLD = "configEligibilityThreshold";
    public static final String FIELD_CONFIG_TOLERANCE = "configTolerance";
    public static final String FIELD_CONFIG_AVERAGE_TIME_LIMIT = "configAverageTimeLimit";
    public static final String FIELD_CONFIG_EVALUATION_TYPE = "configEvaluationType";
    public static final String FIELD_CONFIG_INSTITUTION_COUNT = "configInstitutionCount";
    public static final String FIELD_CONFIG_TRANSACTION_COUNT = "configTransactionCount";
    public static final String FIELD_CONFIG_INSTITUTION_TOLERANCE = "configInstitutionTolerance";
    public static final String FIELD_CONFIG_TRANSACTION_TOLERANCE = "configTransactionTolerance";
    public static final String FIELD_CONFIG_NOTIFICATION_TOLERANCE = "configNotificationTolerance";

    private static final String CURRENT_USER_LOGIN_NOT_FOUND = "Current user login not found";

    private final QueryBuilder queryBuilder;
    private final KpiConfigurationRepository kpiConfigurationRepository;
    private final ModuleRepository moduleRepository;
    private final KpiConfigurationMapper kpiConfigurationMapper;
    private final UserUtils userUtils;

    public KpiConfigurationServiceImpl(
        QueryBuilder queryBuilder,
        KpiConfigurationRepository kpiConfigurationRepository,
        KpiConfigurationMapper kpiConfigurationMapper,
        ModuleRepository moduleRepository,
        UserUtils userUtils
    ) {
        this.queryBuilder = queryBuilder;
        this.kpiConfigurationRepository = kpiConfigurationRepository;
        this.kpiConfigurationMapper = kpiConfigurationMapper;
        this.moduleRepository = moduleRepository;
        this.userUtils = userUtils;
    }

    /**
     * Finds and retrieves a KPI configuration based on the provided module code.
     *
     * @param code the code of the module for which the KPI configuration is retrieved
     * @return an {@link Optional} containing the {@link KpiConfigurationDTO} if found, or an empty {@link Optional} if no configuration exists for the specified code
     */
    @Override
    public Optional<KpiConfigurationDTO> findKpiConfigurationByCode(String code) {
        QKpiConfiguration qKpiConfiguration = QKpiConfiguration.kpiConfiguration;
        QModule qModule = QModule.module;

        JPQLQuery<KpiConfiguration> jpql = queryBuilder
            .<KpiConfiguration>createQuery()
            .from(qKpiConfiguration)
            .leftJoin(qKpiConfiguration.module, qModule)
            .where(qKpiConfiguration.module.code.eq(code));

        JPQLQuery<KpiConfigurationDTO> jpqlResponse = jpql.select(createKpiConfigurationProjection(qKpiConfiguration, qModule));

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

        QModule qModule = QModule.module;

        JPQLQuery<KpiConfiguration> query = queryBuilder
            .<KpiConfiguration>createQuery()
            .from(qKpiConfiguration)
            .leftJoin(qKpiConfiguration.module, qModule);

        long total = query.fetchCount();

        JPQLQuery<KpiConfigurationDTO> jpqlQuery = query.select(createKpiConfigurationProjection(qKpiConfiguration, qModule));

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

    private com.querydsl.core.types.Expression<KpiConfigurationDTO> createKpiConfigurationProjection(
        QKpiConfiguration qKpiConfiguration,
        QModule qModule
    ) {
        return Projections.fields(
            KpiConfigurationDTO.class,
            qKpiConfiguration.id.as(FIELD_ID),
            qKpiConfiguration.module.id.as(FIELD_MODULE_ID),
            qModule.code.as(FIELD_MODULE_CODE),
            qKpiConfiguration.excludePlannedShutdown.as(FIELD_EXCLUDE_PLANNED_SHUTDOWN),
            qKpiConfiguration.excludeUnplannedShutdown.as(FIELD_EXCLUDE_UNPLANNED_SHUTDOWN),
            qKpiConfiguration.eligibilityThreshold.as(FIELD_ELIGIBILITY_THRESHOLD),
            qKpiConfiguration.tolerance.as(FIELD_TOLERANCE),
            qKpiConfiguration.averageTimeLimit.as(FIELD_AVERAGE_TIME_LIMIT),
            qKpiConfiguration.evaluationType.as(FIELD_EVALUATION_TYPE),
            qKpiConfiguration.transactionCount.as(FIELD_TRANSACTION_COUNT),
            qKpiConfiguration.institutionCount.as(FIELD_INSTITUTION_COUNT),
            qKpiConfiguration.transactionTolerance.as(FIELD_TRANSACTION_TOLERANCE),
            qKpiConfiguration.institutionTolerance.as(FIELD_INSTITUTION_TOLERANCE),
            qKpiConfiguration.notificationTolerance.as(FIELD_NOTIFICATION_TOLERANCE),
            qModule.name.as(FIELD_MODULE_NAME),
            qModule.configExcludePlannedShutdown.as(FIELD_CONFIG_EXCLUDE_PLANNED_SHUTDOWN),
            qModule.configExcludeUnplannedShutdown.as(FIELD_CONFIG_EXCLUDE_UNPLANNED_SHUTDOWN),
            qModule.configEligibilityThreshold.as(FIELD_CONFIG_ELIGIBILITY_THRESHOLD),
            qModule.configTolerance.as(FIELD_CONFIG_TOLERANCE),
            qModule.configAverageTimeLimit.as(FIELD_CONFIG_AVERAGE_TIME_LIMIT),
            qModule.configEvaluationType.as(FIELD_CONFIG_EVALUATION_TYPE),
            qModule.configInstitutionCount.as(FIELD_CONFIG_INSTITUTION_COUNT),
            qModule.configTransactionCount.as(FIELD_CONFIG_TRANSACTION_COUNT),
            qModule.configInstitutionTolerance.as(FIELD_CONFIG_INSTITUTION_TOLERANCE),
            qModule.configTransactionTolerance.as(FIELD_CONFIG_TRANSACTION_TOLERANCE),
            qModule.configNotificationTolerance.as(FIELD_CONFIG_NOTIFICATION_TOLERANCE)

        );
    }

    /**
     * Save a new kpi configuration.
     *
     * @param kpiConfigurationToCreate the kpi configuration to save.
     * @return the persisted kpi configuration.
     */
    @Override
    public KpiConfigurationDTO saveNew(KpiConfigurationRequestBean kpiConfigurationToCreate) {
        AuthUser loggedUser = userUtils.getLoggedUser();

        //Il codice deve corrispondere ad un modulo che esiste realmente
        Module module = moduleRepository
            .findByCode(kpiConfigurationToCreate.getModuleCode())
            .orElseThrow(() ->
                new GenericServiceException(
                    String.format("Module with code %s does not exist", kpiConfigurationToCreate.getModuleCode()),
                    "module",
                    "kpiConfiguration.moduleNotExists"
                )
            );

        //Non possono esistere due configurazioni asssociate allo stesso modulo
        findKpiConfigurationByCode(kpiConfigurationToCreate.getModuleCode())
            .filter(kpiConfigurationDTO -> !kpiConfigurationDTO.getId().equals(kpiConfigurationToCreate.getId()))
            .ifPresent(kpiConfigurationDTO -> {
                throw new GenericServiceException(
                    String.format(
                        "Kpi configuration cannot be updated or created because the module %s has already a kpi configurations",
                        kpiConfigurationToCreate.getModuleCode()
                    ),
                    "module",
                    "module.cannotBeUpdated"
                );
            });

        KpiConfiguration kpiConfiguration = new KpiConfiguration();
        kpiConfiguration.setModule(module);

        DecimalFormat df = new DecimalFormat("0.00");

        if (module.getConfigAverageTimeLimit()) {
            kpiConfiguration.setAverageTimeLimit(Math.round(kpiConfigurationToCreate.getAverageTimeLimit() * 100.0) / 100.0);
        }
        if (module.getConfigEligibilityThreshold()) {
            kpiConfiguration.setEligibilityThreshold(Math.round(kpiConfigurationToCreate.getEligibilityThreshold() * 100.0) / 100.0);
        }
        if (module.getConfigEvaluationType()) {
            kpiConfiguration.setEvaluationType(kpiConfigurationToCreate.getEvaluationType());
        }
        if (module.getConfigExcludePlannedShutdown()) {
            kpiConfiguration.setExcludePlannedShutdown(kpiConfigurationToCreate.getExcludePlannedShutdown());
        }
        if (module.getConfigExcludeUnplannedShutdown()) {
            kpiConfiguration.setExcludeUnplannedShutdown(kpiConfigurationToCreate.getExcludeUnplannedShutdown());
        }
        if (module.getConfigTolerance()) {
            kpiConfiguration.setTolerance(Math.round(kpiConfigurationToCreate.getTolerance() * 100.0) / 100.0);
        }
        if (module.getConfigInstitutionTolerance()) {
            kpiConfiguration.setInstitutionTolerance(kpiConfigurationToCreate.getInstitutionTolerance());
        }
        if (module.getConfigTransactionTolerance()) {
            kpiConfiguration.setTransactionTolerance(kpiConfigurationToCreate.getTransactionTolerance());
        }

        if (module.getConfigNotificationTolerance()) {
            kpiConfiguration.setNotificationTolerance(kpiConfigurationToCreate.getNotificationTolerance());
        }

        if (module.getConfigInstitutionCount()) {
            kpiConfiguration.setInstitutionCount(kpiConfigurationToCreate.getInstitutionCount());
        }
        if (module.getConfigTransactionCount()) {
            kpiConfiguration.setTransactionCount(kpiConfigurationToCreate.getTransactionCount());
        }

        kpiConfiguration = kpiConfigurationRepository.save(kpiConfiguration);

        LOGGER.info("Creation of kpi configuration with identification {} by user {}", kpiConfiguration.getId(), loggedUser.getLogin());

        return kpiConfigurationMapper.toDto(kpiConfiguration);
    }

    @Override
    public KpiConfigurationDTO update(KpiConfigurationRequestBean kpiConfigurationToUpdate) {
        String loginUtenteLoggato = SecurityUtils.getCurrentUserLogin()
            .orElseThrow(() -> new RuntimeException(CURRENT_USER_LOGIN_NOT_FOUND));

        //Il codice deve corrispondere ad un modulo che esista realmente
        Module module = moduleRepository
            .findByCode(kpiConfigurationToUpdate.getModuleCode())
            .orElseThrow(() ->
                new GenericServiceException(
                    String.format("Module with code %s does not exist", kpiConfigurationToUpdate.getModuleCode()),
                    "module",
                    "kpiConfiguration.moduleNotExists"
                )
            );

        //Non possono esistere due configurazioni asssociate allo stesso modulo
        findKpiConfigurationByCode(kpiConfigurationToUpdate.getModuleCode())
            .filter(kpiConfigurationDTO -> !kpiConfigurationDTO.getId().equals(kpiConfigurationToUpdate.getId()))
            .ifPresent(kpiConfigurationDTO -> {
                throw new GenericServiceException(
                    String.format(
                        "Kpi configuration with id %s cannot be updated because the module %s has already a kpi configurations",
                        kpiConfigurationToUpdate.getId(),
                        kpiConfigurationToUpdate.getModuleCode()
                    ),
                    "module",
                    "module.cannotBeUpdated"
                );
            });

        return Optional.of(kpiConfigurationRepository.findById(kpiConfigurationToUpdate.getId()))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .map(kpiConfiguration -> {
                kpiConfiguration.setModule(module);
                if (module.getConfigAverageTimeLimit()) {
                    kpiConfiguration.setAverageTimeLimit(Math.round(kpiConfigurationToUpdate.getAverageTimeLimit() * 100.0) / 100.0);
                }
                if (module.getConfigEligibilityThreshold()) {
                    kpiConfiguration.setEligibilityThreshold(
                        Math.round(kpiConfigurationToUpdate.getEligibilityThreshold() * 100.0) / 100.0
                    );
                }
                if (module.getConfigEvaluationType()) {
                    kpiConfiguration.setEvaluationType(kpiConfigurationToUpdate.getEvaluationType());
                }
                if (module.getConfigExcludePlannedShutdown()) {
                    kpiConfiguration.setExcludePlannedShutdown(kpiConfigurationToUpdate.getExcludePlannedShutdown());
                }
                if (module.getConfigExcludeUnplannedShutdown()) {
                    kpiConfiguration.setExcludeUnplannedShutdown(kpiConfigurationToUpdate.getExcludeUnplannedShutdown());
                }
                if (module.getConfigTolerance()) {
                    kpiConfiguration.setTolerance(Math.round(kpiConfigurationToUpdate.getTolerance() * 100.0) / 100.0);
                }
                if (module.getConfigInstitutionTolerance()) {
                    kpiConfiguration.setInstitutionTolerance(kpiConfigurationToUpdate.getInstitutionTolerance());
                }
                if (module.getConfigTransactionTolerance()) {
                    kpiConfiguration.setTransactionTolerance(kpiConfigurationToUpdate.getTransactionTolerance());
                }
                if (module.getConfigNotificationTolerance()) {
                    kpiConfiguration.setNotificationTolerance(kpiConfigurationToUpdate.getNotificationTolerance());
                }
                if (module.getConfigInstitutionCount()) {
                    kpiConfiguration.setInstitutionCount(kpiConfigurationToUpdate.getInstitutionCount());
                }
                if (module.getConfigTransactionCount()) {
                    kpiConfiguration.setTransactionCount(kpiConfigurationToUpdate.getTransactionCount());
                }

                kpiConfigurationRepository.save(kpiConfiguration);

                LOGGER.info("Updating of kpiConfiguration with identification {} by user {}", kpiConfiguration.getId(), loginUtenteLoggato);

                return kpiConfiguration;
            })
            .map(kpiConfigurationMapper::toDto)
            .orElseThrow(() ->
                new GenericServiceException(
                    String.format("kpiConfiguration with id %s not exist", kpiConfigurationToUpdate.getId()),
                    KPI_CONFIGURATION,
                    "kpiConfiguration.notExists"
                )
            );
    }

    @Override
    public KpiConfigurationDTO delete(Long id) {
        return Optional.of(kpiConfigurationRepository.findById(id))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .map(kpiConfiguration -> {
                String loginUtenteLoggato = SecurityUtils.getCurrentUserLogin()
                    .orElseThrow(() -> new RuntimeException("current user login not found"));
                kpiConfigurationRepository.deleteById(id);
                LOGGER.info(
                    "Physical deleting of kpi configuration with identification {} by user {}",
                    kpiConfiguration.getId(),
                    loginUtenteLoggato
                );
                return kpiConfiguration;
            })
            .map(kpiConfigurationMapper::toDto)
            .orElseThrow(() ->
                new GenericServiceException(
                    String.format("kpi configuration with id %s not exist", id),
                    KPI_CONFIGURATION,
                    "kpiConfiguration.notExists"
                )
            );
    }

    @Override
    public Optional<KpiConfigurationDTO> findByModuleCode(ModuleCode moduleCode) {
        LOGGER.debug("Request to find KpiConfiguration by ModuleCode: {}", moduleCode);
        return kpiConfigurationRepository.findByModuleCode(moduleCode.code)
            .map(kpiConfigurationMapper::toDto);
    }
}
