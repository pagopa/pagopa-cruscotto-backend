package com.nexigroup.pagopa.cruscotto.job;

import com.nexigroup.pagopa.cruscotto.config.ApplicationProperties;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.TaxonomyField;
import com.nexigroup.pagopa.cruscotto.job.client.PagoPaClient;
import com.nexigroup.pagopa.cruscotto.service.TaxonomyService;
import com.nexigroup.pagopa.cruscotto.service.dto.TaxonomyDTO;
import lombok.AllArgsConstructor;
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
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;


@Component
@AllArgsConstructor
@DisallowConcurrentExecution
public class LoadTaxonomyJob extends QuartzJobBean {

	private final static Logger LOGGER = LoggerFactory.getLogger(LoadTaxonomyJob.class);

    private final PagoPaClient pagoPaClient;

    private final ApplicationProperties applicationProperties;

    private final TaxonomyService taxonomyService;

    @Override
    protected void executeInternal(@NotNull JobExecutionContext context) throws JobExecutionException {
        LOGGER.info("Start load taxonomy from PagoPA");

        try {
            LOGGER.info("Call PagoPA to get taxonomy");
            String uri = applicationProperties.getPagoPaClient().getHost() + applicationProperties.getPagoPaClient().getApi().get("taxonomy");

            List<Map<String, String>> response = pagoPaClient.tassonomia(new URI(uri));

            Long rows = taxonomyService.countAll();
            LOGGER.info("Delete all {} rows taxonomy from database", rows);

            taxonomyService.deleteAll();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

            AtomicInteger ok = new AtomicInteger(0);
            AtomicInteger ko = new AtomicInteger(0);
            LOGGER.info("Save {} rows taxonomy to database", response.size());

            response.forEach(taxonomy -> {
                try {
                    TaxonomyDTO taxonomyDTO = new TaxonomyDTO();
                    taxonomyDTO.setTakingsIdentifier(taxonomy.get(TaxonomyField.TAKINGS_IDENTIFIER.field));
                    taxonomyDTO.setValidityStartDate(LocalDate.parse(taxonomy.get(TaxonomyField.VALIDITY_START_DATE.field), formatter));
                    taxonomyDTO.setValidityEndDate(LocalDate.parse(taxonomy.get(TaxonomyField.VALIDITY_END_DATE.field), formatter));

                    taxonomyService.save(taxonomyDTO);
                } catch (RuntimeException e) {
                  ko.incrementAndGet();
                }
                ok.incrementAndGet();
            });

            LOGGER.info("Saved {} rows taxonomy to database. {} rows taxonomy not saved.", ok.get(), ko.get() );

        } catch (URISyntaxException e) {
            throw new JobExecutionException(e);
        }

		LOGGER.info("End");
    }
}
