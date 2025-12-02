package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.*;
import com.nexigroup.pagopa.cruscotto.domain.Module;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.AnalysisOutcome;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.AnalysisType;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.AuthenticationType;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.InstanceStatus;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.ModuleCode;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.ModuleStatus;
import com.nexigroup.pagopa.cruscotto.repository.AnagPartnerRepository;
import com.nexigroup.pagopa.cruscotto.repository.InstanceRepository;
import com.nexigroup.pagopa.cruscotto.repository.ModuleRepository;
import com.nexigroup.pagopa.cruscotto.security.AuthoritiesConstants;
import com.nexigroup.pagopa.cruscotto.security.SecurityUtils;
import com.nexigroup.pagopa.cruscotto.service.AnagPartnerService;
import com.nexigroup.pagopa.cruscotto.service.AuthUserService;
import com.nexigroup.pagopa.cruscotto.service.GenericServiceException;
import com.nexigroup.pagopa.cruscotto.service.InstanceService;
import com.nexigroup.pagopa.cruscotto.service.bean.InstanceRequestBean;
import com.nexigroup.pagopa.cruscotto.service.dto.AuthUserDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.InstanceDTO;
import com.nexigroup.pagopa.cruscotto.service.filter.InstanceFilter;
import com.nexigroup.pagopa.cruscotto.service.mapper.InstanceMapper;
import com.nexigroup.pagopa.cruscotto.service.qdsl.QdslUtility;
import com.nexigroup.pagopa.cruscotto.service.qdsl.QueryBuilder;
import com.nexigroup.pagopa.cruscotto.service.util.UserUtils;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.*;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAUpdateClause;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Instance}.
 */
@Service
@Transactional
public class InstanceServiceImpl implements InstanceService {

    private final Logger LOGGER = LoggerFactory.getLogger(InstanceServiceImpl.class);

    private static final String ID_FIELD = "id";

    private static final String INSTANCE_IDENTIFICATION_FIELD = "instanceIdentification";

    private static final String PARTNER_ID_FIELD = "partnerId";

    private static final String PARTNER_FISCAL_CODE_FIELD = "partnerFiscalCode";

    private static final String PARTNER_NAME_FIELD = "partnerName";

    private static final String APPLICATION_DATE_FIELD = "applicationDate";

    private static final String PREDICTED_DATE_ANALYSIS_FIELD = "predictedDateAnalysis";

    private static final String ANALYSIS_PERIOD_START_DATE_FIELD = "analysisPeriodStartDate";

    private static final String ANALYSIS_PERIOD_END_DATE_FIELD = "analysisPeriodEndDate";

    private static final String INSTANCE = "instance";

    private static final String CURRENT_USER_LOGIN_NOT_FOUND = "Current user login not found";

    static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final InstanceRepository instanceRepository;

    private final AnagPartnerRepository anagPartnerRepository;

    private final ModuleRepository moduleRepository;

    private final InstanceMapper instanceMapper;

    private final QueryBuilder queryBuilder;

    private final UserUtils userUtils;

    private final AuthUserService authUserService;

    private final AnagPartnerService anagPartnerService;

    public InstanceServiceImpl(
        InstanceRepository instanceRepository,
        AnagPartnerRepository anagPartnerRepository,
        ModuleRepository moduleRepository,
        InstanceMapper instanceMapper,
        QueryBuilder queryBuilder,
        UserUtils userUtils,
        AuthUserService authUserService,
        AnagPartnerService anagPartnerService
    ) {
        this.instanceRepository = instanceRepository;
        this.anagPartnerRepository = anagPartnerRepository;
        this.moduleRepository = moduleRepository;
        this.instanceMapper = instanceMapper;
        this.queryBuilder = queryBuilder;
        this.userUtils = userUtils;
        this.authUserService = authUserService;
        this.anagPartnerService = anagPartnerService;
    }

