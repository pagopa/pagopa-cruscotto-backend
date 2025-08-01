package com.nexigroup.pagopa.cruscotto.job.cache;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.nexigroup.pagopa.cruscotto.service.validation.ValidationGroups;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreditorInstitutionStation {

    @NotNull(groups = { ValidationGroups.RegistryJob.class })
    private String creditorInstitutionCode;

    @NotNull(groups = { ValidationGroups.RegistryJob.class })
    private String stationCode;
    private Boolean aca;
    private Boolean standin;
}
