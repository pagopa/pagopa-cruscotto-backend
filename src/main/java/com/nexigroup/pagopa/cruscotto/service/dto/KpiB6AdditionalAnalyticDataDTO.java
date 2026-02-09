package com.nexigroup.pagopa.cruscotto.service.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KpiB6AdditionalAnalyticDataDTO {

    @JsonProperty("stationCode")
    private String stationCode;

    @JsonProperty("stationStatus")
    private String stationStatus;

    @JsonProperty("paymentOptionsEnabled")
    private Boolean paymentOptionsEnabled;

    @JsonProperty("institutionFiscalCode")
    private String institutionFiscalCode;

    @JsonProperty("partnerFiscalCode")
    private String partnerFiscalCode;
}
