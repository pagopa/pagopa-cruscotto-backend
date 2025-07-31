package com.nexigroup.pagopa.cruscotto.job.cache;

import com.nexigroup.pagopa.cruscotto.domain.enumeration.PartnerStatus;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.StationStatus;
import com.nexigroup.pagopa.cruscotto.job.client.PagoPaCacheClient;
import com.nexigroup.pagopa.cruscotto.service.AnagPartnerService;
import com.nexigroup.pagopa.cruscotto.service.AnagStationService;
import com.nexigroup.pagopa.cruscotto.service.AnagInstitutionService;
import com.nexigroup.pagopa.cruscotto.service.AnagStationAnagInstitutionService;
import com.nexigroup.pagopa.cruscotto.domain.AnagStationAnagInstitution;
import com.nexigroup.pagopa.cruscotto.domain.AnagStation;
import com.nexigroup.pagopa.cruscotto.domain.AnagInstitution;
import com.nexigroup.pagopa.cruscotto.service.dto.AnagPartnerDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.AnagStationDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.PartnerIdentificationDTO;
import com.nexigroup.pagopa.cruscotto.service.validation.ValidationGroups;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.jetbrains.annotations.NotNull;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@DisallowConcurrentExecution
public class LoadRegistryJob extends QuartzJobBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoadRegistryJob.class);
        
    private final PagoPaCacheClient pagoPaCacheClient;

    private final AnagPartnerService anagPartnerService;

    private final AnagStationService anagStationService;

    private final AnagInstitutionService anagInstitutionService;
    private final AnagStationAnagInstitutionService anagStationAnagInstitutionService;

    @Override
    protected void executeInternal(@NotNull JobExecutionContext context) {
        LOGGER.info("Start load cache from PagoPA");

        loadPartners();

        loadStations();

        loadInstitutions();

        loadInstitutionsStations();

        LOGGER.info("End");
    }
    /**
     * Loads institutions from PagoPA and fills anag_institution table.
     * Uses PagoPaCacheClient.creditorInstitutions() and saves all institutions.
     */
    private void loadInstitutions() {
        LOGGER.info("Call PagoPA to get creditorInstitutions");
        CreditorInstitutionsResponse response = pagoPaCacheClient.creditorInstitutions();
        int size = response.getCreditorInstitutions() != null ? response.getCreditorInstitutions().size() : 0;
        LOGGER.info("{} records will be processed for institutions", size);

        List<AnagInstitution> institutions = new ArrayList<>();

        if (size > 0) {
            for (CreditorInstitution ci : response.getCreditorInstitutions().values()) {
                // Find or create AnagInstitution by code
                AnagInstitution institution = anagInstitutionService.findByInstitutionCode(ci.getCreditorInstitutionCode());
                if (institution == null) {
                    institution = new AnagInstitution();
                }
                institution.setFiscalCode(ci.getCreditorInstitutionCode());
                institution.setName(ci.getBusinessName());
                institution.setEnabled(ci.getEnabled());
                institutions.add(institution);
            }
            // Save all institutions
            anagInstitutionService.saveAll(institutions);
            LOGGER.info("Saved {} rows to anag_institution", institutions.size());
        }
    }

        
    
    private void loadInstitutionsStations() {
        LOGGER.info("Call PagoPA to get creditorInstitutionStations");
        CreditorInstitutionStationsResponse response = pagoPaCacheClient.creditorInstitutionStations();
        int size = response.getCreditorInstitutionStations() != null ? response.getCreditorInstitutionStations().size() : 0;
        LOGGER.info("{} records will be processed for institution-station associations", size);

        List<AnagStationAnagInstitution> associations = new ArrayList<>();

        if (size > 0) {
            for (CreditorInstitutionStation cis : response.getCreditorInstitutionStations().values()) {
                // Find or create AnagStation and AnagInstitution by code
                AnagStation station = anagStationService.findOneByName(cis.getStationCode()).orElse(null);
                AnagInstitution institution = anagInstitutionService.findByInstitutionCode(cis.getCreditorInstitutionCode());
                if (station != null && institution != null) {
                    AnagStationAnagInstitution association = new AnagStationAnagInstitution();
                    association.setAnagStation(station);
                    association.setAnagInstitution(institution);
                    association.setAca(cis.getAca());
                    association.setStandin(cis.getStandin());
                    associations.add(association);
                } else {
                    LOGGER.warn("Station or Institution not found for codes: {} / {}", cis.getStationCode(), cis.getCreditorInstitutionCode());
                }
            }
            // Save all associations
            anagStationAnagInstitutionService.saveAll(associations);
            LOGGER.info("Saved {} rows to anag_station_anag_institution", associations.size());
        }
    }

    private void loadPartners() {
        LOGGER.info("Call PagoPA to get partner");

        Partner[] response = pagoPaCacheClient.partners();

        List<AnagPartnerDTO> partnerDTOS = new ArrayList<>();

        LOGGER.info("{} records will be saved ", response.length);

        if (ArrayUtils.isNotEmpty(response)) {
            try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
                Validator validator = factory.getValidator();
                for (Partner partner : response) {
                    LOGGER.info("{}", partner);

                    AnagPartnerDTO partnerDTO;
                    partnerDTO = new AnagPartnerDTO();
                    partnerDTO.setPartnerIdentification(new PartnerIdentificationDTO());
                    partnerDTO.getPartnerIdentification().setFiscalCode(partner.getBrokerCode());
                    partnerDTO.getPartnerIdentification().setName(partner.getDescription());
                    partnerDTO.setStatus(
                        BooleanUtils.toBooleanDefaultIfNull(partner.getEnabled(), false) ? PartnerStatus.ATTIVO : PartnerStatus.NON_ATTIVO
                    );

                    Set<ConstraintViolation<AnagPartnerDTO>> violations = validator.validate(
                        partnerDTO,
                        ValidationGroups.RegistryJob.class
                    );

                    if (violations.isEmpty()) {
                        partnerDTOS.add(partnerDTO);
                    } else {
                        LOGGER.error("Invalid partner {}", partnerDTO);
                        violations.forEach(violation -> LOGGER.error("{}: {}", violation.getPropertyPath(), violation.getMessage()));
                    }
                }
            }

            LOGGER.info("After validation {} records will be saved", partnerDTOS.size());

            StopWatch stopWatch = new StopWatch();
            stopWatch.start();

            anagPartnerService.saveAll(partnerDTOS);

            stopWatch.stop();

            LOGGER.info("Saved {} rows partners to database into {} seconds", partnerDTOS.size(), stopWatch.getTime(TimeUnit.SECONDS));
        }
    }

    private void loadStations() {
        LOGGER.info("Call PagoPA to get stations");

        Station[] response = pagoPaCacheClient.stations();

        List<AnagStationDTO> stationDTOS = new ArrayList<>();

        LOGGER.info("{} records will be saved ", response.length);

        if (ArrayUtils.isNotEmpty(response)) {
            try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
                Validator validator = factory.getValidator();
                for (Station station : response) {
                    LOGGER.info("{}", station);

                    AnagStationDTO stationDTO;
                    stationDTO = new AnagStationDTO();
                    stationDTO.setName(station.getStationCode());
                    stationDTO.setPartnerFiscalCode(station.getBrokerCode());
                    stationDTO.setPrimitiveVersion(station.getPrimitiveVersion());
                    stationDTO.setPaymentOption(station.getIsPaymentOptionsEnabled());
                    stationDTO.setStatus(
                        BooleanUtils.toBooleanDefaultIfNull(station.getEnabled(), false) ? StationStatus.ATTIVA : StationStatus.NON_ATTIVA
                    );

                    Set<ConstraintViolation<AnagStationDTO>> violations = validator.validate(stationDTO, ValidationGroups.StationJob.class);

                    if (violations.isEmpty()) {
                        stationDTOS.add(stationDTO);
                    } else {
                        LOGGER.error("Invalid station {}", stationDTO);
                        violations.forEach(violation -> LOGGER.error("{}: {}", violation.getPropertyPath(), violation.getMessage()));
                    }
                }
            }

            LOGGER.info("After validation {} records will be saved", stationDTOS.size());

            StopWatch stopWatch = new StopWatch();
            stopWatch.start();

            anagStationService.saveAll(stationDTOS);

            // Enhance: increment stationsCount for each partner as stations are loaded
            java.util.Map<String, Long> partnerStationCounts = new java.util.HashMap<>();
            for (AnagStationDTO stationDTO : stationDTOS) {
                String partnerFiscalCode = stationDTO.getPartnerFiscalCode();
                partnerStationCounts.put(partnerFiscalCode, partnerStationCounts.getOrDefault(partnerFiscalCode, 0L) + 1);
            }
            partnerStationCounts.forEach((fiscalCode, count) -> {
                anagPartnerService.findOneByFiscalCode(fiscalCode).ifPresent(partnerDTO -> {
                    Long partnerId = partnerDTO.getPartnerIdentification() != null ? partnerDTO.getPartnerIdentification().getId() : null;
                    if (partnerId != null) {
                        anagPartnerService.updateStationsCount(partnerId, count);
                    }
                });
            });

            stopWatch.stop();

            LOGGER.info("Saved {} rows stations to database into {} seconds", stationDTOS.size(), stopWatch.getTime(TimeUnit.SECONDS));
        }
    }
}