    /**
     * Get all the instance by filter.
     *
     * @param filter the pagination filter.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Override
    public Page<InstanceDTO> findAll(InstanceFilter filter, Pageable pageable) {
        LOGGER.debug("Request to get all Instance by filter: {}", filter);

        BooleanBuilder builder = new BooleanBuilder();

        // IMPORTANT: Exclude deleted instances (status CANCELLATA)
        builder.and(QInstance.instance.status.ne(InstanceStatus.CANCELLATA));

        if (StringUtils.isNotBlank(filter.getPartnerId())) {
            builder.and(QInstance.instance.partner.id.eq(Long.valueOf(filter.getPartnerId())));
        }
        if (filter.getStatus() != null) {
            builder.and(QInstance.instance.status.eq(filter.getStatus()));
        }

        if (filter.getPredictedAnalysisStartDate() != null) {
            LocalDate predictedAnalysisStartDate = LocalDate.parse(filter.getPredictedAnalysisStartDate(), formatter);
            builder.and(QInstance.instance.predictedDateAnalysis.goe(predictedAnalysisStartDate));
        }
        if (filter.getPredictedAnalysisEndDate() != null) {
            LocalDate predictedAnalysisEndDate = LocalDate.parse(filter.getPredictedAnalysisEndDate(), formatter);
            builder.and(QInstance.instance.predictedDateAnalysis.loe(predictedAnalysisEndDate));
        }

        if (filter.getAnalysisStartDate() != null) {
            LocalDate analysisStartDate = LocalDate.parse(filter.getAnalysisStartDate(), formatter);
            builder.and(QInstance.instance.analysisPeriodStartDate.goe(analysisStartDate));
        }
        if (filter.getAnalysisEndDate() != null) {
            LocalDate analysisEndDate = LocalDate.parse(filter.getAnalysisEndDate(), formatter);
            builder.and(QInstance.instance.analysisPeriodEndDate.loe(analysisEndDate));
        }

        JPQLQuery<Instance> jpql = queryBuilder
            .<Instance>createQuery()
            .from(QInstance.instance)
            .leftJoin(QInstance.instance.partner, QAnagPartner.anagPartner)
            .where(builder);

        long size = jpql.fetchCount();

        JPQLQuery<InstanceDTO> jpqlSelected = jpql.select(
            Projections.fields(
                InstanceDTO.class,
                QInstance.instance.id.as("id"),
                QInstance.instance.instanceIdentification.as("instanceIdentification"),
                QAnagPartner.anagPartner.id.as("partnerId"),
                QAnagPartner.anagPartner.name.as("partnerName"),
                QAnagPartner.anagPartner.fiscalCode.as("partnerFiscalCode"),
                QInstance.instance.applicationDate.as("applicationDate"),
                QInstance.instance.predictedDateAnalysis.as("predictedDateAnalysis"),
                QInstance.instance.assignedUser.id.as("assignedUserId"),
                QInstance.instance.assignedUser.firstName.as("assignedFirstName"),
                QInstance.instance.assignedUser.lastName.as("assignedLastName"),
                QInstance.instance.analysisPeriodStartDate.as("analysisPeriodStartDate"),
                QInstance.instance.analysisPeriodEndDate.as("analysisPeriodEndDate"),
                QInstance.instance.status.as("status"),
                QInstance.instance.lastAnalysisDate.as("lastAnalysisDate"),
                QInstance.instance.lastAnalysisOutcome.as("lastAnalysisOutcome"),
                QInstance.instance.changePartnerQualified.as("changePartnerQualified")
            )
        );

        jpqlSelected.offset(pageable.getOffset());
        jpqlSelected.limit(pageable.getPageSize());

        pageable
            .getSortOr(Sort.by(Sort.Direction.ASC, "predictedDateAnalysis"))
            .forEach(order -> {
                jpqlSelected.orderBy(
                    new OrderSpecifier<>(
                        order.isAscending() ? Order.ASC : Order.DESC,
                        Expressions.stringPath(order.getProperty()),
                        QdslUtility.toQueryDslNullHandling(order.getNullHandling())
                    )
                );
            });

        List<InstanceDTO> list = jpqlSelected.fetch();

        return new PageImpl<>(list, pageable, size);
    }

    @Override
    public Optional<InstanceDTO> findOne(Long id) {
        return instanceRepository.findById(id).map(instanceMapper::toDto);
    }

    /**
     * Save a new instance.
     *
     * @param instanceToCreate the entity to save.
     * @return the persisted entity.
     */
    @Override
    public InstanceDTO saveNew(InstanceRequestBean instanceToCreate) {
        StringBuilder instanceIdentification = new StringBuilder();

        AuthUser loggedUser = userUtils.getLoggedUser();

        AnagPartner partner = anagPartnerRepository
            .findById(Long.valueOf(instanceToCreate.getPartnerId()))
            .orElseThrow(() ->
                new GenericServiceException(
                    String.format("Partner with id %s not exist", instanceToCreate.getPartnerId()),
                    INSTANCE,
                    "instance.partnerNotExists"
                )
            );

        ZonedDateTime now = ZonedDateTime.now(ZoneId.systemDefault());

        instanceIdentification
            .append("INST-")
            .append(partner.getFiscalCode())
            .append("-")
            .append(now.format(DateTimeFormatter.ofPattern("yyyyMMdd")))
            .append("-")
            .append(now.format(DateTimeFormatter.ofPattern("HHmmssSSS")));

        Instance instance = new Instance();
        instance.setInstanceIdentification(instanceIdentification.toString());
        instance.setPartner(partner);
        instance.setPredictedDateAnalysis(LocalDate.parse(instanceToCreate.getPredictedDateAnalysis(), formatter));
        instance.setAnalysisPeriodStartDate(LocalDate.parse(instanceToCreate.getAnalysisPeriodStartDate(), formatter));
        instance.setAnalysisPeriodEndDate(LocalDate.parse(instanceToCreate.getAnalysisPeriodEndDate(), formatter));
        instance.setStatus(InstanceStatus.BOZZA);
        instance.setApplicationDate(now.toInstant());
        instance.setAssignedUser(loggedUser);
        instance.setChangePartnerQualified(BooleanUtils.toBoolean(instanceToCreate.getChangePartnerQualified()));
        instance.setLastAnalysisOutcome(AnalysisOutcome.STANDBY);

        Set<InstanceModule> instanceModules = new HashSet<>();
        List<Module> modules = moduleRepository.findAllByStatus(ModuleStatus.ATTIVO);

        for (Module module : modules) {
            InstanceModule instanceModule = new InstanceModule();
            instanceModule.setInstance(instance);
            instanceModule.setModule(module);
            instanceModule.setModuleCode(module.getCode());
            instanceModule.setAnalysisType(module.getAnalysisType());
            instanceModule.setStatus(module.getStatus());
            instanceModule.setAllowManualOutcome(module.isAllowManualOutcome());

            if (module.getAnalysisType().equals(AnalysisType.AUTOMATICA)) {
                instanceModule.setAutomaticOutcome(AnalysisOutcome.STANDBY);
            } else if (module.getAnalysisType().equals(AnalysisType.MANUALE)) {
                instanceModule.setManualOutcome(AnalysisOutcome.STANDBY);
            }

            instanceModules.add(instanceModule);
        }

        instance.setInstanceModules(instanceModules);
        instance = instanceRepository.save(instance);

        LOGGER.info("Creation of instance with identification {} by user {}", instance.getInstanceIdentification(), loggedUser.getLogin());

        return instanceMapper.toDto(instance);
    }

