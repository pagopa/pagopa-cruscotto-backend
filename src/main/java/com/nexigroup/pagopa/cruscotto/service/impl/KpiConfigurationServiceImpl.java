package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.*;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.ModuleCode;
import com.nexigroup.pagopa.cruscotto.service.KpiConfigurationService;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiConfigurationDTO;
import com.nexigroup.pagopa.cruscotto.service.qdsl.QueryBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPQLQuery;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link KpiConfiguration}.
 */
@Service
@Transactional
public class KpiConfigurationServiceImpl implements KpiConfigurationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(KpiConfigurationServiceImpl.class);

    private final QueryBuilder queryBuilder;

    public KpiConfigurationServiceImpl(QueryBuilder queryBuilder) {
        this.queryBuilder = queryBuilder;
    }

    /**
     * Get configuration for a module.
     *
     * @param code the code of module.
     * @return the entity kpiConfiguration.
     */
    @Override
    public Optional<KpiConfigurationDTO> findKpiConfigurationByCode(ModuleCode code) {
        QKpiConfiguration qKpiConfiguration = QKpiConfiguration.kpiConfiguration;

        JPQLQuery<KpiConfiguration> jpql = queryBuilder
            .<KpiConfiguration>createQuery()
            .from(qKpiConfiguration)
            .leftJoin(qKpiConfiguration.module, QModule.module)
            .where(qKpiConfiguration.module.code.eq(code.code));

        JPQLQuery<KpiConfigurationDTO> jpqlResponse = jpql.select(
            Projections.fields(
                KpiConfigurationDTO.class, //
                qKpiConfiguration.id.as("id"),
                qKpiConfiguration.module.id.as("moduleId"),
                qKpiConfiguration.module.code.as("moduleCode"),
                qKpiConfiguration.excludePlannedShutdown.as("excludePlannedShutdown"),
                qKpiConfiguration.excludeUnplannedShutdown.as("excludeUnplannedShutdown"),
                qKpiConfiguration.eligibilityThreshold.as("eligibilityThreshold"),
                qKpiConfiguration.tollerance.as("tollerance"),
                qKpiConfiguration.averageTimeLimit.as("averageTimeLimit"),
                qKpiConfiguration.evaluationType.as("evaluationType")
            )
        );
        return Optional.ofNullable(jpqlResponse.fetchOne());
    }
}
