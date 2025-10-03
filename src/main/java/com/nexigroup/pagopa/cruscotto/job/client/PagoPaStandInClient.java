package com.nexigroup.pagopa.cruscotto.job.client;

import com.nexigroup.pagopa.cruscotto.job.standin.StandInEventsResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

public interface PagoPaStandInClient {
    @GetMapping("/events")
    @ApiKey(name = "application.pago-pa-client.stand-in.api-key-name", value = "application.pago-pa-client.stand-in.api-key-value")
    StandInEventsResponse getStandInEvents(
        @RequestParam("from") String from,
        @RequestParam("to") String to,
        @RequestParam(value = "station", required = false) String station
    );
}