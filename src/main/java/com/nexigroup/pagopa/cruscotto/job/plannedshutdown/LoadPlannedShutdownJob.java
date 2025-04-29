package com.nexigroup.pagopa.cruscotto.job.plannedshutdown;

import com.nexigroup.pagopa.cruscotto.config.ApplicationProperties;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.TaxonomyField;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.TypePlanned;
import com.nexigroup.pagopa.cruscotto.job.client.PagoPaClient;
import com.nexigroup.pagopa.cruscotto.service.AnagPlannedShutdownService;
import com.nexigroup.pagopa.cruscotto.service.TaxonomyService;
import com.nexigroup.pagopa.cruscotto.service.dto.AnagPlannedShutdownDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.TaxonomyDTO;
import com.nexigroup.pagopa.cruscotto.service.validation.ValidationGroups;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.time.StopWatch;
import org.jetbrains.annotations.NotNull;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalField;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;


@Component
@AllArgsConstructor
@DisallowConcurrentExecution
public class LoadPlannedShutdownJob extends QuartzJobBean {

	private final static Logger LOGGER = LoggerFactory.getLogger(LoadPlannedShutdownJob.class);

    private final PagoPaClient pagoPaClient;

    private final ApplicationProperties applicationProperties;

    private final AnagPlannedShutdownService anagPlannedShutdownService;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    @Override
    protected void executeInternal(@NotNull JobExecutionContext context) throws JobExecutionException {
        LOGGER.info("Start load maintenance from PagoPA");

        int year = LocalDate.now().getYear();

        saveDataForYear(year - 1);

        saveDataForYear(year);

		LOGGER.info("End");
    }

    public void saveDataForYear(int year) throws JobExecutionException {
        try {
            LOGGER.info("Call PagoPA to get maintenance year {}", year);

            StationPlannedShutdownResponse response = callServiceMaintenance(new URI(applicationProperties.getPagoPaClient().getMaintenance().getUrl()), year);

            List<AnagPlannedShutdownDTO> plannedShutdownDTOS = validate(response.getStationMaintenanceList(), year);

            Long rows = anagPlannedShutdownService.count(TypePlanned.PROGRAMMATO, year);

            LOGGER.info("Delete all {} rows planned shutdown from database for year {}", rows, year);

            anagPlannedShutdownService.delete(TypePlanned.PROGRAMMATO, year);

            StopWatch stopWatch = new StopWatch();
            stopWatch.start();

            anagPlannedShutdownService.saveAll(plannedShutdownDTOS);

            stopWatch.stop();

            LOGGER.info("Saved {} rows planned shutdown to database into {} seconds", plannedShutdownDTOS.size(), stopWatch.getTime(TimeUnit.SECONDS));
        } catch (URISyntaxException e) {
            throw new JobExecutionException(e);
        }
    }

    private StationPlannedShutdownResponse callServiceMaintenance(URI uri, int year) {
        return pagoPaClient.maintenance(uri, year);
    }

    private List<AnagPlannedShutdownDTO> validate(List<StationPlannedShutdownResponse.StationMaintenance> stationMaintenanceList, int year) {
        List<AnagPlannedShutdownDTO> plannedShutdownDTOS = new ArrayList<>();

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            Validator validator = factory.getValidator();

            stationMaintenanceList.forEach(maintenance -> {

                AnagPlannedShutdownDTO anagPlannedShutdownDTO;
                anagPlannedShutdownDTO = new AnagPlannedShutdownDTO();
                anagPlannedShutdownDTO.setTypePlanned(TypePlanned.PROGRAMMATO);
                anagPlannedShutdownDTO.setStandInd(maintenance.isStandIn());
                anagPlannedShutdownDTO.setShutdownStartDate(maintenance.getStartDateTime().toInstant());
                anagPlannedShutdownDTO.setShutdownEndDate(maintenance.getEndDateTime().toInstant());
                anagPlannedShutdownDTO.setPartnerFiscalCode(maintenance.getBrokerCode());
                anagPlannedShutdownDTO.setExternalId(maintenance.getMaintenanceId());
                anagPlannedShutdownDTO.setYear((long) year);
                anagPlannedShutdownDTO.setStationName(maintenance.getStationCode());

                Set<ConstraintViolation<AnagPlannedShutdownDTO>> violations = validator.validate(anagPlannedShutdownDTO, ValidationGroups.PlannedShutdownJob.class);

                if (violations.isEmpty()) {
                    plannedShutdownDTOS.add(anagPlannedShutdownDTO);
                } else {
                    LOGGER.error("Invalid planned shutdown {}", anagPlannedShutdownDTO);
                    violations.forEach(violation -> LOGGER.error("{}: {}", violation.getPropertyPath(), violation.getMessage()));
                }
            });
        }

        LOGGER.info("{} records out of {} will be saved", plannedShutdownDTOS.size(), stationMaintenanceList.size());

        return plannedShutdownDTOS;
    }
}
