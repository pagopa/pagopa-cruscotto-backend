package com.nexigroup.pagopa.cruscotto.job.client;

import com.nexigroup.pagopa.cruscotto.config.ApplicationProperties;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

public class ApiKeyInterceptor implements RequestInterceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiKeyInterceptor.class);

    private final Environment environment;
    private final ApplicationProperties applicationProperties;

    public ApiKeyInterceptor(Environment environment, ApplicationProperties applicationProperties) {
        this.environment = environment;
        this.applicationProperties = applicationProperties;
    }

    // Backwards compatibility constructor
    public ApiKeyInterceptor(Environment environment) {
        this.environment = environment;
        this.applicationProperties = null;
    }

    @Override
    public void apply(RequestTemplate requestTemplate) {
        ApiKey apiKey = requestTemplate.methodMetadata().method().getAnnotation(ApiKey.class);
        if (apiKey != null) {
            String nameProperty = apiKey.name();
            String valueProperty = apiKey.value();
            
            String name;
            String value;
            
            // Use ApplicationProperties for Stand-In API, fallback to Environment for others
            if (applicationProperties != null && nameProperty.contains("stand-in")) {
                name = applicationProperties.getPagoPaClient().getStandIn().getApiKeyName();
                value = applicationProperties.getPagoPaClient().getStandIn().getApiKeyValue();
                LOGGER.debug("Using ApplicationProperties for Stand-In: name={}, value=***", name);
            } else {
                name = environment.getProperty(nameProperty);
                value = environment.getProperty(valueProperty);
                LOGGER.debug("Using Environment properties: nameProperty={}, name={}, value=***", nameProperty, name);
            }
            
            if (name != null && value != null) {
                requestTemplate.header(name, value);
                LOGGER.debug("Added API key header: {} = ***", name);
            } else {
                LOGGER.error("API key configuration missing: nameProperty={}, valueProperty={}, name={}, value={}", 
                           nameProperty, valueProperty, name, value != null ? "***" : "null");
            }
        } else {
            LOGGER.debug("No @ApiKey annotation found on method: {}", requestTemplate.methodMetadata().method().getName());
        }
    }
}
