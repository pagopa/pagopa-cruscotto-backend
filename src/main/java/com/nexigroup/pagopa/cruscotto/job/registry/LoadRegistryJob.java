package com.nexigroup.pagopa.cruscotto.job.registry;

import com.nexigroup.pagopa.cruscotto.config.ApplicationProperties;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.TaxonomyField;
import com.nexigroup.pagopa.cruscotto.job.client.PagoPaClient;
import com.nexigroup.pagopa.cruscotto.service.TaxonomyService;
import com.nexigroup.pagopa.cruscotto.service.dto.TaxonomyDTO;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
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

@Component
@AllArgsConstructor
@DisallowConcurrentExecution
public class LoadRegistryJob extends QuartzJobBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoadRegistryJob.class);

    private final PagoPaClient pagoPaClient;

    private final ApplicationProperties applicationProperties;

    private final TaxonomyService taxonomyService;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    @Override
    protected void executeInternal(@NotNull JobExecutionContext context) throws JobExecutionException {
        LOGGER.info("Start load taxonomy from PagoPA");

        try {
            LOGGER.info("Call PagoPA to get taxonomy");

            List<Map<String, String>> response = pagoPaClient.tassonomia(
                new URI(applicationProperties.getPagoPaClient().getTaxonomy().getUrl())
            );

            List<TaxonomyDTO> taxonomyDTOS = new ArrayList<>();

            try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
                Validator validator = factory.getValidator();

                response.forEach(taxonomy -> {
                    TaxonomyDTO taxonomyDTO;
                    taxonomyDTO = new TaxonomyDTO();
                    taxonomyDTO.setTakingsIdentifier(taxonomy.get(TaxonomyField.TAKINGS_IDENTIFIER.field));
                    taxonomyDTO.setValidityStartDate(
                        parseDate(
                            taxonomy.get(TaxonomyField.VALIDITY_START_DATE.field),
                            taxonomy.get(TaxonomyField.TAKINGS_IDENTIFIER.field)
                        )
                    );
                    taxonomyDTO.setValidityEndDate(
                        parseDate(taxonomy.get(TaxonomyField.VALIDITY_END_DATE.field), taxonomy.get(TaxonomyField.TAKINGS_IDENTIFIER.field))
                    );

                    Set<ConstraintViolation<TaxonomyDTO>> violations = validator.validate(taxonomyDTO);

                    if (violations.isEmpty()) {
                        taxonomyDTOS.add(taxonomyDTO);
                    } else {
                        LOGGER.error("Invalid taxonomy {}", taxonomyDTO);
                        violations.forEach(violation -> LOGGER.error("{}: {}", violation.getPropertyPath(), violation.getMessage()));
                    }
                });
            }

            LOGGER.info("{} records out of {} will be saved", taxonomyDTOS.size(), response.size());

            Long rows = taxonomyService.countAll();

            LOGGER.info("Delete all {} rows taxonomy from database", rows);
            taxonomyService.deleteAll();

            StopWatch stopWatch = new StopWatch();
            stopWatch.start();

            taxonomyService.saveAll(taxonomyDTOS);

            stopWatch.stop();

            LOGGER.info("Saved {} rows taxonomy to database into {} seconds", taxonomyDTOS.size(), stopWatch.getTime(TimeUnit.SECONDS));
        } catch (URISyntaxException e) {
            throw new JobExecutionException(e);
        }

        LOGGER.info("End");
    }

    private LocalDate parseDate(String date, String takingsIdentifier) {
        try {
            return LocalDate.parse(date, formatter);
        } catch (DateTimeParseException e) {
            LOGGER.error("Error parsing date {} for takings identifier {}", date, takingsIdentifier);
            return null;
        }
    }
}
