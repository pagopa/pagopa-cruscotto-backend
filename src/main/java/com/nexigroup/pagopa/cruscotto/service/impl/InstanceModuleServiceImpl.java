package com.nexigroup.pagopa.cruscotto.service.impl;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nexigroup.pagopa.cruscotto.domain.AnagStation;
import com.nexigroup.pagopa.cruscotto.domain.AuthUser;
import com.nexigroup.pagopa.cruscotto.domain.Instance;
import com.nexigroup.pagopa.cruscotto.domain.InstanceModule;
import com.nexigroup.pagopa.cruscotto.domain.KpiB2DetailResult;
import com.nexigroup.pagopa.cruscotto.domain.QInstanceModule;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.AnalysisOutcome;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.AnalysisType;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.InstanceStatus;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.ModuleStatus;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import com.nexigroup.pagopa.cruscotto.repository.InstanceModuleRepository;
import com.nexigroup.pagopa.cruscotto.service.InstanceModuleService;
import com.nexigroup.pagopa.cruscotto.service.dto.InstanceModuleDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB2DetailResultDTO;
import com.nexigroup.pagopa.cruscotto.service.qdsl.QueryBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.QBean;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPQLQuery;

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

    @Override
    public Optional<InstanceModule> findById(Long id) {
        return instanceModuleRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<InstanceModuleDTO> findInstanceModuleDTOById(Long id) {
        JPQLQuery<InstanceModule> jpql = queryBuilder
            .<InstanceModule>createQuery()
            .from(QInstanceModule.instanceModule)
            .leftJoin(QInstanceModule.instanceModule.manualOutcomeUser)
            .where(QInstanceModule.instanceModule.id.eq(id));

        return Optional.ofNullable(
            jpql
                .select(
                    Projections.fields(
                        InstanceModuleDTO.class,
                        QInstanceModule.instanceModule.id.as("id"),
                        QInstanceModule.instanceModule.moduleCode.as("moduleCode"),
                        QInstanceModule.instanceModule.analysisType.as("analysisType"),
                        QInstanceModule.instanceModule.allowManualOutcome.as("allowManualOutcome"),
                        QInstanceModule.instanceModule.automaticOutcomeDate.as("automaticOutcomeDate"),
                        QInstanceModule.instanceModule.automaticOutcome.as("automaticOutcome"),
                        QInstanceModule.instanceModule.manualOutcome.as("manualOutcome"),
                        QInstanceModule.instanceModule.manualOutcomeDate.as("manualOutcomeDate"),
                        QInstanceModule.instanceModule.status.as("status"),
                        QInstanceModule.instanceModule.manualOutcomeUser.id.as("assignedUserId"),
                        QInstanceModule.instanceModule.manualOutcomeUser.firstName.as("assignedUserFirstName"),
                        QInstanceModule.instanceModule.manualOutcomeUser.lastName.as("assignedUserLastName")
                    )
                )
                .fetchOne()
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
        kpiB2DetailResult.setEvaluationType(kpiB2DetailResultDTO.getEvaluationType());
        kpiB2DetailResult.setEvaluationStartDate(kpiB2DetailResultDTO.getEvaluationStartDate());
        kpiB2DetailResult.setEvaluationEndDate(kpiB2DetailResultDTO.getEvaluationEndDate());
        kpiB2DetailResult.setTotReq(kpiB2DetailResultDTO.getTotReq());
        kpiB2DetailResult.setAvgTime(kpiB2DetailResultDTO.getAvgTime());
        kpiB2DetailResult.setOverTimeLimit(kpiB2DetailResultDTO.getOverTimeLimit());
        kpiB2DetailResult.setOutcome(kpiB2DetailResultDTO.getOutcome());

        return kpiB2DetailResult;
    }
/*
	@Override
	public void updateStatusAndAllowManualOutcome(Long instanceModuleId, ModuleStatus status, Boolean allowManualOutcome) {
        InstanceModule instanceModule = instanceModuleRepository
                .findById(instanceModuleId)
                .orElseThrow(() -> new IllegalArgumentException("InstanceModule not found"));
        	if (InstanceStatus.BOZZA == instanceModule.getInstance().getStatus()) {
        		instanceModule.setStatus(status);
                instanceModule.setAllowManualOutcome(allowManualOutcome);
                instanceModule.setLastModifiedDate(Instant.now());
                instanceModuleRepository.save(instanceModule);	
        	}
            
		
	}*/

	/**
	 * Updates an InstanceModule with patch-style updates (only provided fields are modified).
	 * This overloaded version accepts an AuthUser parameter to set the manualOutcomeUser when updating manualOutcome.
	 * Business rules:
	 * - status and allowManualOutcome can only be updated when instance is in BOZZA status
	 * - manualOutcome can only be updated when allowManualOutcome is true AND analysisType is AUTOMATICA
	 * 
	 * @param instanceModuleDTO the DTO containing the fields to update
	 * @param currentUser the user performing the update (used for manualOutcomeUser assignment)
	 * @return the updated InstanceModuleDTO reflecting the current state
	 * @throws IllegalArgumentException if InstanceModule is not found
	 * @throws RuntimeException if business rules are violated
	 */
	@Override
	public InstanceModuleDTO updateInstanceModule(InstanceModuleDTO instanceModuleDTO, AuthUser currentUser) {
		// Retrieve the existing entity
		InstanceModule instanceModule = instanceModuleRepository
                .findById(instanceModuleDTO.getId())
                .orElseThrow(() -> new IllegalArgumentException("InstanceModule not found"));
		
		// Check if the parent instance is in draft status
		boolean isInstanceInDraft = InstanceStatus.BOZZA == instanceModule.getInstance().getStatus();
		boolean hasChanges = false;
		
		// Store original allowManualOutcome value for manualOutcome validation
		boolean originalAllowManualOutcome = instanceModule.isAllowManualOutcome();
		
		// Update status if provided and instance is in draft
		if (instanceModuleDTO.getStatus() != null && instanceModule.getStatus() != instanceModuleDTO.getStatus()) {
			if (!isInstanceInDraft) {
				throw new RuntimeException("Cannot update status: instance is not in BOZZA status");
			}
			instanceModule.setStatus(instanceModuleDTO.getStatus());
			hasChanges = true;
		}
		
		// Update allowManualOutcome if provided and instance is in draft
		if (instanceModuleDTO.getAllowManualOutcome() != null && instanceModule.isAllowManualOutcome() != instanceModuleDTO.getAllowManualOutcome()) {
			if (!isInstanceInDraft) {
				throw new RuntimeException("Cannot update allowManualOutcome: instance is not in BOZZA status");
			}
			instanceModule.setAllowManualOutcome(instanceModuleDTO.getAllowManualOutcome());
			hasChanges = true;
		}
		
		// Update manualOutcome if provided and conditions are met
		if (instanceModuleDTO.getManualOutcome() != null && instanceModule.getManualOutcome() != instanceModuleDTO.getManualOutcome()) {
			if (!originalAllowManualOutcome) {
				throw new RuntimeException("Cannot update manualOutcome: allowManualOutcome is false");
			}
			/*
			if (AnalysisType.AUTOMATICA != instanceModule.getAnalysisType()) {
				throw new RuntimeException("Cannot update manualOutcome: analysisType is not AUTOMATICA");
			}*/
			instanceModule.setManualOutcome(instanceModuleDTO.getManualOutcome());
			instanceModule.setManualOutcomeDate(Instant.now());
			// Set the current user as manualOutcomeUser when updating manual outcome
			instanceModule.setManualOutcomeUser(currentUser);
			hasChanges = true;
		}
		
		// Save only if there were changes
		if (hasChanges) {
			instanceModule.setLastModifiedDate(Instant.now());
			instanceModuleRepository.save(instanceModule);
		}
		
		// Build and return the updated DTO without re-querying the database
		InstanceModuleDTO result = new InstanceModuleDTO();
		result.setId(instanceModule.getId());
		result.setInstanceId(instanceModule.getInstance().getId());
		result.setModuleId(instanceModule.getModule().getId());
		result.setModuleCode(instanceModule.getModuleCode());
		result.setAnalysisType(instanceModule.getAnalysisType());
		result.setAllowManualOutcome(instanceModule.isAllowManualOutcome());
		result.setAutomaticOutcome(instanceModule.getAutomaticOutcome());
		result.setAutomaticOutcomeDate(instanceModule.getAutomaticOutcomeDate());
		result.setManualOutcome(instanceModule.getManualOutcome());
		result.setManualOutcomeDate(instanceModule.getManualOutcomeDate());
		result.setStatus(instanceModule.getStatus());
		// Set user information if available
		if (instanceModule.getManualOutcomeUser() != null) {
			result.setAssignedUserId(instanceModule.getManualOutcomeUser().getId());
			result.setAssignedUserFirstName(instanceModule.getManualOutcomeUser().getFirstName());
			result.setAssignedUserLastName(instanceModule.getManualOutcomeUser().getLastName());
		}
		
		return result;
	}
}
