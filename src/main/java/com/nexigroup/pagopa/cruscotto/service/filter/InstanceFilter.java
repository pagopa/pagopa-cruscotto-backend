package com.nexigroup.pagopa.cruscotto.service.filter;

import com.nexigroup.pagopa.cruscotto.domain.enumeration.InstanceStatus;
import com.nexigroup.pagopa.cruscotto.service.validation.ValidRangeDate;

import java.io.Serial;
import java.io.Serializable;

import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ValidRangeDate.List(
    {
        @ValidRangeDate(
            minDate = " predictedAnalysisStartDate",
            maxDate = " predictedAnalysisEndDate",
            pattern = "dd/MM/yyyy",
            field = "FIELD@predictedAnalysisStartDateEndDate"
        ),
        @ValidRangeDate(
            minDate = "analysisStartDate",
            maxDate = "analysisEndDate",
            pattern = "dd/MM/yyyy",
            equalsIsValid = false,
            field = "FIELD@analysisStartDateEndDate"
        ),
    }
)
public class InstanceFilter implements Serializable {

	@Serial
	private static final long serialVersionUID = -5682277704081342570L;

	@Pattern(regexp = "^[0-9]{0,25}$")
    private String partnerId;

    private InstanceStatus status;

    @Pattern(regexp = "(0[1-9]|[12][0-9]|3[01])/(0[1-9]|1[012])/[0-9]{4}")
    private String predictedAnalysisStartDate;

    @Pattern(regexp = "(0[1-9]|[12][0-9]|3[01])/(0[1-9]|1[012])/[0-9]{4}")
    private String predictedAnalysisEndDate;

    @Pattern(regexp = "(0[1-9]|[12][0-9]|3[01])/(0[1-9]|1[012])/[0-9]{4}")
    private String analysisStartDate;

    @Pattern(regexp = "(0[1-9]|[12][0-9]|3[01])/(0[1-9]|1[012])/[0-9]{4}")
    private String analysisEndDate;

    @Override
    public String toString() {
        return (
            "InstanceFilter{" +
            "partnerId='" +
            partnerId +
            '\'' +
            ", status=" +
            status +
            ", predictedAnalysisStartDate='" +
            predictedAnalysisStartDate +
            '\'' +
            ", predictedAnalysisEndDate='" +
            predictedAnalysisEndDate +
            '\'' +
            ", analysisStartDate='" +
            analysisStartDate +
            '\'' +
            ", analysisEndDate='" +
            analysisEndDate +
            '\'' +
            '}'
        );
    }
}
