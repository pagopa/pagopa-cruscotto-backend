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
public class CreditorInstitution {
    private String creditorInstitutionCode;
    private Boolean enabled;
    private String businessName;

    public String getCreditorInstitutionCode() {
        return creditorInstitutionCode;
    }
    public void setCreditorInstitutionCode(String creditorInstitutionCode) {
        this.creditorInstitutionCode = creditorInstitutionCode;
    }
    public Boolean getEnabled() {
        return enabled;
    }
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
    public String getBusinessName() {
        return businessName;
    }
    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }
}
