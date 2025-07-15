package com.nexigroup.pagopa.cruscotto.service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AnagPartnerFilterDTO {

	private Long partnerId;

    private Boolean analyzed;

    private Boolean qualified;

//    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private String lastAnalysisDate;

//    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private String analysisPeriodStartDate;

//    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private String analysisPeriodEndDate;

    private Boolean showNotActive;

}

