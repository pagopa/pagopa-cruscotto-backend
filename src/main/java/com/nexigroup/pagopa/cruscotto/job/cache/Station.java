package com.nexigroup.pagopa.cruscotto.job.cache;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Station {

    private String stationCode;

    private String brokerCode;

    private Integer primitiveVersion;

    private Boolean isPaymentOptionsEnabled;

    private Boolean enabled;

    @Override
    public String toString() {
        return (
            "Station{" +
            "stationCode='" +
            stationCode +
            '\'' +
            ", brokerCode='" +
            brokerCode +
            '\'' +
            ", primitiveVersion=" +
            primitiveVersion +
            ", isPaymentOptionsEnabled=" +
            isPaymentOptionsEnabled +
            ", enabled=" +
            enabled +
            '}'
        );
    }
}
