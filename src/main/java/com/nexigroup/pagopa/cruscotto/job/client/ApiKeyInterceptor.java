package com.nexigroup.pagopa.cruscotto.job.client;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

public class ApiKeyInterceptor implements RequestInterceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiKeyInterceptor.class);

    private final Environment environment;

    public ApiKeyInterceptor(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void apply(RequestTemplate requestTemplate) {
        ApiKey apiKey = requestTemplate.methodMetadata().method().getAnnotation(ApiKey.class);
        if (apiKey != null) {
            LOGGER.info("Adding API key to request");
            String name = environment.getProperty(apiKey.name());
            String value = environment.getProperty(apiKey.value());
            requestTemplate.header(name, value);
        }
    }
}
