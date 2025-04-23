package com.nexigroup.pagopa.cruscotto.job.client;

import org.springframework.web.bind.annotation.GetMapping;

import java.net.URI;
import java.util.List;
import java.util.Map;

public interface PagoPaClient {

    @GetMapping
    List<Map<String, String>> tassonomia(URI baseUri);
}
