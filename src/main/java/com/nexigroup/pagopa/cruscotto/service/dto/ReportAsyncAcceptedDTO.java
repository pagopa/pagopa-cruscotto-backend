package com.nexigroup.pagopa.cruscotto.service.dto;

import java.time.OffsetDateTime;
import java.util.List;

import lombok.NoArgsConstructor;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportAsyncAcceptedDTO {
    private Long id;
    private ReportTypeEnum reportType;
    private ReportStatusEnum status;
    private OffsetDateTime requestedDate;
    private OffsetDateTime completionDate;
    private String downloadUrl;
    private Long fileSizeBytes;
    private String fileName;
    private List<String> packageContents;
}
