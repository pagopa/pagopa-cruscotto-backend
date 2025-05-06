package com.nexigroup.pagopa.cruscotto.service.bean;

import com.nexigroup.pagopa.cruscotto.service.validation.FutureOrPresent;
import com.nexigroup.pagopa.cruscotto.service.validation.ValidRangeDate;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@ValidRangeDate.List({@ValidRangeDate(minDate = "analysisPeriodStartDate", maxDate = "analysisPeriodEndDate", pattern = "dd/MM/yyyy", field = "FIELD@analysisPeriodStartDateEndDate"),
					  @ValidRangeDate(minDate = "analysisPeriodStartDate", maxDate = "predictedDateAnalysis", pattern = "dd/MM/yyyy", equalsIsValid = false, field = "FIELD@analysisPeriodStartDatePredictedDateAnalysis"),
					  @ValidRangeDate(minDate = "analysisPeriodEndDate", maxDate = "predictedDateAnalysis", pattern = "dd/MM/yyyy", equalsIsValid = false, field = "FIELD@analysisPeriodEndDatePredictedDateAnalysis")})
@FutureOrPresent(date = "predictedDateAnalysis", pattern = "dd/MM/yyyy", present = false, field = "FIELD@predictedDateAnalysis")
public class InstanceRequestBean {

    private Long id;
    
    @NotBlank
    @Digits(integer = 19, fraction = 0)
    private String partnerId;

    @NotBlank
    @Pattern(regexp = "(0[1-9]|[12][0-9]|3[01])/(0[1-9]|1[012])/[0-9]{4}")
    private String predictedDateAnalysis;

    @NotBlank
    @Pattern(regexp = "(0[1-9]|[12][0-9]|3[01])/(0[1-9]|1[012])/[0-9]{4}")
    private String analysisPeriodStartDate;

    @NotBlank
    @Pattern(regexp = "(0[1-9]|[12][0-9]|3[01])/(0[1-9]|1[012])/[0-9]{4}")
    private String analysisPeriodEndDate;
}
