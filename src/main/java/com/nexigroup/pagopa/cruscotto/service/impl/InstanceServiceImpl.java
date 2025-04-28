package com.nexigroup.pagopa.cruscotto.service.impl;


import com.nexigroup.pagopa.cruscotto.domain.AuthFunction;
import com.nexigroup.pagopa.cruscotto.domain.Instance;
import com.nexigroup.pagopa.cruscotto.domain.QAuthFunction;
import com.nexigroup.pagopa.cruscotto.domain.QInstance;
import com.nexigroup.pagopa.cruscotto.repository.InstanceRepository;
import com.nexigroup.pagopa.cruscotto.service.InstanceService;
import com.nexigroup.pagopa.cruscotto.service.dto.AuthFunctionDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.InstanceDTO;
import com.nexigroup.pagopa.cruscotto.service.filter.InstanceFilter;
import com.nexigroup.pagopa.cruscotto.service.mapper.InstanceMapper;
import com.nexigroup.pagopa.cruscotto.service.qdsl.QdslUtility;
import com.nexigroup.pagopa.cruscotto.service.qdsl.QueryBuilder;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPQLQuery;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/**
 * Service Implementation for managing {@link Instance}.
 */
@Service
@Transactional
public class InstanceServiceImpl implements InstanceService {

    private final Logger log = LoggerFactory.getLogger(InstanceServiceImpl.class);

    private final InstanceRepository instanceRepository;

    private final InstanceMapper instanceMapper;

    private final QueryBuilder queryBuilder;

    public InstanceServiceImpl(InstanceRepository instanceRepository, InstanceMapper instanceMapper, QueryBuilder queryBuilder) {
        this.instanceRepository = instanceRepository;
        this.instanceMapper = instanceMapper;
        this.queryBuilder = queryBuilder;
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
}
