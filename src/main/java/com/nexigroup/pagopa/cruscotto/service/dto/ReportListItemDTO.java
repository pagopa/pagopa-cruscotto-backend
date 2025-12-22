package com.nexigroup.pagopa.cruscotto.service.dto;

import java.time.OffsetDateTime;

import lombok.NoArgsConstructor;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportListItemDTO {
    private Long id;
    private String instanceName;
    private ReportTypeEnum reportType;
    private ReportStatusEnum status;
    private OffsetDateTime requestedDate;
    private Long fileSizeBytes;
    private String fileName;
}
