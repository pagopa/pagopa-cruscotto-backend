package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.PagoPaTaxonomyAggregatePosition;
import com.nexigroup.pagopa.cruscotto.domain.QPagoPaTaxonomyAggregatePosition;
import com.nexigroup.pagopa.cruscotto.service.PagoPaTaxonomyAggregatePositionService;
import com.nexigroup.pagopa.cruscotto.service.dto.PagoPaTaxonomyAggregatePositionDTO;
import com.nexigroup.pagopa.cruscotto.service.filter.PagoPaTaxonomyAggregatePositionFilter;
import com.nexigroup.pagopa.cruscotto.service.qdsl.QdslUtility;
import com.nexigroup.pagopa.cruscotto.service.qdsl.QueryBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import com.querydsl.jpa.JPQLQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link PagoPaTaxonomyAggregatePosition}.
 */

@Service
@Transactional
public class PagoPaTaxonomyAggregatePositionServiceImpl implements PagoPaTaxonomyAggregatePositionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PagoPaTaxonomyAggregatePositionServiceImpl.class);

    private final QueryBuilder queryBuilder;

    public PagoPaTaxonomyAggregatePositionServiceImpl(QueryBuilder queryBuilder) {
        this.queryBuilder = queryBuilder;
    }

    @Override
    public List<PagoPaTaxonomyAggregatePositionDTO> findAllRecordIntoDayForPartner(String fiscalCodePartner, LocalDate day) {
        QPagoPaTaxonomyAggregatePosition qPagoPaTaxonomyAggregatePosition =
            QPagoPaTaxonomyAggregatePosition.pagoPaTaxonomyAggregatePosition;

        LocalDateTime startDateTime = day.atStartOfDay();
        LocalDateTime endDateTime = day.atTime(23, 59, 59, 0);

        LOGGER.info(startDateTime.toString());
        LOGGER.info(endDateTime.toString());

        return queryBuilder
            .createQueryFactory()
            .select(
                Projections.fields(
                    PagoPaTaxonomyAggregatePositionDTO.class,
                    qPagoPaTaxonomyAggregatePosition.id.as("id"),
                    qPagoPaTaxonomyAggregatePosition.cfPartner.as("cfPartner"),
                    qPagoPaTaxonomyAggregatePosition.station.as("station"),
                    qPagoPaTaxonomyAggregatePosition.transferCategory.as("transferCategory"),
                    qPagoPaTaxonomyAggregatePosition.startDate.as("startDate"),
                    qPagoPaTaxonomyAggregatePosition.endDate.as("endDate"),
                    qPagoPaTaxonomyAggregatePosition.total.as("total")
                )
            )
            .from(qPagoPaTaxonomyAggregatePosition)
            .where(
                qPagoPaTaxonomyAggregatePosition.cfPartner
                    .eq(fiscalCodePartner)
                    .and(qPagoPaTaxonomyAggregatePosition.startDate.goe(startDateTime.atZone(ZoneOffset.systemDefault()).toInstant()))
                    .and(qPagoPaTaxonomyAggregatePosition.startDate.loe(endDateTime.atZone(ZoneOffset.systemDefault()).toInstant()))
            )
            .orderBy(new OrderSpecifier<>(Order.ASC, Expressions.stringPath("startDate")))
            .fetch();
    }

    /**
     * Get all the pagopa taxonomy aggregate position
     *
     * @param pageable the pagination information.
     * @return the list of pagopa taxonomy aggregate position.
     */
    @Override
    public Page<PagoPaTaxonomyAggregatePositionDTO> findAll(PagoPaTaxonomyAggregatePositionFilter filter, Pageable pageable) {
        LOGGER.debug("Request to get all pagopa taxonomy aggregate positions by filter {}", filter);

        QPagoPaTaxonomyAggregatePosition qPagoPaTaxonomyAggregatePosition = QPagoPaTaxonomyAggregatePosition.pagoPaTaxonomyAggregatePosition;

        JPQLQuery<PagoPaTaxonomyAggregatePosition> query = queryBuilder
            .<PagoPaTaxonomyAggregatePosition>createQuery()
            .from(qPagoPaTaxonomyAggregatePosition);

        if(filter.getCfPartner() != null) {
            query.where(qPagoPaTaxonomyAggregatePosition.cfPartner.eq(filter.getCfPartner()));
        }

        if(filter.getStation() != null) {
            query.where(qPagoPaTaxonomyAggregatePosition.station.eq(filter.getStation()));
        }

        long total = query.fetchCount();

        JPQLQuery<PagoPaTaxonomyAggregatePositionDTO> jpqlQuery = query.select(createPagoPaTaxonomyAggregatePositionProjection());

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

        List<PagoPaTaxonomyAggregatePositionDTO> results = jpqlQuery.fetch();

        return new PageImpl<>(results, pageable, total);
    }

    private com.querydsl.core.types.Expression<PagoPaTaxonomyAggregatePositionDTO> createPagoPaTaxonomyAggregatePositionProjection() {
        return Projections.fields(
            PagoPaTaxonomyAggregatePositionDTO.class,
            QPagoPaTaxonomyAggregatePosition.pagoPaTaxonomyAggregatePosition.id.as("id"),
            QPagoPaTaxonomyAggregatePosition.pagoPaTaxonomyAggregatePosition.cfPartner.as("cfPartner"),
            QPagoPaTaxonomyAggregatePosition.pagoPaTaxonomyAggregatePosition.station.as("station"),
            QPagoPaTaxonomyAggregatePosition.pagoPaTaxonomyAggregatePosition.transferCategory.as("transferCategory"),
            QPagoPaTaxonomyAggregatePosition.pagoPaTaxonomyAggregatePosition.startDate.as("startDate"),
            QPagoPaTaxonomyAggregatePosition.pagoPaTaxonomyAggregatePosition.endDate.as("endDate"),
            QPagoPaTaxonomyAggregatePosition.pagoPaTaxonomyAggregatePosition.total.as("total"));
    }

}
