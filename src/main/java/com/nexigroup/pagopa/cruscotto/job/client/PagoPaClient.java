package com.nexigroup.pagopa.cruscotto.job.client;

import com.nexigroup.pagopa.cruscotto.job.plannedshutdown.StationPlannedShutdownResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.URI;
import java.util.List;
import java.util.Map;

public interface PagoPaClient {

    @GetMapping(name = "tassonomia")
    List<Map<String, String>> tassonomia(URI baseUri);

    @GetMapping
    @ApiKey(name = "application.pago-pa-client.maintenance.api-key-name", value = "application.pago-pa-client.maintenance.api-key-value")
    StationPlannedShutdownResponse maintenance(URI baseUri, @RequestParam("year") Integer year);
}
