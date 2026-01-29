package com.nexigroup.pagopa.cruscotto.service.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportGenerationRequestDTO {
    @NotNull
    @NotEmpty
    private List<Long> instanceIds;
    private ReportTypeEnum reportType;
    private String variant;
    @NotBlank
    private String language;
    private LocalDate startDate;
    private LocalDate endDate;
    private Map<String, Object> parameters;
}
