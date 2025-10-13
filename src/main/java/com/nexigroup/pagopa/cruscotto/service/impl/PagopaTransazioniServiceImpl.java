package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.PagopaTransaction;
import com.nexigroup.pagopa.cruscotto.domain.QPagopaTransaction;
import com.nexigroup.pagopa.cruscotto.repository.PagopaTransazioniRepository;
import com.nexigroup.pagopa.cruscotto.service.PagopaTransazioniService;
import com.nexigroup.pagopa.cruscotto.service.dto.PagopaTransactionDTO;
import com.nexigroup.pagopa.cruscotto.service.qdsl.QueryBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPQLQuery;
import java.time.LocalDate;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link PagopaTransaction}.
 */
@Service
@Transactional
public class PagopaTransazioniServiceImpl implements PagopaTransazioniService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PagopaTransazioniServiceImpl.class);

    private final PagopaTransazioniRepository pagopaTransazioniRepository;

    private final QueryBuilder queryBuilder;

    public PagopaTransazioniServiceImpl(
        PagopaTransazioniRepository pagopaTransazioniRepository,
        QueryBuilder queryBuilder
    ) {
        this.pagopaTransazioniRepository = pagopaTransazioniRepository;
        this.queryBuilder = queryBuilder;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PagopaTransactionDTO> findAllRecordIntoPeriodForPartner(
            String partnerFiscalCode, 
            LocalDate startDate, 
            LocalDate endDate) {
        
        LOGGER.debug("Request to get PagoPA transactions for partner {} from {} to {}", 
                partnerFiscalCode, startDate, endDate);

        final QPagopaTransaction qPagopaTransazioni = QPagopaTransaction.pagopaTransaction;

        JPQLQuery<PagopaTransactionDTO> query = queryBuilder
            .createQuery()
            .from(qPagopaTransazioni)
            .where(qPagopaTransazioni.cfPartner.eq(partnerFiscalCode)
                .and(qPagopaTransazioni.date.between(startDate, endDate)))
            .orderBy(qPagopaTransazioni.date.asc(),
                     qPagopaTransazioni.cfInstitution.asc(),
                     qPagopaTransazioni.station.asc())
            .select(
                Projections.fields(
                    PagopaTransactionDTO.class,
                    qPagopaTransazioni.id.as("id"),
                    qPagopaTransazioni.cfPartner.as("cfPartner"),
                    qPagopaTransazioni.cfInstitution.as("cfInstitution"),
                    qPagopaTransazioni.date.as("date"),
                    qPagopaTransazioni.station.as("station"),
                    qPagopaTransazioni.transactionTotal.as("transactionTotal")
                )
            );

        return query.fetch();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PagopaTransactionDTO> findAllRecordIntoPeriodForEntity(
            String entityCode, 
            LocalDate startDate, 
            LocalDate endDate) {
        
        LOGGER.debug("Request to get PagoPA transactions for entity {} from {} to {}", 
                entityCode, startDate, endDate);

        final QPagopaTransaction qPagopaTransazioni = QPagopaTransaction.pagopaTransaction;

        JPQLQuery<PagopaTransactionDTO> query = queryBuilder
            .createQuery()
            .from(qPagopaTransazioni)
            .where(qPagopaTransazioni.cfInstitution.eq(entityCode)
                .and(qPagopaTransazioni.date.between(startDate, endDate)))
            .orderBy(qPagopaTransazioni.date.asc(),
                     qPagopaTransazioni.station.asc())
            .select(
                Projections.fields(
                    PagopaTransactionDTO.class,
                    qPagopaTransazioni.id.as("id"),
                    qPagopaTransazioni.cfPartner.as("cfPartner"),
                    qPagopaTransazioni.cfInstitution.as("cfInstitution"),
                    qPagopaTransazioni.date.as("date"),
                    qPagopaTransazioni.station.as("station"),
                    qPagopaTransazioni.transactionTotal.as("transactionTotal")
                )
            );

        return query.fetch();
    }

    @Override
    @Transactional(readOnly = true)
    public long countUniqueEntitiesForPartnerInPeriod(
            String partnerFiscalCode, 
            LocalDate startDate, 
            LocalDate endDate) {
        
        LOGGER.debug("Request to count unique entities for partner {} from {} to {}", 
                partnerFiscalCode, startDate, endDate);

        final QPagopaTransaction qPagopaTransazioni = QPagopaTransaction.pagopaTransaction;

        Long count = queryBuilder
            .createQuery()
            .from(qPagopaTransazioni)
            .where(qPagopaTransazioni.cfPartner.eq(partnerFiscalCode)
                .and(qPagopaTransazioni.date.between(startDate, endDate)))
            .select(qPagopaTransazioni.cfInstitution.countDistinct())
            .fetchOne();

        return count != null ? count : 0L;
    }

    @Override
    @Transactional(readOnly = true)
    public long sumTotalTransactionsForPartnerInPeriod(
            String partnerFiscalCode, 
            LocalDate startDate, 
            LocalDate endDate) {
        
        LOGGER.debug("Request to sum total transactions for partner {} from {} to {}", 
                partnerFiscalCode, startDate, endDate);

        final QPagopaTransaction qPagopaTransazioni = QPagopaTransaction.pagopaTransaction;

        Integer sum = queryBuilder
            .createQuery()
            .from(qPagopaTransazioni)
            .where(qPagopaTransazioni.cfPartner.eq(partnerFiscalCode)
                .and(qPagopaTransazioni.date.between(startDate, endDate)))
            .select(qPagopaTransazioni.transactionTotal.sum())
            .fetchOne();

        return sum != null ? sum.longValue() : 0L;
    }

    /**
     * Convert PagopaTransazioni entity to DTO.
     *
     * @param entity the entity to convert
     * @return the DTO
     */
    public static @NotNull PagopaTransactionDTO convertToDTO(PagopaTransaction entity) {
        PagopaTransactionDTO dto = new PagopaTransactionDTO();
        dto.setId(entity.getId());
        dto.setCfPartner(entity.getCfPartner());
        dto.setCfInstitution(entity.getCfInstitution());
        dto.setDate(entity.getDate());
        dto.setStation(entity.getStation());
        dto.setTransactionTotal(entity.getTransactionTotal());
        return dto;
    }

    /**
     * Convert PagopaTransazioniDTO to entity.
     *
     * @param dto the DTO to convert
     * @return the entity
     */
    public static @NotNull PagopaTransaction convertToEntity(PagopaTransactionDTO dto) {
        PagopaTransaction entity = new PagopaTransaction();
        entity.setId(dto.getId());
        entity.setCfPartner(dto.getCfPartner());
        entity.setCfInstitution(dto.getCfInstitution());
        entity.setDate(dto.getDate());
        entity.setStation(dto.getStation());
        entity.setTransactionTotal(dto.getTransactionTotal());
        return entity;
    }
}