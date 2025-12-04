package com.nexigroup.pagopa.cruscotto.security;

import static org.junit.jupiter.api.Assertions.*;

import com.nexigroup.pagopa.cruscotto.config.Constants;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

class SpringSecurityAuditorAwareTest {

    private SpringSecurityAuditorAware auditorAware;

    @BeforeEach
    void setUp() {
        auditorAware = new SpringSecurityAuditorAware();
    }

    @Test
    void testGetCurrentAuditor_UserPresent() {
        try (MockedStatic<SecurityUtils> utilities = Mockito.mockStatic(SecurityUtils.class)) {
            utilities.when(SecurityUtils::getCurrentUserLogin)
                .thenReturn(Optional.of("testUser"));

            Optional<String> auditor = auditorAware.getCurrentAuditor();

            assertTrue(auditor.isPresent());
            assertEquals("testUser", auditor.orElseThrow());
        }
    }

    @Test
    void testGetCurrentAuditor_UserAbsent() {
        try (MockedStatic<SecurityUtils> utilities = Mockito.mockStatic(SecurityUtils.class)) {
            utilities.when(SecurityUtils::getCurrentUserLogin)
                .thenReturn(Optional.empty());

            Optional<String> auditor = auditorAware.getCurrentAuditor();

            assertTrue(auditor.isPresent());
            assertEquals(Constants.SYSTEM, auditor.orElseThrow());
        }
    }
}
