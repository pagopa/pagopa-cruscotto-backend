package com.nexigroup.pagopa.cruscotto.job.cache;

import java.util.Map;

public class CreditorInstitutionsResponse {
    private Map<String, CreditorInstitution> creditorInstitutions;

    public Map<String, CreditorInstitution> getCreditorInstitutions() {
        return creditorInstitutions;
    }

    public void setCreditorInstitutions(Map<String, CreditorInstitution> creditorInstitutions) {
        this.creditorInstitutions = creditorInstitutions;
    }
}
