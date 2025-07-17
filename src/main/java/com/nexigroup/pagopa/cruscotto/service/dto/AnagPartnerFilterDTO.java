package com.nexigroup.pagopa.cruscotto.service.dto;

import com.nexigroup.pagopa.cruscotto.service.validation.ValidDate;
import com.nexigroup.pagopa.cruscotto.service.validation.ValidRangeDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

@ValidRangeDate.List(
	    {
	        @ValidRangeDate(
	            minDate = "analysisPeriodStartDate",
	            maxDate = "analysisPeriodEndDate",
	            pattern = "yyyy-MM-dd",
        		equalsIsValid = true,
	            field = "FIELD@analysisPeriodStartDate"
	        ),
	        @ValidRangeDate(
        		minDate = "analysisPeriodStartDate",
	            maxDate = "analysisPeriodEndDate",
	            pattern = "yyyy-MM-dd",
	            equalsIsValid = true,
	            field = "FIELD@analysisPeriodEndDate"
	        )
	    }
	)
public class AnagPartnerFilterDTO {

	private Long partnerId;

	private Boolean analyzed;

    private Boolean qualified;

    @ValidDate
    private String lastAnalysisDate;

    @ValidDate
    private String analysisPeriodStartDate;

    @ValidDate
    private String analysisPeriodEndDate;

    private Boolean showNotActive;

}

