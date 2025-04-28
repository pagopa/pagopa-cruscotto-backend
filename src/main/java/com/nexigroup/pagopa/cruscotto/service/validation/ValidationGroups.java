package com.nexigroup.pagopa.cruscotto.service.validation;


import jakarta.validation.groups.Default;

public class ValidationGroups {
    private ValidationGroups() {
    }

    public interface PlannedShutdownJob extends Default {};
}
