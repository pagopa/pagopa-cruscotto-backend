package com.nexigroup.pagopa.cruscotto.service.bean;

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
@ValidRangeDate(minDate = "shutdownStartDate", maxDate = "shutdownEndDate", pattern = "dd/MM/yyyy", field = "FIELD@shutdownPeriodStartDateEndDate")
public class ShutdownRequestBean {

    private Long id;

    @NotBlank
    @Digits(integer = 19, fraction = 0)
    private String partnerId;

    @NotBlank
    @Pattern(regexp = "(0[1-9]|[12][0-9]|3[01])/(0[1-9]|1[012])/[0-9]{4}")
    private String shutdownStartDate;

    @NotBlank
    @Pattern(regexp = "(0[1-9]|[12][0-9]|3[01])/(0[1-9]|1[012])/[0-9]{4}")
    private String shutdownEndDate;

    @NotBlank
    @Digits(integer = 19, fraction = 0)
    private String stationId;
}
