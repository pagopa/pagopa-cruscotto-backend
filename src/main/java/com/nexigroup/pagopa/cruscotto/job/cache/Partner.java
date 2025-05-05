package com.nexigroup.pagopa.cruscotto.job.cache;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class Partner {

    private String brokerCode;

    private Boolean enabled;

    private String description;

    private Boolean extendedFaultBean;

    @Override
    public String toString() {
        return (
            "Partner{" +
            "brokerCode='" +
            brokerCode +
            '\'' +
            ", enabled=" +
            enabled +
            ", description='" +
            description +
            '\'' +
            ", extendedFaultBean=" +
            extendedFaultBean +
            '}'
        );
    }
}
