package com.nexigroup.pagopa.cruscotto.job.plannedshutdown;

import com.nexigroup.pagopa.cruscotto.domain.enumeration.TypePlanned;
import com.nexigroup.pagopa.cruscotto.job.client.PagoPaBackOfficeClient;
import com.nexigroup.pagopa.cruscotto.service.AnagPlannedShutdownService;
import com.nexigroup.pagopa.cruscotto.service.dto.AnagPlannedShutdownDTO;
import com.nexigroup.pagopa.cruscotto.service.validation.ValidationGroups;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import lombok.AllArgsConstructor;
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
public class LoadPlannedShutdownJob extends QuartzJobBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoadPlannedShutdownJob.class);

    private final PagoPaBackOfficeClient pagoPaBackOfficeClient;

    private final AnagPlannedShutdownService anagPlannedShutdownService;

    @Override
    protected void executeInternal(@NotNull JobExecutionContext context) {
        LOGGER.info("Start load maintenance from PagoPA");

        int year = LocalDate.now().getYear();

        saveDataForYear(year - 1);

        saveDataForYear(year);

        LOGGER.info("End");
    }

    public void saveDataForYear(int year) {
        LOGGER.info("Call PagoPA to get maintenance year {}", year);

        StationPlannedShutdown response = callServiceMaintenance(year);

        List<AnagPlannedShutdownDTO> plannedShutdownDTOS = validate(response.getStationMaintenanceList(), year);

        Long rows = anagPlannedShutdownService.count(TypePlanned.PROGRAMMATO, year);

        LOGGER.info("Delete all {} rows planned shutdown from database for year {}", rows, year);

        anagPlannedShutdownService.delete(TypePlanned.PROGRAMMATO, year);

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        anagPlannedShutdownService.saveAll(plannedShutdownDTOS);

        stopWatch.stop();

        LOGGER.info(
            "Saved {} rows planned shutdown to database into {} seconds",
            plannedShutdownDTOS.size(),
            stopWatch.getTime(TimeUnit.SECONDS)
        );
    }

    private StationPlannedShutdown callServiceMaintenance(int year) {
        return pagoPaBackOfficeClient.maintenance(year);
    }

    private List<AnagPlannedShutdownDTO> validate(List<StationPlannedShutdown.StationMaintenance> stationMaintenanceList, int year) {
        List<AnagPlannedShutdownDTO> plannedShutdownDTOS = new ArrayList<>();

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            Validator validator = factory.getValidator();

            stationMaintenanceList.forEach(maintenance -> {
                AnagPlannedShutdownDTO anagPlannedShutdownDTO;
                anagPlannedShutdownDTO = new AnagPlannedShutdownDTO();
                anagPlannedShutdownDTO.setTypePlanned(TypePlanned.PROGRAMMATO);
                anagPlannedShutdownDTO.setStandInd(maintenance.getStandIn());
                anagPlannedShutdownDTO.setShutdownStartDate(maintenance.getStartDateTime().toInstant());
                anagPlannedShutdownDTO.setShutdownEndDate(maintenance.getEndDateTime().toInstant());
                anagPlannedShutdownDTO.setPartnerFiscalCode(maintenance.getBrokerCode());
                anagPlannedShutdownDTO.setPartnerName(maintenance.getBrokerCode());
                anagPlannedShutdownDTO.setExternalId(maintenance.getMaintenanceId());
                anagPlannedShutdownDTO.setYear((long) year);
                anagPlannedShutdownDTO.setStationName(maintenance.getStationCode());

                Set<ConstraintViolation<AnagPlannedShutdownDTO>> violations = validator.validate(
                    anagPlannedShutdownDTO,
                    ValidationGroups.PlannedShutdownJob.class
                );

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
