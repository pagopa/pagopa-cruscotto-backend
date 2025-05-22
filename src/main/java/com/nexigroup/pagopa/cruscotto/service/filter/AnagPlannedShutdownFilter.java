package com.nexigroup.pagopa.cruscotto.service.filter;

import com.nexigroup.pagopa.cruscotto.domain.enumeration.TypePlanned;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class AnagPlannedShutdownFilter implements Serializable {

    @Pattern(regexp = "^[0-9]{0,25}$")
    private String partnerId;

    private TypePlanned typePlanned;

    @Override
    public String toString() {
        return "AnagPlannedShutdownFilter{" +
            "partnerId='" + partnerId + '\'' +
            ", typePlanned=" + typePlanned +
            '}';
    }
}
