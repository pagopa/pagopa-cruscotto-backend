package com.nexigroup.pagopa.cruscotto.service.dto;

import java.time.OffsetDateTime;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportStatusBaseDTO {
    private Long id;
    private ReportStatusEnum status;
    private Integer progress; // 0..100
    private String statusMessage;
    private Integer estimatedTimeRemaining;
    private Integer retryCount;
    private OffsetDateTime lastRetryDate;
}
