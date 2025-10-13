package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.PagopaTransazioni;
import com.nexigroup.pagopa.cruscotto.domain.QPagopaTransazioni;
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
 * Service Implementation for managing {@link PagopaTransazioni}.
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

        final QPagopaTransazioni qPagopaTransazioni = QPagopaTransazioni.pagopaTransazioni;

        JPQLQuery<PagopaTransactionDTO> query = queryBuilder
            .createQuery()
            .from(qPagopaTransazioni)
            .where(qPagopaTransazioni.partner.eq(partnerFiscalCode)
                .and(qPagopaTransazioni.data.between(startDate, endDate)))
            .orderBy(qPagopaTransazioni.data.asc(),
                     qPagopaTransazioni.ente.asc(),
                     qPagopaTransazioni.stazione.asc())
            .select(
                Projections.fields(
                    PagopaTransactionDTO.class,
                    qPagopaTransazioni.id.as("id"),
                    qPagopaTransazioni.partner.as("partner"),
                    qPagopaTransazioni.ente.as("ente"),
                    qPagopaTransazioni.data.as("data"),
                    qPagopaTransazioni.stazione.as("stazione"),
                    qPagopaTransazioni.totaleTransazioni.as("totaleTransazioni")
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

        final QPagopaTransazioni qPagopaTransazioni = QPagopaTransazioni.pagopaTransazioni;

        JPQLQuery<PagopaTransactionDTO> query = queryBuilder
            .createQuery()
            .from(qPagopaTransazioni)
            .where(qPagopaTransazioni.ente.eq(entityCode)
                .and(qPagopaTransazioni.data.between(startDate, endDate)))
            .orderBy(qPagopaTransazioni.data.asc(),
                     qPagopaTransazioni.stazione.asc())
            .select(
                Projections.fields(
                    PagopaTransactionDTO.class,
                    qPagopaTransazioni.id.as("id"),
                    qPagopaTransazioni.partner.as("partner"),
                    qPagopaTransazioni.ente.as("ente"),
                    qPagopaTransazioni.data.as("data"),
                    qPagopaTransazioni.stazione.as("stazione"),
                    qPagopaTransazioni.totaleTransazioni.as("totaleTransazioni")
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

        final QPagopaTransazioni qPagopaTransazioni = QPagopaTransazioni.pagopaTransazioni;

        Long count = queryBuilder
            .createQuery()
            .from(qPagopaTransazioni)
            .where(qPagopaTransazioni.partner.eq(partnerFiscalCode)
                .and(qPagopaTransazioni.data.between(startDate, endDate)))
            .select(qPagopaTransazioni.ente.countDistinct())
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

        final QPagopaTransazioni qPagopaTransazioni = QPagopaTransazioni.pagopaTransazioni;

        Long sum = queryBuilder
            .createQuery()
            .from(qPagopaTransazioni)
            .where(qPagopaTransazioni.partner.eq(partnerFiscalCode)
                .and(qPagopaTransazioni.data.between(startDate, endDate)))
            .select(qPagopaTransazioni.totaleTransazioni.sum())
            .fetchOne();

        return sum != null ? sum : 0L;
    }

    /**
     * Convert PagopaTransazioni entity to DTO.
     *
     * @param entity the entity to convert
     * @return the DTO
     */
    public static @NotNull PagopaTransactionDTO convertToDTO(PagopaTransazioni entity) {
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
    public static @NotNull PagopaTransazioni convertToEntity(PagopaTransactionDTO dto) {
        PagopaTransazioni entity = new PagopaTransazioni();
        entity.setId(dto.getId());
        entity.setCfPartner(dto.getCfPartner());
        entity.setCfInstitution(dto.getCfInstitution());
        entity.setDate(dto.getDate());
        entity.setStation(dto.getStation());
        entity.setTransactionTotal(dto.getTransactionTotal());
        return entity;
    }
}