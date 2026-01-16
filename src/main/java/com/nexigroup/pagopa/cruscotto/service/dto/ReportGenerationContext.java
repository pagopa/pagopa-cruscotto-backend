package com.nexigroup.pagopa.cruscotto.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
