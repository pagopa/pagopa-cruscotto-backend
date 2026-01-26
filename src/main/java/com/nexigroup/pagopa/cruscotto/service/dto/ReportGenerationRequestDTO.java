package com.nexigroup.pagopa.cruscotto.service.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportGenerationRequestDTO {
    private List<Long> instanceIds;
    private ReportTypeEnum reportType;
    private String variant;
    private String language;
    private LocalDate startDate;
    private LocalDate endDate;
    private Map<String, Object> parameters;
}
