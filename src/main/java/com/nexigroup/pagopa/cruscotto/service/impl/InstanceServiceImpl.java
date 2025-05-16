package com.nexigroup.pagopa.cruscotto.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nexigroup.pagopa.cruscotto.domain.AnagPartner;
import com.nexigroup.pagopa.cruscotto.domain.AuthUser;
import com.nexigroup.pagopa.cruscotto.domain.Instance;
import com.nexigroup.pagopa.cruscotto.domain.InstanceModule;
import com.nexigroup.pagopa.cruscotto.domain.Module;
import com.nexigroup.pagopa.cruscotto.domain.QInstance;
import com.nexigroup.pagopa.cruscotto.domain.QInstanceModule;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.AnalysisOutcome;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.AnalysisType;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.InstanceStatus;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.ModuleCode;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.ModuleStatus;
import com.nexigroup.pagopa.cruscotto.repository.AnagPartnerRepository;
import com.nexigroup.pagopa.cruscotto.repository.InstanceRepository;
import com.nexigroup.pagopa.cruscotto.repository.ModuleRepository;
import com.nexigroup.pagopa.cruscotto.security.SecurityUtils;
import com.nexigroup.pagopa.cruscotto.service.GenericServiceException;
import com.nexigroup.pagopa.cruscotto.service.InstanceService;
import com.nexigroup.pagopa.cruscotto.service.bean.InstanceRequestBean;
import com.nexigroup.pagopa.cruscotto.service.dto.InstanceDTO;
import com.nexigroup.pagopa.cruscotto.service.filter.InstanceFilter;
import com.nexigroup.pagopa.cruscotto.service.mapper.InstanceMapper;
import com.nexigroup.pagopa.cruscotto.service.qdsl.QdslUtility;
import com.nexigroup.pagopa.cruscotto.service.qdsl.QueryBuilder;
import com.nexigroup.pagopa.cruscotto.service.util.UserUtils;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPQLQuery;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Service Implementation for managing {@link Instance}.
 */
@Service
@Transactional
public class InstanceServiceImpl implements InstanceService {

    private final Logger log = LoggerFactory.getLogger(InstanceServiceImpl.class);

    private static final String INSTANCE = "instance";

    private static final String CURRENT_USER_LOGIN_NOT_FOUND = "Current user login not found";

    static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final InstanceRepository instanceRepository;

    private final AnagPartnerRepository anagPartnerRepository;

    private final ModuleRepository moduleRepository;

    private final InstanceMapper instanceMapper;

    private final QueryBuilder queryBuilder;

    private final UserUtils userUtils;

    public InstanceServiceImpl(
        InstanceRepository instanceRepository,
        AnagPartnerRepository anagPartnerRepository,
        ModuleRepository moduleRepository,
        InstanceMapper instanceMapper,
        QueryBuilder queryBuilder,
        UserUtils userUtils
    ) {
        this.instanceRepository = instanceRepository;
        this.anagPartnerRepository = anagPartnerRepository;
        this.moduleRepository = moduleRepository;
        this.instanceMapper = instanceMapper;
        this.queryBuilder = queryBuilder;
        this.userUtils = userUtils;
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
        log.debug("Request to get all Instance by filter: {}", filter);

        BooleanBuilder builder = new BooleanBuilder();

        if (StringUtils.isNotBlank(filter.getPartnerId())) {
            builder.and(QInstance.instance.partner.id.eq(Long.valueOf(filter.getPartnerId())));
        }

        JPQLQuery<Instance> jpql = queryBuilder.<Instance>createQuery().from(QInstance.instance).where(builder);

        long size = jpql.fetchCount();

        JPQLQuery<InstanceDTO> jpqlSelected = jpql.select(
            Projections.fields(
                InstanceDTO.class,
                QInstance.instance.id.as("id"),
                QInstance.instance.instanceIdentification.as("instanceIdentification"),
                QInstance.instance.partner.id.as("partnerId"),
                QInstance.instance.partner.name.as("partnerName"),
                QInstance.instance.applicationDate.as("applicationDate"),
                QInstance.instance.predictedDateAnalysis.as("predictedDateAnalysis"),
                QInstance.instance.assignedUser.id.as("assignedUserId"),
                QInstance.instance.assignedUser.firstName.as("assignedFirstName"),
                QInstance.instance.assignedUser.lastName.as("assignedLastName"),
                QInstance.instance.analysisPeriodStartDate.as("analysisPeriodStartDate"),
                QInstance.instance.analysisPeriodEndDate.as("analysisPeriodEndDate"),
                QInstance.instance.status.as("status"),
                QInstance.instance.lastAnalysisDate.as("lastAnalysisDate")
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

        log.info("Creation of instance with identification {} by user {}", instance.getInstanceIdentification(), loggedUser.getLogin());

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

                instanceRepository.save(instance);

                log.info(
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
                if (!instance.getStatus().equals(InstanceStatus.BOZZA) && !instance.getStatus().equals(InstanceStatus.PIANIFICATA)) {
                    throw new GenericServiceException(
                        String.format("Instance with id %s cannot be deleted because it is in %s status", id, instance.getStatus()),
                        INSTANCE,
                        "instance.cannotBeDeleted"
                    );
                }

                String loginUtenteLoggato = SecurityUtils.getCurrentUserLogin()
                    .orElseThrow(() -> new RuntimeException(CURRENT_USER_LOGIN_NOT_FOUND));

                instanceRepository.deleteById(id);

                log.info(
                    "Physical deleting of instance with identification {} by user {}",
                    instance.getInstanceIdentification(),
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
    public List<InstanceDTO> findInstanceToCalculate(ModuleCode moduleCode, Integer limit) {
        JPQLQuery<InstanceDTO> jpql = queryBuilder
            .<Instance>createQuery()
            .from(QInstance.instance)
            .leftJoin(QInstance.instance.instanceModules, QInstanceModule.instanceModule)
            .where(
                QInstance.instance.status
                    .eq(InstanceStatus.PIANIFICATA)
                    .and(QInstance.instance.predictedDateAnalysis.loe(LocalDate.now()))
                    .and(QInstanceModule.instanceModule.moduleCode.eq(moduleCode.code))
                    .and(QInstanceModule.instanceModule.analysisType.eq(AnalysisType.AUTOMATICA))
                    .and(QInstanceModule.instanceModule.status.eq(ModuleStatus.ATTIVO))
                    .and(QInstanceModule.instanceModule.automaticOutcomeDate.isNull())
            )
            .select(
                Projections.fields(
                    InstanceDTO.class,
                    QInstance.instance.id.as("id"),
                    QInstance.instance.instanceIdentification.as("instanceIdentification"),
                    QInstance.instance.partner.id.as("partnerId"),
                    QInstance.instance.partner.fiscalCode.as("partnerFiscalCode"),
                    QInstance.instance.partner.name.as("partnerName"),
                    QInstance.instance.applicationDate.as("applicationDate"),
                    QInstance.instance.predictedDateAnalysis.as("predictedDateAnalysis"),
                    QInstance.instance.analysisPeriodStartDate.as("analysisPeriodStartDate"),
                    QInstance.instance.analysisPeriodEndDate.as("analysisPeriodEndDate")
                )
            )
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

                log.info(
                    "Updating status of instance with identifier {} in {} by user {}",
                    instance.getInstanceIdentification(),
                    loginUtenteLoggato,
                    instance.getStatus()
                );

                return instance;
            })
            .map(instanceMapper::toDto)
            .orElseThrow(() ->
                new GenericServiceException(String.format("Instance with id %s not exist", id), INSTANCE, "instance.notExists")
            );
    }
}
