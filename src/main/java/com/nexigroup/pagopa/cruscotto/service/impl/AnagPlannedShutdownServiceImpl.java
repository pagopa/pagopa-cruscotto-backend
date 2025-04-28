package com.nexigroup.pagopa.cruscotto.service.impl;


import com.nexigroup.pagopa.cruscotto.domain.AnagPartner;
import com.nexigroup.pagopa.cruscotto.domain.AnagPlannedShutdown;
import com.nexigroup.pagopa.cruscotto.domain.AnagStation;
import com.nexigroup.pagopa.cruscotto.domain.QAnagPlannedShutdown;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.TypePlanned;
import com.nexigroup.pagopa.cruscotto.repository.AnagPartnerRepository;
import com.nexigroup.pagopa.cruscotto.repository.AnagPlannedShutdownRepository;
import com.nexigroup.pagopa.cruscotto.repository.AnagStationRepository;
import com.nexigroup.pagopa.cruscotto.service.AnagPlannedShutdownService;
import com.nexigroup.pagopa.cruscotto.service.dto.AnagPlannedShutdownDTO;
import com.nexigroup.pagopa.cruscotto.service.qdsl.QueryBuilder;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/**
 * Service Implementation for managing {@link AnagPlannedShutdown}.
 */
@Service
@Transactional
public class AnagPlannedShutdownServiceImpl implements AnagPlannedShutdownService {

    private final Logger log = LoggerFactory.getLogger(AnagPlannedShutdownServiceImpl.class);

    private final AnagPlannedShutdownRepository anagPlannedShutdownRepository;

    private final AnagPartnerRepository anagPartnerRepository;

    private final AnagStationRepository anagStationRepository;

    private final QueryBuilder queryBuilder;

    public AnagPlannedShutdownServiceImpl(AnagPlannedShutdownRepository anagPlannedShutdownRepository, AnagPartnerRepository anagPartnerRepository, AnagStationRepository anagStationRepository, QueryBuilder queryBuilder) {
        this.anagPlannedShutdownRepository = anagPlannedShutdownRepository;
        this.anagPartnerRepository = anagPartnerRepository;
        this.anagStationRepository = anagStationRepository;
        this.queryBuilder = queryBuilder;
    }

    /**
     * Count all the plannedShutdowns by type planned and year.
     *
     * @param typePlanned the type planned.
     * @param year the year.
     * @return the count of entities.
     */
    @Override
    public Long count(TypePlanned typePlanned, long year) {
        AnagPlannedShutdown anagPlannedShutdown = new AnagPlannedShutdown();
        anagPlannedShutdown.setTypePlanned(typePlanned);
        anagPlannedShutdown.setYear(year);
        Example<AnagPlannedShutdown> example = Example.of(anagPlannedShutdown);

        return anagPlannedShutdownRepository.count(example);
    }

    /**
     * Delete all the plannedShutdowns by type planned and year.
     *
     * @param typePlanned the type planned.
     * @param year the year.
     */
    @Override
    public void delete(TypePlanned typePlanned, long year) {
        queryBuilder.createQueryFactory().delete(QAnagPlannedShutdown.anagPlannedShutdown)
            .where(
                QAnagPlannedShutdown.anagPlannedShutdown.typePlanned.eq(TypePlanned.PROGRAMMATO)
                    .and(QAnagPlannedShutdown.anagPlannedShutdown.year.eq(year))
                );
    }

    /**
     * Save all planned shutdown.
     *
     * @param anagPlannedShutdownDTOS the entities to save.
     */
    @Override
    public void saveAll(List<AnagPlannedShutdownDTO> anagPlannedShutdownDTOS) {
        anagPlannedShutdownDTOS.forEach(anagPlannedShutdownDTO -> {
            AnagPartner partnerExample = new AnagPartner();
            partnerExample.setFiscalCode(anagPlannedShutdownDTO.getPartnerFiscalCode());

            AnagPartner partner = anagPartnerRepository.findOne(Example.of(partnerExample)).orElseGet(() -> {
               AnagPartner partnerSaved = new AnagPartner();
                partnerSaved.setFiscalCode(anagPlannedShutdownDTO.getPartnerFiscalCode());
               return anagPartnerRepository.save(partnerSaved);
            });

            AnagStation stationExample = new AnagStation();
            stationExample.setName(anagPlannedShutdownDTO.getStationName());


            AnagStation station = anagStationRepository.findOne(Example.of(stationExample)).orElseGet(() -> {
                AnagStation stationSaved = new AnagStation();
                stationSaved.setName(anagPlannedShutdownDTO.getPartnerName());
                return anagStationRepository.save(stationSaved);
            });


            AnagPlannedShutdown plannedShutdown = getAnagPlannedShutdown(anagPlannedShutdownDTO, partner, station);

            anagPlannedShutdownRepository.save(plannedShutdown);
        });
    }

    private static @NotNull AnagPlannedShutdown getAnagPlannedShutdown(AnagPlannedShutdownDTO anagPlannedShutdownDTO, AnagPartner partner, AnagStation station) {
        AnagPlannedShutdown plannedShutdown = new AnagPlannedShutdown();
        plannedShutdown.setTypePlanned(anagPlannedShutdownDTO.getTypePlanned());
        plannedShutdown.setStandInd(anagPlannedShutdownDTO.isStandInd());
        plannedShutdown.setShutdownStartDate(anagPlannedShutdownDTO.getShutdownStartDate());
        plannedShutdown.setShutdownEndDate(anagPlannedShutdownDTO.getShutdownEndDate());
        plannedShutdown.setAnagPartner(partner);
        plannedShutdown.setAnagStation(station);
        plannedShutdown.setYear(anagPlannedShutdownDTO.getYear());
        plannedShutdown.setExternalId(anagPlannedShutdownDTO.getExternalId());
        return plannedShutdown;
    }
}