    /**
     * Update a instance.
     *
     * @param instanceToUpdate the entity to save.
     * @return the persisted entity.
     */
    @Override
    public InstanceDTO update(InstanceRequestBean instanceToUpdate) {
        return Optional.of(instanceRepository.findById(instanceToUpdate.getId()))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .map(instance -> {
                if (!instance.getStatus().equals(InstanceStatus.BOZZA) && !instance.getStatus().equals(InstanceStatus.PIANIFICATA)) {
                    throw new GenericServiceException(
                        String.format(
                            "Instance with id %s cannot be updated because it is in %s status",
                            instanceToUpdate.getId(),
                            instance.getStatus()
                        ),
                        INSTANCE,
                        "instance.cannotBeUpdated"
                    );
                }

                String loginUtenteLoggato = SecurityUtils.getCurrentUserLogin()
                    .orElseThrow(() -> new RuntimeException(CURRENT_USER_LOGIN_NOT_FOUND));

                AnagPartner partner = anagPartnerRepository
                    .findById(Long.valueOf(instanceToUpdate.getPartnerId()))
                    .orElseThrow(() ->
                        new GenericServiceException(
                            String.format("Partner with id %s not exist", instanceToUpdate.getPartnerId()),
                            INSTANCE,
                            "instance.partnerNotExists"
                        )
                    );
                instance.setPartner(partner);
                instance.setPredictedDateAnalysis(LocalDate.parse(instanceToUpdate.getPredictedDateAnalysis(), formatter));
                instance.setAnalysisPeriodStartDate(LocalDate.parse(instanceToUpdate.getAnalysisPeriodStartDate(), formatter));
                instance.setAnalysisPeriodEndDate(LocalDate.parse(instanceToUpdate.getAnalysisPeriodEndDate(), formatter));
                instance.setChangePartnerQualified(BooleanUtils.toBoolean(instanceToUpdate.getChangePartnerQualified()));
                instanceRepository.save(instance);

                LOGGER.info(
                    "Updating of instance with identification {} by user {}",
                    instance.getInstanceIdentification(),
                    loginUtenteLoggato
                );

                return instance;
            })
            .map(instanceMapper::toDto)
            .orElseThrow(() ->
                new GenericServiceException(
                    String.format("Instance with id %s not exist", instanceToUpdate.getId()),
                    INSTANCE,
                    "instance.notExists"
                )
            );
    }

