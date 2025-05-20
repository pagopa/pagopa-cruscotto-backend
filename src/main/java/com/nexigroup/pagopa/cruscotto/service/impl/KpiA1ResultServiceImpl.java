package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.*;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import com.nexigroup.pagopa.cruscotto.repository.InstanceModuleRepository;
import com.nexigroup.pagopa.cruscotto.repository.InstanceRepository;
import com.nexigroup.pagopa.cruscotto.repository.KpiA1ResultRepository;
import com.nexigroup.pagopa.cruscotto.service.KpiA1ResultService;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiA1ResultDTO;
import com.nexigroup.pagopa.cruscotto.service.qdsl.QueryBuilder;
import com.querydsl.jpa.impl.JPAUpdateClause;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing {@link KpiB2Result}.
 */
@Service
@Transactional
public class KpiA1ResultServiceImpl implements KpiA1ResultService {

    private static final Logger LOGGER = LoggerFactory.getLogger(KpiA1ResultServiceImpl.class);

    private final InstanceRepository instanceRepository;

    private final InstanceModuleRepository instanceModuleRepository;

    private final KpiA1ResultRepository kpiA1ResultRepository;

    private final QueryBuilder queryBuilder;


    public KpiA1ResultServiceImpl(InstanceRepository instanceRepository, InstanceModuleRepository instanceModuleRepository,
                                  KpiA1ResultRepository kpiA1ResultRepository, QueryBuilder queryBuilder) {
        this.instanceRepository = instanceRepository;
        this.instanceModuleRepository = instanceModuleRepository;
        this.kpiA1ResultRepository = kpiA1ResultRepository;
        this.queryBuilder = queryBuilder;
    }

    /**
     * Save kpiB2Result.
     *
     * @param kpiA1ResultDTO the entity to save.
     */
    @Override
    public KpiA1ResultDTO save(KpiA1ResultDTO kpiA1ResultDTO) {
        Instance instance = instanceRepository.findById(kpiA1ResultDTO.getInstanceId())
                							  .orElseThrow(() -> new IllegalArgumentException("Instance not found"));

    	InstanceModule instanceModule = instanceModuleRepository.findById(kpiA1ResultDTO.getInstanceModuleId())
                												.orElseThrow(() -> new IllegalArgumentException("InstanceModule not found"));

        KpiA1Result kpiA1Result = getKpiA1Result(kpiA1ResultDTO, instance, instanceModule);

        kpiA1Result = kpiA1ResultRepository.save(kpiA1Result);

        kpiA1ResultDTO.setId(kpiA1Result.getId());

        return kpiA1ResultDTO;
    }

    private static @NotNull KpiA1Result getKpiA1Result(KpiA1ResultDTO kpiA1ResultDTO, Instance instance, InstanceModule instanceModule) {

        KpiA1Result kpiA1Result = new KpiA1Result();
        kpiA1Result.setInstance(instance);
        kpiA1Result.setInstanceModule(instanceModule);
        kpiA1Result.setAnalysisDate(kpiA1ResultDTO.getAnalysisDate());
        kpiA1Result.setExcludePlannedShutdown(kpiA1ResultDTO.getExcludePlannedShutdown());
        kpiA1Result.setExcludeUnplannedShutdown(kpiA1ResultDTO.getExcludeUnplannedShutdown());
        kpiA1Result.setEligibilityThreshold(kpiA1ResultDTO.getEligibilityThreshold());
        kpiA1Result.setTollerance(kpiA1ResultDTO.getTollerance());
        kpiA1Result.setEvaluationType(kpiA1ResultDTO.getEvaluationType());
        kpiA1Result.setOutcome(kpiA1ResultDTO.getOutcome());

        return kpiA1Result;
    }

    private static @NotNull KpiA1ResultDTO getKpiA1ResultDTO(KpiA1Result kpiA1Result) {
        KpiA1ResultDTO kpiA1ResultDTO = new KpiA1ResultDTO();
        kpiA1ResultDTO.setId(kpiA1Result.getId());
        kpiA1ResultDTO.setInstanceModuleId((kpiA1Result.getInstanceModule().getId()));
        kpiA1ResultDTO.setAnalysisDate(kpiA1Result.getAnalysisDate());
        kpiA1ResultDTO.setExcludePlannedShutdown(kpiA1Result.getExcludePlannedShutdown());
        kpiA1ResultDTO.setExcludeUnplannedShutdown(kpiA1Result.getExcludeUnplannedShutdown());
        kpiA1ResultDTO.setEligibilityThreshold(kpiA1Result.getEligibilityThreshold());
        kpiA1ResultDTO.setTollerance(kpiA1Result.getTollerance());
        kpiA1ResultDTO.setEvaluationType(kpiA1Result.getEvaluationType());
        kpiA1ResultDTO.setOutcome(kpiA1Result.getOutcome());
        return kpiA1ResultDTO;
    }

	@Override
	public int deleteAllByInstanceModule(long instanceModuleId) {
		return kpiA1ResultRepository.deleteAllByInstanceModuleId(instanceModuleId);
	}

	@Override
	public void updateKpiA1ResultOutcome(long id, OutcomeStatus outcomeStatus) {
		LOGGER.debug("Request to get all AnagPartner");

		JPAUpdateClause jpql = queryBuilder.updateQuery(QKpiB2Result.kpiB2Result);

		jpql.set(QKpiB2Result.kpiB2Result.outcome, outcomeStatus)
			.where(QKpiB2Result.kpiB2Result.id.eq(id))
			.execute();
	}

    @Override
    public List<KpiA1ResultDTO> findByInstanceModuleId(long instanceModuleId) {
        return kpiA1ResultRepository.selectByInstanceModuleId(instanceModuleId)
            .stream()
            .map(KpiA1ResultServiceImpl::getKpiA1ResultDTO)
            .collect(Collectors.toList());
    }
}
