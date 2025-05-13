package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.*;
import com.nexigroup.pagopa.cruscotto.service.PagoPaRecordedTimeoutService;
import com.nexigroup.pagopa.cruscotto.service.dto.PagoPaRecordedTimeoutDTO;
import com.nexigroup.pagopa.cruscotto.service.qdsl.QueryBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import java.beans.Expression;
import java.time.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
                    .and(qPagoPaRecordedTimeout.startDate.goe(startDateTime.toInstant(ZoneOffset.UTC)))
                    .and(qPagoPaRecordedTimeout.endDate.loe(endDateTime.toInstant(ZoneOffset.UTC)))
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
}