    @Override
    public InstanceDTO delete(Long id) {
        return Optional.of(instanceRepository.findById(id))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .map(instance -> {
                String loginUtenteLoggato = SecurityUtils.getCurrentUserLogin()
                    .orElseThrow(() -> new RuntimeException(CURRENT_USER_LOGIN_NOT_FOUND));

                AuthenticationType authenticationType = SecurityUtils.getAuthenticationTypeUserLogin()
                    .orElseThrow(() -> new RuntimeException("Authentication Type not found"));

                // Get user with authorities
                AuthUserDTO currentUser = authUserService.getUserWithAuthorities(authenticationType)
                    .orElseThrow(() -> new RuntimeException("Current user not found"));    

                // Check if user has force delete permission
                boolean canForceDelete = currentUser.getAuthorities() != null && 
                    currentUser.getAuthorities().contains(AuthoritiesConstants.INSTANCE_FORCED_DELETION);

                // Check if deletion is allowed based on status (unless user can force delete)
                if (!canForceDelete && 
                    !instance.getStatus().equals(InstanceStatus.BOZZA) && 
                    !instance.getStatus().equals(InstanceStatus.PIANIFICATA)) {
                    throw new GenericServiceException(
                        String.format("Instance with id %s cannot be deleted because it is in %s status", id, instance.getStatus()),
                        INSTANCE,
                        "instance.cannotBeDeleted"
                    );
                }

                if (canForceDelete) {
                    // Soft delete - just change status to CANCELLATA
                    instance.setStatus(InstanceStatus.CANCELLATA);
                    instanceRepository.save(instance);

                    // Get partner info
                    AnagPartner partner = instance.getPartner();
                    
                    // Update partner's analysis tracking fields
                    updatePartnerAnalysisFieldsAfterDeletion(partner.getId(), instance.getChangePartnerQualified());

                } else instanceRepository.deleteById(id);

                if (canForceDelete) {
                    LOGGER.warn(
                        "FORCED deletion of instance with identification {} in status {} by user {} with INSTANCE_FORCE_DELETE permission",
                        instance.getInstanceIdentification(),
                        instance.getStatus(),
                        loginUtenteLoggato
                    );
                } else {
                    LOGGER.info(
                        "Physical deleting of instance with identification {} by user {}",
                        instance.getInstanceIdentification(),
                        loginUtenteLoggato
                    );
                }

                return instance;
            })
            .map(instanceMapper::toDto)
            .orElseThrow(() ->
                new GenericServiceException(String.format("Instance with id %s not exist", id), INSTANCE, "instance.notExists")
            );
    }

