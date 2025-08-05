package com.nexigroup.pagopa.cruscotto.job.cache;

import java.util.Map;

public class CreditorInstitutionStationsResponse {
    private Map<String, CreditorInstitutionStation> creditorInstitutionStations;

    public Map<String, CreditorInstitutionStation> getCreditorInstitutionStations() {
        return creditorInstitutionStations;
    }

    public void setCreditorInstitutionStations(Map<String, CreditorInstitutionStation> creditorInstitutionStations) {
        this.creditorInstitutionStations = creditorInstitutionStations;
    }
}
