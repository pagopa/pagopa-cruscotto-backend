package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.*;
import com.nexigroup.pagopa.cruscotto.service.PagoPaRecordedTimeoutService;
import com.nexigroup.pagopa.cruscotto.service.qdsl.QueryBuilder;
import com.querydsl.core.Tuple;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
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
        QPagoPaRecordedTimeout qTimeout = QPagoPaRecordedTimeout.pagoPaRecordedTimeout;

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59, 999999999);

        List<Tuple> results = queryBuilder
            .createQueryFactory()
            .select(qTimeout.station, qTimeout.method)
            .from(qTimeout)
            .where(
                qTimeout.cfPartner
                    .eq(fiscalCodePartner)
                    .and(qTimeout.startDate.goe(startDateTime.atZone(ZoneId.systemDefault()).toInstant()))
                    .and(qTimeout.endDate.loe(endDateTime.atZone(ZoneId.systemDefault()).toInstant()))
            )
            .groupBy(qTimeout.station, qTimeout.method)
            .orderBy(qTimeout.station.asc(), qTimeout.method.asc())
            .fetch();

        Map<String, List<String>> groupedResults = new HashMap<>();

        for (Tuple tuple : results) {
            String station = tuple.get(qTimeout.station);
            String method = tuple.get(qTimeout.method);
            groupedResults.computeIfAbsent(station, k -> new ArrayList<>()).add(method);
        }

        return groupedResults;
    }
}
