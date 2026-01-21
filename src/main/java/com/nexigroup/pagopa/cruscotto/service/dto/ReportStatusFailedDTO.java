package com.nexigroup.pagopa.cruscotto.service.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ReportStatusFailedDTO {
    private String lastError;
    private Boolean canRetry;
}