    private void updatePartnerAnalysisFieldsAfterDeletion(Long partnerId, Boolean changePartnerQualified) {
        // Find most recent remaining instance for this partner
        List<Instance> remainingInstances = instanceRepository
                .findByPartnerIdAndStatusOrderByLastAnalysisDateDesc(partnerId, InstanceStatus.ESEGUITA);

        if (!remainingInstances.isEmpty()) {

            // Check if instance being deleted has changed partner qualification
            if (changePartnerQualified != null && changePartnerQualified) {

                // Find the most recent instance with changePartnerQualified = true
                Optional<Instance> mostRecentChangedQualifiedInstance = remainingInstances.stream()
                        .filter(instance -> instance.getChangePartnerQualified() != null
                                && instance.getChangePartnerQualified())
                        .findFirst();

                if (mostRecentChangedQualifiedInstance.isPresent()) {
                    Instance instance = mostRecentChangedQualifiedInstance.get();
                    AnalysisOutcome outcome = instance.getLastAnalysisOutcome();

                    // Partner is qualified if outcome is OK
                    // Partner is NOT qualified if outcome is KO
                    boolean isQualified = outcome == AnalysisOutcome.OK;

                    anagPartnerService.changePartnerQualified(partnerId, isQualified);

                    LOGGER.info(
                            "Partner {} qualified status updated to {} based on instance {} with outcome {}",
                            partnerId,
                            isQualified,
                            instance.getInstanceIdentification(),
                            outcome);
                } else {
                    // No remaining instance changed qualification, restore to default qualified flag
                    anagPartnerService.changePartnerQualified(partnerId, false);
                    LOGGER.info(
                            "Partner {} qualified flag restored to default because no remaining instance changed qualification for partner {}.",
                            partnerId);
                }

            } else {
                // If changePartnerQualified is false or null, retain current qualified status
                LOGGER.info(
                        "Partner {} qualified status remains unchanged after deletion of instance that did not change qualification",
                        partnerId);
            }

            Instance mostRecent = remainingInstances.get(0);
            anagPartnerService.updateLastAnalysisDate(partnerId, mostRecent.getLastAnalysisDate());
            anagPartnerService.updateAnalysisPeriodDates(
                    partnerId,
                    mostRecent.getAnalysisPeriodStartDate(),
                    mostRecent.getAnalysisPeriodEndDate());
            LOGGER.info(
                            "Partner lastAnalysisDate, lastAnalysisPeriod updated to values based on instance {}",
                            partnerId,
                            mostRecent.getInstanceIdentification()
                        );         
        } else {
            // No more instances for this partner
            anagPartnerService.changePartnerQualified(partnerId, false);
            anagPartnerService.updateLastAnalysisDate(partnerId, null);
            anagPartnerService.updateAnalysisPeriodDates(
                    partnerId,
                    null,
                    null);
            LOGGER.info(
                            "Partner lastAnalysisDate, lastAnalysisPeriod and qualified flag restored to default values after deletion of all instances for partner {}",
                            partnerId);        
        }

    }


    @Override
    public List<InstanceDTO> findInstanceToCalculate(ModuleCode moduleCode, Integer limit) {
        JPQLQuery<InstanceDTO> jpql = queryBuilder
            .<Instance>createQuery()
            .from(QInstance.instance)
            .leftJoin(QInstance.instance.instanceModules, QInstanceModule.instanceModule)
            .where(
                QInstance.instance.status
                    .in(InstanceStatus.PIANIFICATA, InstanceStatus.IN_ESECUZIONE)
                    .and(QInstance.instance.predictedDateAnalysis.loe(LocalDate.now()))
                    .and(QInstanceModule.instanceModule.moduleCode.eq(moduleCode.code))
                    .and(QInstanceModule.instanceModule.analysisType.eq(AnalysisType.AUTOMATICA))
                    .and(QInstanceModule.instanceModule.status.eq(ModuleStatus.ATTIVO))
                    .and(QInstanceModule.instanceModule.automaticOutcomeDate.isNull())
            )
            .select(buildInstanceProjection(QInstance.instance))
            .limit(limit)
            .orderBy(new OrderSpecifier<>(Order.ASC, Expressions.stringPath("applicationDate")));

        return jpql.fetch();
    }

    @Override
    public InstanceDTO updateStatus(Long id) {
        return Optional.of(instanceRepository.findById(id))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .map(instance -> {
                if (!instance.getStatus().equals(InstanceStatus.BOZZA) && !instance.getStatus().equals(InstanceStatus.PIANIFICATA)) {
                    throw new GenericServiceException(
                        String.format(
                            "Cannot be updated status of instance with id %s because it is in %s status",
                            id,
                            instance.getStatus()
                        ),
                        INSTANCE,
                        "instance.cannotBeUpdated"
                    );
                }

                String loginUtenteLoggato = SecurityUtils.getCurrentUserLogin()
                    .orElseThrow(() -> new RuntimeException(CURRENT_USER_LOGIN_NOT_FOUND));

                instance.setStatus(instance.getStatus().equals(InstanceStatus.BOZZA) ? InstanceStatus.PIANIFICATA : InstanceStatus.BOZZA);

                instanceRepository.save(instance);

                LOGGER.info(
                    "Updating status of instance with identifier {} in {} by user {}",
                    instance.getInstanceIdentification(),
                    instance.getStatus(),
                    loginUtenteLoggato
                );

                return instance;
            })
            .map(instanceMapper::toDto)
            .orElseThrow(() ->
                new GenericServiceException(String.format("Instance with id %s not exist", id), INSTANCE, "instance.notExists")
            );
    }

