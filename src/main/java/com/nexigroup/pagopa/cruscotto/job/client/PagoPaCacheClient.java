package com.nexigroup.pagopa.cruscotto.job.client;

import com.nexigroup.pagopa.cruscotto.job.cache.Partner;
import com.nexigroup.pagopa.cruscotto.job.cache.Station;
import com.nexigroup.pagopa.cruscotto.job.cache.CreditorInstitutionStationsResponse;
import com.nexigroup.pagopa.cruscotto.job.cache.CreditorInstitutionsResponse;
import org.springframework.web.bind.annotation.GetMapping;

public interface PagoPaCacheClient {
    @GetMapping("/cache?keys=creditorInstitutions")
    @ApiKey(name = "application.pago-pa-client.cache.api-key-name", value = "application.pago-pa-client.cache.api-key-value")
    CreditorInstitutionsResponse creditorInstitutions();
    @GetMapping("/cache?keys=creditorInstitutionBrokers")
    @ApiKey(name = "application.pago-pa-client.cache.api-key-name", value = "application.pago-pa-client.cache.api-key-value")
    Partner[] partners();

    @GetMapping("/cache?keys=stations")
    @ApiKey(name = "application.pago-pa-client.cache.api-key-name", value = "application.pago-pa-client.cache.api-key-value")
    Station[] stations();

    @GetMapping("/cache?keys=creditorInstitutionStations")
    @ApiKey(name = "application.pago-pa-client.cache.api-key-name", value = "application.pago-pa-client.cache.api-key-value")
    CreditorInstitutionStationsResponse creditorInstitutionStations();
}
