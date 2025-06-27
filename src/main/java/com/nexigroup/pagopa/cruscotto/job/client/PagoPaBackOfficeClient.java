package com.nexigroup.pagopa.cruscotto.job.client;

import com.nexigroup.pagopa.cruscotto.job.plannedshutdown.StationPlannedShutdown;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

public interface PagoPaBackOfficeClient {
    @GetMapping("/station-maintenances")
    @ApiKey(name = "application.pago-pa-client.back-office.api-key-name", value = "application.pago-pa-client.back-office.api-key-value")
    StationPlannedShutdown maintenance(@RequestParam("year") Integer year);
}
