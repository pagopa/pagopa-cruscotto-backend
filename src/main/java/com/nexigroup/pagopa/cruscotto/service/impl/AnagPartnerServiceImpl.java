package com.nexigroup.pagopa.cruscotto.service.impl;


import com.nexigroup.pagopa.cruscotto.domain.AnagPartner;
import com.nexigroup.pagopa.cruscotto.domain.Instance;
import com.nexigroup.pagopa.cruscotto.domain.QAnagPartner;
import com.nexigroup.pagopa.cruscotto.repository.AnagPartnerRepository;
import com.nexigroup.pagopa.cruscotto.service.AnagPartnerService;
import com.nexigroup.pagopa.cruscotto.service.dto.AnagPartnerDTO;
import com.nexigroup.pagopa.cruscotto.service.mapper.AnagPartnerMapper;
import com.nexigroup.pagopa.cruscotto.service.qdsl.QdslUtility;
import com.nexigroup.pagopa.cruscotto.service.qdsl.QueryBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPQLQuery;
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
public class AnagPartnerServiceImpl implements AnagPartnerService {

    private final Logger log = LoggerFactory.getLogger(AnagPartnerServiceImpl.class);

    private final QueryBuilder queryBuilder;

    public AnagPartnerServiceImpl(QueryBuilder queryBuilder) {
        this.queryBuilder = queryBuilder;
    }

    /**
     * Get all the partner by filter.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Override
    public Page<AnagPartnerDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Instance");

        JPQLQuery<AnagPartner> jpql = queryBuilder.<AnagPartner>createQuery().from(QAnagPartner.anagPartner);

        long size = jpql.fetchCount();

        JPQLQuery<AnagPartnerDTO> jpqlSelected = jpql.select(
            Projections.fields(
                AnagPartnerDTO.class,
                QAnagPartner.anagPartner.id.as("id"),
                QAnagPartner.anagPartner.fiscalCode.as("fiscalCode"),
                QAnagPartner.anagPartner.name.as("name"),
                QAnagPartner.anagPartner.status.as("status")
            )
        );

        jpqlSelected.offset(pageable.getOffset());
        jpqlSelected.limit(pageable.getPageSize());

        pageable
            .getSortOr(Sort.by(Sort.Direction.ASC, "name"))
            .forEach(order -> {
                jpqlSelected.orderBy(
                    new OrderSpecifier<>(
                        order.isAscending() ? Order.ASC : Order.DESC,
                        Expressions.stringPath(order.getProperty()),
                        QdslUtility.toQueryDslNullHandling(order.getNullHandling())
                    )
                );
            });

        List<AnagPartnerDTO> list = jpqlSelected.fetch();

        return new PageImpl<>(list, pageable, size);
    }
}
