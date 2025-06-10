package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.PagoPaRecordedTimeout;
import com.nexigroup.pagopa.cruscotto.domain.QPagoPaRecordedTimeout;
import com.nexigroup.pagopa.cruscotto.service.PagoPaRecordedTimeoutService;
import com.nexigroup.pagopa.cruscotto.service.dto.PagoPaRecordedTimeoutDTO;
import com.nexigroup.pagopa.cruscotto.service.qdsl.QdslUtility;
import com.nexigroup.pagopa.cruscotto.service.qdsl.QueryBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.querydsl.jpa.JPQLQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link PagoPaRecordedTimeout}.
 */
@Service
@Transactional
public class PagoPaRecordedTimeoutServiceImpl implements PagoPaRecordedTimeoutService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PagoPaRecordedTimeoutServiceImpl.class);

    private final QueryBuilder queryBuilder;

    public PagoPaRecordedTimeoutServiceImpl(QueryBuilder queryBuilder) {
        this.queryBuilder = queryBuilder;
    }

    /**
     * Get station and method into a period for a partner.
     *
     * @param fiscalCodePartner the fiscal code of a partner.
     * @param startDate the start date of the period.
     * @param endDate the end date of the period.
     * @return the map contains Station and list method.
     */
    @Override
    public Map<String, List<String>> findAllStationAndMethodIntoPeriodForPartner(
        String fiscalCodePartner,
        LocalDate startDate,
        LocalDate endDate
    ) {
        QPagoPaRecordedTimeout qPagoPaRecordedTimeout = QPagoPaRecordedTimeout.pagoPaRecordedTimeout;

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59, 0);

        List<Tuple> results = queryBuilder
            .createQueryFactory()
            .select(qPagoPaRecordedTimeout.station, qPagoPaRecordedTimeout.method)
            .from(qPagoPaRecordedTimeout)
            .where(
                qPagoPaRecordedTimeout.cfPartner
                    .eq(fiscalCodePartner)
                    .and(qPagoPaRecordedTimeout.startDate.goe(startDateTime.atZone(ZoneOffset.systemDefault()).toInstant()))
                    .and(qPagoPaRecordedTimeout.endDate.loe(endDateTime.atZone(ZoneOffset.systemDefault()).toInstant()))
            )
            .groupBy(qPagoPaRecordedTimeout.station, qPagoPaRecordedTimeout.method)
            .orderBy(qPagoPaRecordedTimeout.station.asc(), qPagoPaRecordedTimeout.method.asc())
            .fetch();

        Map<String, List<String>> groupedResults = new HashMap<>();

        for (Tuple tuple : results) {
            String station = tuple.get(qPagoPaRecordedTimeout.station);
            String method = tuple.get(qPagoPaRecordedTimeout.method);
            groupedResults.computeIfAbsent(station, k -> new ArrayList<>()).add(method);
        }

        return groupedResults;
    }

    @Override
    public Long sumRecordIntoPeriodForPartnerStationAndMethod(
        String fiscalCodePartner,
        String station,
        String method,
        LocalDate startDate,
        LocalDate endDate
    ) {
        QPagoPaRecordedTimeout qPagoPaRecordedTimeout = QPagoPaRecordedTimeout.pagoPaRecordedTimeout;

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59, 0);

        LOGGER.info(startDateTime.toString());
        LOGGER.info(endDateTime.toString());

        return queryBuilder
            .createQueryFactory()
            .select(qPagoPaRecordedTimeout.totReq.sum())
            .from(qPagoPaRecordedTimeout)
            .where(
                qPagoPaRecordedTimeout.cfPartner
                    .eq(fiscalCodePartner)
                    .and(qPagoPaRecordedTimeout.station.eq(station))
                    .and(qPagoPaRecordedTimeout.method.eq(method))
                    .and(qPagoPaRecordedTimeout.startDate.goe(startDateTime.atZone(ZoneOffset.systemDefault()).toInstant()))
                    .and(qPagoPaRecordedTimeout.startDate.loe(endDateTime.atZone(ZoneOffset.systemDefault()).toInstant()))
            )
            .fetchOne();
    }

    @Override
    public List<PagoPaRecordedTimeoutDTO> findAllRecordIntoDayForPartnerStationAndMethod(
        String fiscalCodePartner,
        String station,
        String method,
        LocalDate day
    ) {
        QPagoPaRecordedTimeout qPagoPaRecordedTimeout = QPagoPaRecordedTimeout.pagoPaRecordedTimeout;

        LocalDateTime startDateTime = day.atStartOfDay();
        LocalDateTime endDateTime = day.atTime(23, 59, 59, 0);

        LOGGER.info(startDateTime.toString());
        LOGGER.info(endDateTime.toString());

        return queryBuilder
            .createQueryFactory()
            .select(
                Projections.fields(
                    PagoPaRecordedTimeoutDTO.class,
                    qPagoPaRecordedTimeout.id.as("id"),
                    qPagoPaRecordedTimeout.cfPartner.as("cfPartner"),
                    qPagoPaRecordedTimeout.station.as("station"),
                    qPagoPaRecordedTimeout.method.as("method"),
                    qPagoPaRecordedTimeout.startDate.as("startDate"),
                    qPagoPaRecordedTimeout.endDate.as("endDate"),
                    qPagoPaRecordedTimeout.totReq.as("totReq"),
                    qPagoPaRecordedTimeout.reqOk.as("reqOk"),
                    qPagoPaRecordedTimeout.reqTimeout.as("reqTimeout"),
                    qPagoPaRecordedTimeout.avgTime.as("avgTime")
                )
            )
            .from(qPagoPaRecordedTimeout)
            .where(
                qPagoPaRecordedTimeout.cfPartner
                    .eq(fiscalCodePartner)
                    .and(qPagoPaRecordedTimeout.station.eq(station))
                    .and(qPagoPaRecordedTimeout.method.eq(method))
                    .and(qPagoPaRecordedTimeout.startDate.goe(startDateTime.atZone(ZoneOffset.systemDefault()).toInstant()))
                    .and(qPagoPaRecordedTimeout.startDate.loe(endDateTime.atZone(ZoneOffset.systemDefault()).toInstant()))
            )
            .orderBy(new OrderSpecifier<>(Order.ASC, Expressions.stringPath("startDate")))
            .fetch();
    }

    /**
     * Get all the pagopa recorded timeout
     *
     * @param pageable the pagination information.
     * @return the list of pagopa recorded timeout.
     */
    @Override
    public Page<PagoPaRecordedTimeoutDTO> findAll(Pageable pageable) {
        LOGGER.debug("Request to get all pagopa recorded timeout");

        QPagoPaRecordedTimeout qPagoPaRecordedTimeout = QPagoPaRecordedTimeout.pagoPaRecordedTimeout;

        JPQLQuery<PagoPaRecordedTimeout> query = queryBuilder
            .<PagoPaRecordedTimeout>createQuery()
            .from(qPagoPaRecordedTimeout);

        long total = query.fetchCount();

        JPQLQuery<PagoPaRecordedTimeoutDTO> jpqlQuery = query.select(createPagoPaRecordedTimeoutProjection());

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

        List<PagoPaRecordedTimeoutDTO> results = jpqlQuery.fetch();

        return new PageImpl<>(results, pageable, total);
    }

    private com.querydsl.core.types.Expression<PagoPaRecordedTimeoutDTO> createPagoPaRecordedTimeoutProjection() {
        return Projections.fields(
            PagoPaRecordedTimeoutDTO.class,
            QPagoPaRecordedTimeout.pagoPaRecordedTimeout.id.as("id"),
            QPagoPaRecordedTimeout.pagoPaRecordedTimeout.cfPartner.as("cfPartner"),
            QPagoPaRecordedTimeout.pagoPaRecordedTimeout.station.as("station"),
            QPagoPaRecordedTimeout.pagoPaRecordedTimeout.method.as("method"),
            QPagoPaRecordedTimeout.pagoPaRecordedTimeout.startDate.as("startDate"),
            QPagoPaRecordedTimeout.pagoPaRecordedTimeout.endDate.as("endDate"),
            QPagoPaRecordedTimeout.pagoPaRecordedTimeout.totReq.as("totReq"),
            QPagoPaRecordedTimeout.pagoPaRecordedTimeout.reqOk.as("reqOk"),
            QPagoPaRecordedTimeout.pagoPaRecordedTimeout.avgTime.as("avgTime"),
            QPagoPaRecordedTimeout.pagoPaRecordedTimeout.reqTimeout.as("reqTimeout"));
    }
}
