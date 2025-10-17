package com.nexigroup.pagopa.cruscotto.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.time.LocalDate;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * A DTO for the PagoPa API Log data for KPI B.4 drilldown analysis.
 * Maps to OpenAPI PagopaAPILogDTO specification.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagopaAPILogDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("partnerFiscalCode")
    private String cfPartner;

    @JsonProperty("dataDate")
    private LocalDate date;

    @JsonProperty("stationCode")
    private String station;

    @JsonProperty("fiscalCode")
    private String cfEnte;

    @JsonProperty("api")
    private String api;

    @JsonProperty("totalRequests")
    private Integer totReq;

    @JsonProperty("okRequests")
    private Integer reqOk;

    @JsonProperty("koRequests")
    private Integer reqKo;
}