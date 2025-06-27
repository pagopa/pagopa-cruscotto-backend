package com.nexigroup.pagopa.cruscotto.service.validation;

import com.nexigroup.pagopa.cruscotto.domain.enumeration.AuthenticationType;
import com.nexigroup.pagopa.cruscotto.security.SecurityUtils;
import com.nexigroup.pagopa.cruscotto.service.AuthGroupService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class ValidAuthGroupValidator implements ConstraintValidator<ValidAuthGroup, Long> {

    private final Logger logger = LoggerFactory.getLogger(ValidAuthGroupValidator.class);

    @Autowired
    private AuthGroupService authGroupService;

    @Override
    public boolean isValid(Long valueForValidation, ConstraintValidatorContext constraintValidatorContext) {
        if (valueForValidation == null) return true;

        String userLogin = SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new RuntimeException("Current user login not found"));

        AuthenticationType authenticationType = SecurityUtils.getAuthenticationTypeUserLogin()
            .orElseThrow(() -> new RuntimeException("Authentication Type not found"));

        return authGroupService.isAuthGroupValid(userLogin, authenticationType, valueForValidation);
    }
}
