package com.nexigroup.pagopa.cruscotto.config;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.LoggingEvent;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class EcsJsonLayoutTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void writesRequestContextAsTopLevelJsonFields() throws Exception {
        LoggerContext context = new LoggerContext();
        context.putProperty("service.name", "pagopa-cruscotto-backend-test");
        context.putProperty("environment", "test");

        EcsJsonLayout layout = new EcsJsonLayout();
        layout.setContext(context);
        layout.start();

        MDC.put("ctx_transaction_id", "7b7a347d-2440-4274-8b76-5aee56f1bf7d");
        MDC.put("correlation_id", "cfd0b210-3724-4339-a126-e421f45b2132");
        try {
            LoggingEvent event = new LoggingEvent(
                "com.nexigroup.TestService",
                context.getLogger("com.nexigroup.TestService"),
                Level.INFO,
                "request completed",
                null,
                null
            );
            event.setMDCPropertyMap(Map.of(
                "ctx_transaction_id", "7b7a347d-2440-4274-8b76-5aee56f1bf7d",
                "correlation_id", "cfd0b210-3724-4339-a126-e421f45b2132"
            ));

            JsonNode json = objectMapper.readTree(layout.doLayout(event));

            assertThat(json.get("@timestamp").asText()).isNotBlank();
            assertThat(json.get("log.level").asText()).isEqualTo("INFO");
            assertThat(json.get("service.name").asText()).isEqualTo("pagopa-cruscotto-backend-test");
            assertThat(json.get("service.version").asText()).isNotBlank();
            assertThat(json.get("environment").asText()).isEqualTo("test");
            assertThat(json.get("message").asText()).isEqualTo("request completed");
            assertThat(json.get("ctx_transaction_id").asText()).isEqualTo("7b7a347d-2440-4274-8b76-5aee56f1bf7d");
            assertThat(json.get("correlation_id").asText()).isEqualTo("cfd0b210-3724-4339-a126-e421f45b2132");
            assertThat(json.has("mdc")).isFalse();
            assertThat(json.has("trace.id")).isFalse();
        } finally {
            MDC.clear();
            layout.stop();
            context.stop();
        }
    }
}
