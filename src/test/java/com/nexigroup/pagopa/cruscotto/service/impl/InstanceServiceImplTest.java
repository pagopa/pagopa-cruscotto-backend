package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.*;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.*;
import com.nexigroup.pagopa.cruscotto.repository.AnagPartnerRepository;
import com.nexigroup.pagopa.cruscotto.repository.InstanceRepository;
import com.nexigroup.pagopa.cruscotto.repository.ModuleRepository;
import com.nexigroup.pagopa.cruscotto.security.SecurityUtils;
import com.nexigroup.pagopa.cruscotto.service.AuthUserService;
import com.nexigroup.pagopa.cruscotto.service.GenericServiceException;
import com.nexigroup.pagopa.cruscotto.service.bean.InstanceRequestBean;
import com.nexigroup.pagopa.cruscotto.service.dto.AuthUserDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.InstanceDTO;
import com.nexigroup.pagopa.cruscotto.service.mapper.InstanceMapper;
import com.nexigroup.pagopa.cruscotto.service.qdsl.QueryBuilder;
import com.nexigroup.pagopa.cruscotto.service.util.UserUtils;
import com.querydsl.core.types.*;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQuery;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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
    @Mock private JPQLQuery<Instance> jpqlQuery;
    @Mock private JPQLQuery<InstanceDTO> jpqlDtoQuery;
    @Mock private AuthUserService authUserService;

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
        when(moduleRepository.findAllByStatus(ModuleStatus.ATTIVO)).thenReturn(Collections.emptyList());
        when(instanceRepository.save(any(Instance.class))).thenAnswer(inv -> inv.getArgument(0));
        when(instanceMapper.toDto(any(Instance.class))).thenReturn(new InstanceDTO());

        // when
        InstanceDTO dto = service.saveNew(request);

        // then
        assertNotNull(dto);
        verify(instanceRepository).save(any(Instance.class));
    }

    @Test
    void saveNew_shouldThrow_whenPartnerNotFound() {
        InstanceRequestBean request = new InstanceRequestBean();
        request.setPartnerId("99");
        request.setPredictedDateAnalysis("01/01/2025");
        request.setAnalysisPeriodStartDate("01/01/2025");
        request.setAnalysisPeriodEndDate("02/01/2025");

        when(anagPartnerRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(GenericServiceException.class, () -> service.saveNew(request));
    }

    @Test
    void update_shouldUpdate_whenInstanceInBozza() {
        Instance instance = new Instance();
        instance.setId(1L);
        instance.setStatus(InstanceStatus.BOZZA);

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
        when(instanceRepository.save(any(Instance.class))).thenReturn(instance);
        when(instanceMapper.toDto(any(Instance.class))).thenReturn(new InstanceDTO());

        try (MockedStatic<SecurityUtils> utilities = mockStatic(SecurityUtils.class)) {
            utilities.when(SecurityUtils::getCurrentUserLogin).thenReturn(Optional.of("user"));

            InstanceDTO dto = service.update(req);

            assertNotNull(dto);
            verify(instanceRepository).save(instance);
        }
    }

    @Test
    void update_shouldThrow_whenInstanceNotFound() {
        InstanceRequestBean req = new InstanceRequestBean();
        req.setId(999L);

        when(instanceRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(GenericServiceException.class, () -> service.update(req));
    }

    @Test
    void delete_shouldRemove_whenInstanceInBozza() {
        Instance instance = new Instance();
        instance.setId(1L);
        instance.setStatus(InstanceStatus.BOZZA);

        when(instanceRepository.findById(1L)).thenReturn(Optional.of(instance));
        when(instanceMapper.toDto(instance)).thenReturn(new InstanceDTO());

        try (MockedStatic<SecurityUtils> utilities = mockStatic(SecurityUtils.class)) {
            utilities.when(SecurityUtils::getCurrentUserLogin).thenReturn(Optional.of("user"));

            InstanceDTO dto = service.delete(1L);

            assertNotNull(dto);
            verify(instanceRepository).deleteById(1L);
        }
    }

    @Test
    void delete_shouldThrow_whenNotFound() {
        when(instanceRepository.findById(100L)).thenReturn(Optional.empty());

        assertThrows(GenericServiceException.class, () -> service.delete(100L));
    }

    @Test
    void updateStatus_shouldToggleBetweenBozzaAndPianificata() {
        Instance instance = new Instance();
        instance.setId(1L);
        instance.setStatus(InstanceStatus.BOZZA);

        when(instanceRepository.findById(1L)).thenReturn(Optional.of(instance));
        when(instanceRepository.save(any(Instance.class))).thenReturn(instance);
        when(instanceMapper.toDto(instance)).thenReturn(new InstanceDTO());

        try (MockedStatic<SecurityUtils> utilities = mockStatic(SecurityUtils.class)) {
            utilities.when(SecurityUtils::getCurrentUserLogin).thenReturn(Optional.of("user"));

            InstanceDTO dto = service.updateStatus(1L);

            assertNotNull(dto);
            verify(instanceRepository).save(instance);
        }
    }

    @Test
    void findOne_shouldReturnDto_whenExists() {
        Instance instance = new Instance();
        instance.setId(1L);
        when(instanceRepository.findById(1L)).thenReturn(Optional.of(instance));
        when(instanceMapper.toDto(instance)).thenReturn(new InstanceDTO());

        Optional<InstanceDTO> dto = service.findOne(1L);

        assertTrue(dto.isPresent());
    }

    @Test
    void findAll_shouldReturnEmptyPage_whenNoInstances() {
        @SuppressWarnings("unchecked")
        JPAQuery<Object> rawQuery = mock(JPAQuery.class);

        when(queryBuilder.createQuery()).thenReturn(rawQuery);
        when(rawQuery.from(any(EntityPath.class))).thenReturn(rawQuery);
        when(rawQuery.leftJoin(any(EntityPath.class), any(Path.class))).thenReturn(rawQuery);
        when(rawQuery.where(any(Predicate.class))).thenReturn(rawQuery);
        when(rawQuery.fetch()).thenReturn(Collections.singletonList(0L));
        when(rawQuery.select(any(Expression.class))).thenReturn(rawQuery);
        when(rawQuery.offset(anyLong())).thenReturn(rawQuery);
        when(rawQuery.limit(anyLong())).thenReturn(rawQuery);
        // Fix for strict stubbing
        lenient().when(rawQuery.orderBy(any(OrderSpecifier.class))).thenReturn(rawQuery);
        when(rawQuery.fetch()).thenReturn(Collections.emptyList());

        Page<InstanceDTO> result = service.findAll(
            new com.nexigroup.pagopa.cruscotto.service.filter.InstanceFilter(),
            PageRequest.of(0, 10)
        );

        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
    }

    @Test
    void shouldNotDeleteInstanceInEseguitaStatusWithoutForceDeletePermission() {
        // Given
        Instance instance = createInstance(InstanceStatus.ESEGUITA);
        AuthUserDTO user = new AuthUserDTO();
        user.setAuthorities(Set.of("instance.read", "instance.delete")); // No forceDelete

        when(authUserService.getUserWithAuthorities(any())).thenReturn(Optional.of(user));
        when(instanceRepository.findById(1L)).thenReturn(Optional.of(instance));

        // When & Then
        assertThrows(GenericServiceException.class, () -> {
            service.delete(instance.getId());
        });
    }

    @Test
    void shouldForceDeleteInstanceInAnyStatusWithForceDeletePermission() {
        // Given
        Instance instance = createInstance(InstanceStatus.ESEGUITA);
        AuthUserDTO user = new AuthUserDTO();
        user.setAuthorities(Set.of("instance.read", "instance.delete", "instance.forceDelete"));

        when(authUserService.getUserWithAuthorities(any())).thenReturn(Optional.of(user));
        when(instanceRepository.findById(1L)).thenReturn(Optional.of(instance));

        // When
        InstanceDTO result = service.delete(instance.getId());

        // Then
        assertNotNull(result);
        verify(instanceRepository).deleteById(instance.getId());
    }

    /**
     * Helper method to create test instances
     */
    private Instance createInstance(InstanceStatus status) {
        Instance instance = new Instance();
        instance.setId(1L);
        instance.setStatus(status);
        instance.setInstanceIdentification("TEST-INST-001");
        instance.setCreatedDate(Instant.now());
        instance.setCreatedBy("system");
        
        AnagPartner partner = new AnagPartner();
        partner.setId(1L);
        partner.setFiscalCode("TESTFC123");
        instance.setPartner(partner);
        
        return instance;
    }

}