    @Override
    public List<InstanceDTO> findInstanceToCalculate(Integer limit) {
        QInstance instance = QInstance.instance;
        JPQLQuery<InstanceDTO> jpql = queryBuilder
            .<Instance>createQuery()
            .from(instance)
            .where(
                instance.status
                    .eq(InstanceStatus.IN_ESECUZIONE)
                    .and(instance.predictedDateAnalysis.loe(LocalDate.now()))
                    .and(instance.lastAnalysisDate.isNull())
            )
            .select(buildInstanceProjection(instance))
            .limit(limit)
            .orderBy(new OrderSpecifier<>(Order.ASC, Expressions.stringPath(APPLICATION_DATE_FIELD)));

        return jpql.fetch();
    }

    private QBean<InstanceDTO> buildInstanceProjection(QInstance instance) {
        return Projections.fields(
            InstanceDTO.class,
            instance.id.as(ID_FIELD),
            instance.instanceIdentification.as(INSTANCE_IDENTIFICATION_FIELD),
            instance.partner.id.as(PARTNER_ID_FIELD),
            instance.partner.fiscalCode.as(PARTNER_FISCAL_CODE_FIELD),
            instance.partner.name.as(PARTNER_NAME_FIELD),
            instance.applicationDate.as(APPLICATION_DATE_FIELD),
            instance.predictedDateAnalysis.as(PREDICTED_DATE_ANALYSIS_FIELD),
            instance.analysisPeriodStartDate.as(ANALYSIS_PERIOD_START_DATE_FIELD),
            instance.analysisPeriodEndDate.as(ANALYSIS_PERIOD_END_DATE_FIELD)
        );
    }

    @Override
    public void updateExecuteStateAndLastAnalysis(Long id, Instant lastAnalysisDate, AnalysisOutcome lastAnalysisOutcome, String currentUser) {
        LOGGER.debug("Request to update Instance {}", id);

        JPAUpdateClause jpql = queryBuilder.updateQuery(QInstance.instance);

        Instant now = Instant.now();
        
        jpql
            .set(QInstance.instance.status, InstanceStatus.ESEGUITA)
            .set(QInstance.instance.lastAnalysisDate, lastAnalysisDate)
            .set(QInstance.instance.lastAnalysisOutcome, lastAnalysisOutcome)
            .set(QInstance.instance.lastModifiedDate, now)
            .set(QInstance.instance.lastModifiedBy, currentUser)
            .where(QInstance.instance.id.eq(id))
            .execute();
    }

    @Override
    public void updateInstanceStatusInProgress(long id) {
        LOGGER.debug("Request to update status of instance {} to {}", id, InstanceStatus.IN_ESECUZIONE);

        JPAUpdateClause jpql = queryBuilder.updateQuery(QInstance.instance);

        jpql
            .set(QInstance.instance.status, InstanceStatus.IN_ESECUZIONE)
            .where(QInstance.instance.id.eq(id).and(QInstance.instance.status.eq(InstanceStatus.PIANIFICATA)))
            .execute();
    }

    @Override
    public void updateInstanceStatusError(Long id) {
        LOGGER.debug("Request to update status of instance {} to {}", id, InstanceStatus.ERRORE);

        JPAUpdateClause jpql = queryBuilder.updateQuery(QInstance.instance);

        jpql
            .set(QInstance.instance.status, InstanceStatus.ERRORE)
            .where(QInstance.instance.id.eq(id))
            .execute();
    }

    @Override
    public void updateInstanceStatusCompleted(Long id) {
        LOGGER.debug("Request to update status of instance {} to {}", id, InstanceStatus.ESEGUITA);

        JPAUpdateClause jpql = queryBuilder.updateQuery(QInstance.instance);

        jpql
            .set(QInstance.instance.status, InstanceStatus.ESEGUITA)
            .where(QInstance.instance.id.eq(id))
            .execute();
    }

    @Override
    public List<InstanceDTO> findActiveInstancesForModule(Long moduleId) {
        LOGGER.debug("Request to find active instances for module: {}", moduleId);
        
        List<Instance> instances = instanceRepository.findAll().stream()
            .filter(instance -> instance.getStatus().equals(InstanceStatus.PIANIFICATA))
            .filter(instance -> instance.getInstanceModules().stream()
                .anyMatch(im -> im.getModule().getId().equals(moduleId)))
            .toList();
                
        return instances.stream()
            .map(instanceMapper::toDto)
            .toList();
    }
}
