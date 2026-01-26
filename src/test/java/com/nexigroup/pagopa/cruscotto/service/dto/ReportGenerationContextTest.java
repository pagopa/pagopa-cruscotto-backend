package com.nexigroup.pagopa.cruscotto.service.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ReportGenerationContextTest {

    @Test
    void testBuilderAndGetters() {
        Map<String, Object> params = new HashMap<>();
        params.put("key", "value");

        ReportGenerationContext context = ReportGenerationContext.builder()
            .reportId(1L)
            .instanceId(2L)
            .instanceName("Instance A")
            .language("EN")
            .startDate(LocalDate.of(2026, 1, 1))
            .endDate(LocalDate.of(2026, 1, 31))
            .parameters(params)
            .requestedBy("user1")
            .build();

        // Assert all getters
        assertThat(context.getReportId()).isEqualTo(1L);
        assertThat(context.getInstanceId()).isEqualTo(2L);
        assertThat(context.getInstanceName()).isEqualTo("Instance A");
        assertThat(context.getLanguage()).isEqualTo("EN");
        assertThat(context.getStartDate()).isEqualTo(LocalDate.of(2026, 1, 1));
        assertThat(context.getEndDate()).isEqualTo(LocalDate.of(2026, 1, 31));
        assertThat(context.getParameters()).isEqualTo(params);
        assertThat(context.getRequestedBy()).isEqualTo("user1");
    }

    @Test
    void testSetters() {
        ReportGenerationContext context = new ReportGenerationContext();
        Map<String, Object> params = new HashMap<>();
        params.put("key", "value");

        context.setReportId(10L);
        context.setInstanceId(20L);
        context.setInstanceName("Instance B");
        context.setLanguage("IT");
        context.setStartDate(LocalDate.of(2026, 2, 1));
        context.setEndDate(LocalDate.of(2026, 2, 28));
        context.setParameters(params);
        context.setRequestedBy("user2");

        // Assert all getters
        assertThat(context.getReportId()).isEqualTo(10L);
        assertThat(context.getInstanceId()).isEqualTo(20L);
        assertThat(context.getInstanceName()).isEqualTo("Instance B");
        assertThat(context.getLanguage()).isEqualTo("IT");
        assertThat(context.getStartDate()).isEqualTo(LocalDate.of(2026, 2, 1));
        assertThat(context.getEndDate()).isEqualTo(LocalDate.of(2026, 2, 28));
        assertThat(context.getParameters()).isEqualTo(params);
        assertThat(context.getRequestedBy()).isEqualTo("user2");
    }

    @Test
    void testEqualsAndHashCode() {
        Map<String, Object> params = new HashMap<>();
        params.put("key", "value");

        ReportGenerationContext context1 = ReportGenerationContext.builder()
            .reportId(1L)
            .instanceId(2L)
            .instanceName("Instance A")
            .language("EN")
            .startDate(LocalDate.of(2026, 1, 1))
            .endDate(LocalDate.of(2026, 1, 31))
            .parameters(params)
            .requestedBy("user1")
            .build();

        ReportGenerationContext context2 = ReportGenerationContext.builder()
            .reportId(1L)
            .instanceId(2L)
            .instanceName("Instance A")
            .language("EN")
            .startDate(LocalDate.of(2026, 1, 1))
            .endDate(LocalDate.of(2026, 1, 31))
            .parameters(params)
            .requestedBy("user1")
            .build();

        assertThat(context1).isEqualTo(context2);
        assertThat(context1.hashCode()).isEqualTo(context2.hashCode());
    }

    @Test
    void testToString() {
        ReportGenerationContext context = ReportGenerationContext.builder()
            .reportId(1L)
            .instanceId(2L)
            .instanceName("Instance A")
            .language("EN")
            .startDate(LocalDate.of(2026, 1, 1))
            .endDate(LocalDate.of(2026, 1, 31))
            .requestedBy("user1")
            .build();

        assertThat(context.toString()).contains("reportId=1", "instanceName=Instance A", "language=EN");
    }
}
