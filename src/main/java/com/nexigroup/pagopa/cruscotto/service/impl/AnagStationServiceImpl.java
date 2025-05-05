package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.AnagPartner;
import com.nexigroup.pagopa.cruscotto.domain.AnagStation;
import com.nexigroup.pagopa.cruscotto.domain.QAnagPartner;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.PartnerStatus;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.StationStatus;
import com.nexigroup.pagopa.cruscotto.repository.AnagPartnerRepository;
import com.nexigroup.pagopa.cruscotto.repository.AnagStationRepository;
import com.nexigroup.pagopa.cruscotto.service.AnagPartnerService;
import com.nexigroup.pagopa.cruscotto.service.AnagStationService;
import com.nexigroup.pagopa.cruscotto.service.dto.AnagPartnerDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.AnagStationDTO;
import com.nexigroup.pagopa.cruscotto.service.qdsl.QdslUtility;
import com.nexigroup.pagopa.cruscotto.service.qdsl.QueryBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPQLQuery;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link AnagStation}.
 */
@Service
@Transactional
public class AnagStationServiceImpl implements AnagStationService {

    private final Logger log = LoggerFactory.getLogger(AnagStationServiceImpl.class);

    private final AnagStationRepository anagStationRepository;

    private final AnagPartnerRepository anagPartnerRepository;

    @Value("${spring.jpa.properties.hibernate.jdbc.batch_size:100}")
    private String batchSize;

    public AnagStationServiceImpl(AnagStationRepository anagStationRepository, AnagPartnerRepository anagPartnerRepository) {
        this.anagStationRepository = anagStationRepository;
        this.anagPartnerRepository = anagPartnerRepository;
    }

    @Override
    public void saveAll(List<AnagStationDTO> stations) {
        log.debug("Request to save all stations");
        AtomicInteger i = new AtomicInteger(0);

        stations.forEach(stationDTO -> {
            AnagStation stationExample = new AnagStation();
            stationExample.setName(stationDTO.getName());
            stationExample.setCreatedDate(null);
            stationExample.setLastModifiedDate(null);
            stationExample.setPaymentOption(null);

            AnagPartner partnerExample = new AnagPartner();
            partnerExample.setFiscalCode(stationDTO.getPartnerFiscalCode());
            partnerExample.setCreatedDate(null);
            partnerExample.setLastModifiedDate(null);
            partnerExample.setQualified(null);

            AnagPartner anagPartner = anagPartnerRepository.findOne(Example.of(partnerExample)).orElse(new AnagPartner());

            AnagStation anagStation = anagStationRepository.findOne(Example.of(stationExample)).orElse(new AnagStation());
            anagStation.setName(stationDTO.getName());
            anagStation.setActivationDate(stationDTO.getActivationDate());
            anagStation.setTypeConnection(stationDTO.getTypeConnection());
            anagStation.setPrimitiveVersion(stationDTO.getPrimitiveVersion());
            anagStation.setPaymentOption(stationDTO.getPaymentOption());
            anagStation.setAssociatedInstitutes(0);
            anagStation.setAnagPartner(anagPartner);

            if (stationDTO.getStatus().compareTo(StationStatus.NON_ATTIVA) == 0 && anagStation.getDeactivationDate() == null) {
                anagStation.setDeactivationDate(LocalDate.now());
            }

            anagStation.setStatus(stationDTO.getStatus());

            anagStationRepository.save(anagStation);

            if (i.getAndIncrement() % Integer.parseInt(batchSize) == Integer.parseInt(batchSize)) {
                anagStationRepository.flush();
            }
        });
    }
}
