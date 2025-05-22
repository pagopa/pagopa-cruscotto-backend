package com.nexigroup.pagopa.cruscotto.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nexigroup.pagopa.cruscotto.domain.PagoPaTaxonomyAggregatePosition;
import com.nexigroup.pagopa.cruscotto.domain.QPagoPaTaxonomyAggregatePosition;
import com.nexigroup.pagopa.cruscotto.service.PagoPaTaxonomyAggregatePositionService;
import com.nexigroup.pagopa.cruscotto.service.dto.PagoPaTaxonomyAggregatePositionDTO;
import com.nexigroup.pagopa.cruscotto.service.qdsl.QueryBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

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
    	
        QPagoPaTaxonomyAggregatePosition qPagoPaTaxonomyAggregatePosition = QPagoPaTaxonomyAggregatePosition.pagoPaTaxonomyAggregatePosition;

        LocalDateTime startDateTime = day.atStartOfDay();
        LocalDateTime endDateTime = day.atTime(23, 59, 59, 0);

        LOGGER.info(startDateTime.toString());
        LOGGER.info(endDateTime.toString());

        return queryBuilder.createQueryFactory()
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
        				   .where(qPagoPaTaxonomyAggregatePosition.cfPartner.eq(fiscalCodePartner)
        						   .and(qPagoPaTaxonomyAggregatePosition.startDate.goe(startDateTime.atZone(ZoneOffset.systemDefault()).toInstant()))
        						   .and(qPagoPaTaxonomyAggregatePosition.startDate.loe(endDateTime.atZone(ZoneOffset.systemDefault()).toInstant()))
            )
            .orderBy(new OrderSpecifier<>(Order.ASC, Expressions.stringPath("startDate")))
            .fetch();
    }
}
