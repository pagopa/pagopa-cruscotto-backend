package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.AuthUser;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.AnalysisOutcome;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.AnalysisType;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.ModuleStatus;
import com.nexigroup.pagopa.cruscotto.service.AnagPartnerService;
import com.nexigroup.pagopa.cruscotto.service.InstanceModuleService;
import com.nexigroup.pagopa.cruscotto.service.InstanceService;
import com.nexigroup.pagopa.cruscotto.service.dto.InstanceDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.InstanceModuleDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CalculateStateInstanceServiceImpl Tests")
class CalculateStateInstanceServiceImplTest {

    @Mock
    private InstanceService instanceService;

    @Mock
    private InstanceModuleService instanceModuleService;

    @Mock
    private AnagPartnerService anagPartnerService;

    @InjectMocks
    private CalculateStateInstanceServiceImpl calculateStateInstanceService;

    private AuthUser user;

    @BeforeEach
    void setUp() {
        user = new AuthUser();
        user.setLogin("testUser");
    }

    @Test
    void testUpdateModuleAndInstanceState_ModuleAndInstanceUpdated() {
        // Arrange
        InstanceModuleDTO moduleDTO = new InstanceModuleDTO();
        moduleDTO.setInstanceId(1L);

        InstanceModuleDTO updatedModuleDTO = new InstanceModuleDTO();
        updatedModuleDTO.setInstanceId(1L);

        InstanceDTO instanceDTO = new InstanceDTO();
        instanceDTO.setId(1L);
        instanceDTO.setPartnerId(100L);

        // IMPORTANT: must provide at least one module that triggers update
        InstanceModuleDTO moduleForTest = new InstanceModuleDTO();
        moduleForTest.setModuleCode("MOD1");
        moduleForTest.setStatus(ModuleStatus.ATTIVO);
        moduleForTest.setAnalysisType(AnalysisType.AUTOMATICA);
        moduleForTest.setAutomaticOutcome(AnalysisOutcome.OK);

        when(instanceModuleService.updateInstanceModule(moduleDTO, user)).thenReturn(updatedModuleDTO);
        when(instanceService.findOne(1L)).thenReturn(Optional.of(instanceDTO));
        when(instanceModuleService.findAllByInstanceId(1L)).thenReturn(List.of(moduleForTest));

        // Act
        InstanceModuleDTO result = calculateStateInstanceService.updateModuleAndInstanceState(moduleDTO, user);

        // Assert
        assertEquals(updatedModuleDTO, result);
        verify(instanceService).updateExecuteStateAndLastAnalysis(eq(1L), any(Instant.class), eq(AnalysisOutcome.OK), eq("testUser"));
        verify(anagPartnerService).updateLastAnalysisDate(eq(100L), any(Instant.class));
    }

    @Test
    void testCalculateStateInstance_AllModulesStandby() {
        InstanceDTO instanceDTO = new InstanceDTO();
        instanceDTO.setId(1L);
        instanceDTO.setPartnerId(100L);

        InstanceModuleDTO moduleDTO = new InstanceModuleDTO();
        moduleDTO.setModuleCode("MOD1");
        moduleDTO.setStatus(ModuleStatus.ATTIVO);
        moduleDTO.setAnalysisType(AnalysisType.AUTOMATICA);
        moduleDTO.setAutomaticOutcome(AnalysisOutcome.STANDBY);

        when(instanceModuleService.findAllByInstanceId(1L)).thenReturn(List.of(moduleDTO));

        calculateStateInstanceService.calculateStateInstance(instanceDTO, "testUser");

        // Since all modules are standby, update should NOT happen
        verify(instanceService, never()).updateExecuteStateAndLastAnalysis(anyLong(), any(), any(), anyString());
        verify(anagPartnerService, never()).updateLastAnalysisDate(anyLong(), any());
    }

    @Test
    void testCalculateStateInstance_KOModules() {
        InstanceDTO instanceDTO = new InstanceDTO();
        instanceDTO.setId(1L);
        instanceDTO.setPartnerId(100L);

        InstanceModuleDTO moduleDTO = new InstanceModuleDTO();
        moduleDTO.setModuleCode("MOD1");
        moduleDTO.setStatus(ModuleStatus.ATTIVO);
        moduleDTO.setAnalysisType(AnalysisType.AUTOMATICA);
        moduleDTO.setAutomaticOutcome(AnalysisOutcome.KO);

        when(instanceModuleService.findAllByInstanceId(1L)).thenReturn(List.of(moduleDTO));

        calculateStateInstanceService.calculateStateInstance(instanceDTO, "testUser");

        // KO module should trigger KO outcome
        ArgumentCaptor<AnalysisOutcome> outcomeCaptor = ArgumentCaptor.forClass(AnalysisOutcome.class);
        verify(instanceService).updateExecuteStateAndLastAnalysis(eq(1L), any(), outcomeCaptor.capture(), eq("testUser"));

        assertEquals(AnalysisOutcome.KO, outcomeCaptor.getValue());
    }

    @Test
    void testCalculateStateInstance_ManualOutcomeKO() {
        InstanceDTO instanceDTO = new InstanceDTO();
        instanceDTO.setId(1L);
        instanceDTO.setPartnerId(100L);

        InstanceModuleDTO moduleDTO = new InstanceModuleDTO();
        moduleDTO.setModuleCode("MOD1");
        moduleDTO.setStatus(ModuleStatus.ATTIVO);
        moduleDTO.setAnalysisType(AnalysisType.MANUALE);
        moduleDTO.setManualOutcome(AnalysisOutcome.KO);

        when(instanceModuleService.findAllByInstanceId(1L)).thenReturn(List.of(moduleDTO));

        calculateStateInstanceService.calculateStateInstance(instanceDTO, "testUser");

        ArgumentCaptor<AnalysisOutcome> outcomeCaptor = ArgumentCaptor.forClass(AnalysisOutcome.class);
        verify(instanceService).updateExecuteStateAndLastAnalysis(eq(1L), any(), outcomeCaptor.capture(), eq("testUser"));

        assertEquals(AnalysisOutcome.KO, outcomeCaptor.getValue());
    }

    @Test
    void testCalculateStateInstance_ChangePartnerQualified() {
        InstanceDTO instanceDTO = new InstanceDTO();
        instanceDTO.setId(1L);
        instanceDTO.setPartnerId(100L);
        instanceDTO.setChangePartnerQualified(Boolean.TRUE);

        InstanceModuleDTO moduleDTO = new InstanceModuleDTO();
        moduleDTO.setModuleCode("MOD1");
        moduleDTO.setStatus(ModuleStatus.ATTIVO);
        moduleDTO.setAnalysisType(AnalysisType.AUTOMATICA);
        moduleDTO.setAutomaticOutcome(AnalysisOutcome.OK);

        when(instanceModuleService.findAllByInstanceId(1L)).thenReturn(List.of(moduleDTO));

        calculateStateInstanceService.calculateStateInstance(instanceDTO, "testUser");

        verify(anagPartnerService).changePartnerQualified(100L, Boolean.TRUE);
    }
}
