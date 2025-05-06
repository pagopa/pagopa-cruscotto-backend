package com.nexigroup.pagopa.cruscotto.service.util;

import org.springframework.stereotype.Service;

import com.nexigroup.pagopa.cruscotto.domain.AuthUser;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.AuthenticationType;
import com.nexigroup.pagopa.cruscotto.repository.AuthUserRepository;
import com.nexigroup.pagopa.cruscotto.security.SecurityUtils;


@Service
public class UserUtils {
	
    private final AuthUserRepository authUserRepository;

    public UserUtils(AuthUserRepository authUserRepository) {
        this.authUserRepository = authUserRepository;
    }


    public AuthUser getLoggedUser() {   
    	String userLogin = SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new RuntimeException("Current user login not found"));
        AuthenticationType authenticationType = SecurityUtils.getAuthenticationTypeUserLogin() .orElseThrow(() -> new RuntimeException("Authentication Type not found"));
    	return authUserRepository.findOneByLoginAndNotDeleted(userLogin, authenticationType).orElseThrow(() -> new RuntimeException("Current logged user not found"));
    }
}
