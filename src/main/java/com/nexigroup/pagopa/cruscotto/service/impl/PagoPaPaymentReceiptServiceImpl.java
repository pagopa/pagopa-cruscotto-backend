package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.PagoPaPaymentReceipt;
import com.nexigroup.pagopa.cruscotto.domain.QPagoPaPaymentReceipt;
import com.nexigroup.pagopa.cruscotto.service.PagoPaPaymentReceiptService;
import com.nexigroup.pagopa.cruscotto.service.dto.PagoPaPaymentReceiptDTO;
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
 * Service Implementation for managing {@link PagoPaPaymentReceipt}.
 */
@Service
@Transactional
public class PagoPaPaymentReceiptServiceImpl implements PagoPaPaymentReceiptService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PagoPaPaymentReceiptServiceImpl.class);

    private final QueryBuilder queryBuilder;

    public PagoPaPaymentReceiptServiceImpl(QueryBuilder queryBuilder) {
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
    public List<String> findAllStationIntoPeriodForPartner(String fiscalCodePartner, LocalDate startDate, LocalDate endDate) {
        QPagoPaPaymentReceipt qPagoPaPaymentReceipt = QPagoPaPaymentReceipt.pagoPaPaymentReceipt;

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59, 0);

        List<String> results = queryBuilder
            .createQueryFactory()
            .select(qPagoPaPaymentReceipt.station)
            .from(qPagoPaPaymentReceipt)
            .where(
                qPagoPaPaymentReceipt.cfPartner
                    .eq(fiscalCodePartner)
                    .and(qPagoPaPaymentReceipt.startDate.goe(startDateTime.atZone(ZoneOffset.systemDefault()).toInstant()))
                    .and(qPagoPaPaymentReceipt.endDate.loe(endDateTime.atZone(ZoneOffset.systemDefault()).toInstant()))
            )
            .groupBy(qPagoPaPaymentReceipt.station)
            .orderBy(qPagoPaPaymentReceipt.station.asc())
            .fetch();

        return results;
    }

    @Override
    public List<PagoPaPaymentReceiptDTO> findAllRecordIntoDayForPartnerAndStation(String fiscalCodePartner, String station, LocalDate day) {
        QPagoPaPaymentReceipt qPagoPaPaymentReceipt = QPagoPaPaymentReceipt.pagoPaPaymentReceipt;

        LocalDateTime startDateTime = day.atStartOfDay();
        LocalDateTime endDateTime = day.atTime(23, 59, 59, 0);

        LOGGER.info(startDateTime.toString());
        LOGGER.info(endDateTime.toString());

        return queryBuilder
            .createQueryFactory()
            .select(
                Projections.fields(
                    PagoPaPaymentReceiptDTO.class,
                    qPagoPaPaymentReceipt.id.as("id"),
                    qPagoPaPaymentReceipt.cfPartner.as("cfPartner"),
                    qPagoPaPaymentReceipt.station.as("station"),
                    qPagoPaPaymentReceipt.startDate.as("startDate"),
                    qPagoPaPaymentReceipt.endDate.as("endDate"),
                    qPagoPaPaymentReceipt.totRes.as("totRes"),
                    qPagoPaPaymentReceipt.resOk.as("resOk"),
                    qPagoPaPaymentReceipt.resKo.as("resKo")
                )
            )
            .from(qPagoPaPaymentReceipt)
            .where(
                qPagoPaPaymentReceipt.cfPartner
                    .eq(fiscalCodePartner)
                    .and(qPagoPaPaymentReceipt.station.eq(station))
                    .and(qPagoPaPaymentReceipt.startDate.goe(startDateTime.atZone(ZoneOffset.systemDefault()).toInstant()))
                    .and(qPagoPaPaymentReceipt.startDate.loe(endDateTime.atZone(ZoneOffset.systemDefault()).toInstant()))
            )
            .orderBy(new OrderSpecifier<>(Order.ASC, Expressions.stringPath("startDate")))
            .fetch();
    }

    /**
     * Get all the pagopa payment receipts
     *
     * @param pageable the pagination information.
     * @return the list of pagopa payment receipts.
     */
    @Override
    public Page<PagoPaPaymentReceiptDTO> findAll(Pageable pageable) {
        LOGGER.debug("Request to get all pagopa payment receipts");

        QPagoPaPaymentReceipt qPagoPaPaymentReceipt = QPagoPaPaymentReceipt.pagoPaPaymentReceipt;

        JPQLQuery<com.nexigroup.pagopa.cruscotto.domain.PagoPaPaymentReceipt> query = queryBuilder
            .<PagoPaPaymentReceipt>createQuery()
            .from(qPagoPaPaymentReceipt);

        long total = query.fetchCount();

        JPQLQuery<PagoPaPaymentReceiptDTO> jpqlQuery = query.select(createPagoPaPaymentReceiptProjection());

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

        List<PagoPaPaymentReceiptDTO> results = jpqlQuery.fetch();

        return new PageImpl<>(results, pageable, total);
    }

    private com.querydsl.core.types.Expression<PagoPaPaymentReceiptDTO> createPagoPaPaymentReceiptProjection() {
        return Projections.fields(
            PagoPaPaymentReceiptDTO.class,
            QPagoPaPaymentReceipt.pagoPaPaymentReceipt.id.as("id"),
            QPagoPaPaymentReceipt.pagoPaPaymentReceipt.cfPartner.as("cfPartner"),
            QPagoPaPaymentReceipt.pagoPaPaymentReceipt.station.as("station"),
            QPagoPaPaymentReceipt.pagoPaPaymentReceipt.startDate.as("startDate"),
            QPagoPaPaymentReceipt.pagoPaPaymentReceipt.endDate.as("endDate"),
            QPagoPaPaymentReceipt.pagoPaPaymentReceipt.totRes.as("totRes"),
            QPagoPaPaymentReceipt.pagoPaPaymentReceipt.resOk.as("resOk"),
            QPagoPaPaymentReceipt.pagoPaPaymentReceipt.resKo.as("resKo")
        );
    }
}


