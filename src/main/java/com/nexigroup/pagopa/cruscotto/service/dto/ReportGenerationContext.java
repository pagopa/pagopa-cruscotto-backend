package com.nexigroup.pagopa.cruscotto.service.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.Map;

@Data
@Builder
public class ReportGenerationContext {
    private Long reportId;
    private Long instanceId;
    private String instanceName;
    private String language;
    private LocalDate startDate;
    private LocalDate endDate;
    private Map<String, Object> parameters;
    private String requestedBy;
}
