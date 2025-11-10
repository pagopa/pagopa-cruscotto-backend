package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.*;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.TypePlanned;
import com.nexigroup.pagopa.cruscotto.repository.AnagPartnerRepository;
import com.nexigroup.pagopa.cruscotto.repository.AnagPlannedShutdownRepository;
import com.nexigroup.pagopa.cruscotto.repository.AnagStationRepository;
import com.nexigroup.pagopa.cruscotto.service.GenericServiceException;
import com.nexigroup.pagopa.cruscotto.service.bean.ShutdownRequestBean;
import com.nexigroup.pagopa.cruscotto.service.dto.AnagPlannedShutdownDTO;
import com.nexigroup.pagopa.cruscotto.service.mapper.AnagPlannedShutdownMapper;
import com.nexigroup.pagopa.cruscotto.service.util.UserUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthFunctionServiceImplTest Tests")
class AnagPlannedShutdownServiceImplTest {

    @Mock private AnagPlannedShutdownRepository shutdownRepo;
    @Mock private AnagPartnerRepository partnerRepo;
    @Mock private AnagStationRepository stationRepo;
    @Mock private AnagPlannedShutdownMapper mapper;
    @Mock private UserUtils userUtils;

    @InjectMocks
    private AnagPlannedShutdownServiceImpl service;

    @Test
    void findOne_ShouldReturnDto_WhenExists() {
        AnagPlannedShutdown entity = new AnagPlannedShutdown();
        entity.setId(10L);
        AnagPlannedShutdownDTO dto = new AnagPlannedShutdownDTO();
        dto.setId(10L);

        when(shutdownRepo.findById(10L)).thenReturn(Optional.of(entity));
        when(mapper.toDto(entity)).thenReturn(dto);

        Optional<AnagPlannedShutdownDTO> result = service.findOne(10L);

        AnagPlannedShutdownDTO actualDto = result.orElseThrow(() -> new AssertionError("Expected a DTO"));
        assertThat(actualDto.getId()).isEqualTo(10L);
    }

    @Test
    void saveNew_ShouldPersistAndReturnDto() {
        ShutdownRequestBean bean = new ShutdownRequestBean();
        bean.setPartnerId("1");
        bean.setStationId("2");
        bean.setShutdownStartDate("01-01-2025 10:00:00");
        bean.setShutdownEndDate("01-01-2025 12:00:00");

        AnagPartner partner = new AnagPartner();
        partner.setId(1L);
        AnagStation station = new AnagStation();
        station.setId(2L);

        AuthUser user = new AuthUser();
        user.setLogin("tester");

        AnagPlannedShutdown entity = new AnagPlannedShutdown();
        entity.setId(99L);
        entity.setShutdownStartDate(Instant.now());
        entity.setShutdownEndDate(Instant.now());

        AnagPlannedShutdownDTO dto = new AnagPlannedShutdownDTO();
        dto.setId(99L);

        when(userUtils.getLoggedUser()).thenReturn(user);
        when(partnerRepo.findById(1L)).thenReturn(Optional.of(partner));
        when(stationRepo.findById(2L)).thenReturn(Optional.of(station));
        when(shutdownRepo.save(any())).thenReturn(entity);
        when(mapper.toDto(entity)).thenReturn(dto);

        AnagPlannedShutdownDTO result = service.saveNew(bean);

        assertThat(result.getId()).isEqualTo(99L);
        verify(shutdownRepo).save(any(AnagPlannedShutdown.class));
    }

    @Test
    void saveNew_ShouldThrow_WhenPartnerMissing() {
        ShutdownRequestBean bean = new ShutdownRequestBean();
        bean.setPartnerId("1");
        bean.setStationId("2");
        bean.setShutdownStartDate("01-01-2025 10:00:00");
        bean.setShutdownEndDate("01-01-2025 12:00:00");

        when(partnerRepo.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.saveNew(bean))
            .isInstanceOf(GenericServiceException.class)
            .hasMessageContaining("Partner with id 1 not exist");
    }

    @Test
    void delete_ShouldRemoveEntity_WhenTypeNotProgrammed() {
        AnagPlannedShutdown shutdown = new AnagPlannedShutdown();
        shutdown.setId(5L);
        shutdown.setTypePlanned(TypePlanned.NON_PROGRAMMATO);

        when(shutdownRepo.findById(5L)).thenReturn(Optional.of(shutdown));

        service.delete(5L);

        verify(shutdownRepo).deleteById(5L);
    }

    @Test
    void delete_ShouldThrow_WhenTypeProgrammed() {
        AnagPlannedShutdown shutdown = new AnagPlannedShutdown();
        shutdown.setId(5L);
        shutdown.setTypePlanned(TypePlanned.PROGRAMMATO);

        when(shutdownRepo.findById(5L)).thenReturn(Optional.of(shutdown));

        assertThatThrownBy(() -> service.delete(5L))
            .isInstanceOf(GenericServiceException.class)
            .hasMessageContaining("cannot be deleted");
    }

    @Test
    void update_ShouldModifyAndReturnDto_WhenExists() {
        ShutdownRequestBean bean = new ShutdownRequestBean();
        bean.setId(7L);
        bean.setPartnerId("1");
        bean.setStationId("2");
        bean.setShutdownStartDate("01-01-2025 10:00:00");
        bean.setShutdownEndDate("01-01-2025 12:00:00");

        AnagPlannedShutdown shutdown = new AnagPlannedShutdown();
        shutdown.setId(7L);
        shutdown.setTypePlanned(TypePlanned.NON_PROGRAMMATO);

        AnagPartner partner = new AnagPartner();
        partner.setId(1L);
        AnagStation station = new AnagStation();
        station.setId(2L);

        AnagPlannedShutdownDTO dto = new AnagPlannedShutdownDTO();
        dto.setId(7L);

        when(shutdownRepo.findById(7L)).thenReturn(Optional.of(shutdown));
        when(partnerRepo.findById(1L)).thenReturn(Optional.of(partner));
        when(stationRepo.findById(2L)).thenReturn(Optional.of(station));
        when(mapper.toDto(shutdown)).thenReturn(dto);

        AnagPlannedShutdownDTO result = service.update(bean);

        assertThat(result.getId()).isEqualTo(7L);
    }

    @Test
    void update_ShouldThrow_WhenShutdownNotFound() {
        ShutdownRequestBean bean = new ShutdownRequestBean();
        bean.setId(123L);
        when(shutdownRepo.findById(123L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(bean))
            .isInstanceOf(GenericServiceException.class)
            .hasMessageContaining("not exist");
    }

    @Test
    void update_ShouldThrow_WhenTypeProgrammed() {
        ShutdownRequestBean bean = new ShutdownRequestBean();
        bean.setId(7L);

        AnagPlannedShutdown shutdown = new AnagPlannedShutdown();
        shutdown.setId(7L);
        shutdown.setTypePlanned(TypePlanned.PROGRAMMATO);

        when(shutdownRepo.findById(7L)).thenReturn(Optional.of(shutdown));

        assertThatThrownBy(() -> service.update(bean))
            .isInstanceOf(GenericServiceException.class)
            .hasMessageContaining("cannot be updated");
    }
}
