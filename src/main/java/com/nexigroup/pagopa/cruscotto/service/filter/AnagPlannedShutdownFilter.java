package com.nexigroup.pagopa.cruscotto.service.filter;

import com.nexigroup.pagopa.cruscotto.domain.enumeration.TypePlanned;
import com.nexigroup.pagopa.cruscotto.service.validation.ValidEnum;
import com.nexigroup.pagopa.cruscotto.service.validation.ValidRangeDate;

import java.io.Serial;
import java.io.Serializable;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@ValidRangeDate(
    minDate = "shutdownStartDate",
    maxDate = "shutdownEndDate",
    pattern = "dd/MM/yyyy",
    field = "FIELD@shutdownStartDateEndDate"
)
public class AnagPlannedShutdownFilter implements Serializable {

	@Serial
	private static final long serialVersionUID = -5601562316832913296L;

	@Pattern(regexp = "^[0-9]{0,25}$")
    private String partnerId;

    @ValidEnum(enumClass = TypePlanned.class)
    private String typePlanned;

    @NotNull
    private String year;

    @Pattern(regexp = "(0[1-9]|[12][0-9]|3[01])/(0[1-9]|1[012])/[0-9]{4}")
    private String shutdownStartDate;

    @Pattern(regexp = "(0[1-9]|[12][0-9]|3[01])/(0[1-9]|1[012])/[0-9]{4}")
    private String shutdownEndDate;
}
