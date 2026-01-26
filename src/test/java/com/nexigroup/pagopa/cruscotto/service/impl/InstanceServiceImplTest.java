package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.*;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.*;
import com.nexigroup.pagopa.cruscotto.repository.AnagPartnerRepository;
import com.nexigroup.pagopa.cruscotto.repository.InstanceRepository;
import com.nexigroup.pagopa.cruscotto.repository.ModuleRepository;
import com.nexigroup.pagopa.cruscotto.security.SecurityUtils;
import com.nexigroup.pagopa.cruscotto.service.AnagPartnerService;
import com.nexigroup.pagopa.cruscotto.service.AuthUserService;
import com.nexigroup.pagopa.cruscotto.service.bean.InstanceRequestBean;
import com.nexigroup.pagopa.cruscotto.service.dto.AuthUserDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.InstanceDTO;
import com.nexigroup.pagopa.cruscotto.service.mapper.InstanceMapper;
import com.nexigroup.pagopa.cruscotto.service.qdsl.QueryBuilder;
import com.nexigroup.pagopa.cruscotto.service.util.UserUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("InstanceServiceImpl Tests")
class InstanceServiceImplTest {

    @Mock private InstanceRepository instanceRepository;
    @Mock private AnagPartnerRepository anagPartnerRepository;
    @Mock private ModuleRepository moduleRepository;
    @Mock private InstanceMapper instanceMapper;
    @Mock private UserUtils userUtils;
    @Mock private QueryBuilder queryBuilder;
    @Mock private AuthUserService authUserService;
    @Mock private AnagPartnerService anagPartnerService;

    @InjectMocks private InstanceServiceImpl service;

    @Test
    void saveNew_shouldCreateInstance_whenValid() {
        // given
        InstanceRequestBean request = new InstanceRequestBean();
        request.setPartnerId("1");
        request.setPredictedDateAnalysis("01/01/2025");
        request.setAnalysisPeriodStartDate("01/01/2025");
        request.setAnalysisPeriodEndDate("02/01/2025");
        request.setChangePartnerQualified("true");

        AnagPartner partner = new AnagPartner();
        partner.setId(1L);
        partner.setFiscalCode("FISC123");

        AuthUser user = new AuthUser();
        user.setLogin("tester");

        when(anagPartnerRepository.findById(1L)).thenReturn(Optional.of(partner));
        when(userUtils.getLoggedUser()).thenReturn(user);
        when(moduleRepository.findAllByStatus(ModuleStatus.ATTIVO))
            .thenReturn(Collections.emptyList());
        when(instanceRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(instanceMapper.toDto((Instance) any())).thenReturn(new InstanceDTO());

        // when
        InstanceDTO dto = service.saveNew(request);

        // then
        assertNotNull(dto);
        verify(instanceRepository).save(any(Instance.class));
    }

    @Test
    void update_shouldUpdate_whenInstanceInBozza() {
        Instance instance = createInstance(InstanceStatus.BOZZA);

        InstanceRequestBean req = new InstanceRequestBean();
        req.setId(1L);
        req.setPartnerId("1");
        req.setPredictedDateAnalysis("01/01/2025");
        req.setAnalysisPeriodStartDate("01/01/2025");
        req.setAnalysisPeriodEndDate("02/01/2025");

        AnagPartner partner = new AnagPartner();
        partner.setId(1L);

        when(instanceRepository.findById(1L)).thenReturn(Optional.of(instance));
        when(anagPartnerRepository.findById(1L)).thenReturn(Optional.of(partner));
        when(instanceRepository.save(instance)).thenReturn(instance);
        when(instanceMapper.toDto(instance)).thenReturn(new InstanceDTO());

        try (MockedStatic<SecurityUtils> sec = mockStatic(SecurityUtils.class)) {
            sec.when(SecurityUtils::getCurrentUserLogin).thenReturn(Optional.of("user"));
            assertNotNull(service.update(req));
        }
    }

    @Test
    void delete_shouldRemove_whenInstanceInBozza() {
        Instance instance = createInstance(InstanceStatus.BOZZA);

        when(instanceRepository.findById(1L)).thenReturn(Optional.of(instance));
        when(instanceMapper.toDto(instance)).thenReturn(new InstanceDTO());

        AuthUserDTO user = new AuthUserDTO();
        user.setAuthorities(Set.of("instance.delete"));

        try (MockedStatic<SecurityUtils> sec = mockStatic(SecurityUtils.class)) {
            sec.when(SecurityUtils::getCurrentUserLogin).thenReturn(Optional.of("user"));
            sec.when(SecurityUtils::getAuthenticationTypeUserLogin)
                .thenReturn(Optional.of(AuthenticationType.FORM_LOGIN));

            when(authUserService.getUserWithAuthorities(any()))
                .thenReturn(Optional.of(user));

            service.delete(1L);
            verify(instanceRepository).deleteById(1L);
        }
    }

    private Instance createInstance(InstanceStatus status) {
        Instance instance = new Instance();
        instance.setId(1L);
        instance.setStatus(status);
        instance.setPredictedDateAnalysis(LocalDate.of(2025, 1, 1));
        instance.setAnalysisPeriodStartDate(LocalDate.of(2025, 1, 1));
        instance.setAnalysisPeriodEndDate(LocalDate.of(2025, 1, 2));
        return instance;
    }
}
 