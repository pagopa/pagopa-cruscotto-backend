package com.nexigroup.pagopa.cruscotto.job.client;

import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;

public interface PagoPaTaxonomyClient {
    @GetMapping("/taxonomy")
    List<Map<String, String>> tassonomia();
}
