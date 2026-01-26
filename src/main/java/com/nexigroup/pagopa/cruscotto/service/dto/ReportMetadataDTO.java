package com.nexigroup.pagopa.cruscotto.service.dto;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportMetadataDTO {
    private Long id;
    private Long instanceId;
    private String instanceName;
    private Configuration configuration;
    private ReportStatusEnum status;
    private String language;
    private LocalDate startDate;
    private LocalDate endDate;
    private OffsetDateTime requestedDate;
    private OffsetDateTime generationStartDate;
    private OffsetDateTime completionDate;
    private String requestedBy;
    private String downloadUrl;
    private Long fileSizeBytes;
    private String fileName;
    private List<String> packageContents;
    private String errorMessage;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Configuration {
        private Boolean includeSummary;
        private Boolean includeKoKpisDetail;
        private Boolean includeAllKpisDetail;
        private Boolean includeDrilldownExcel;
    }
}
