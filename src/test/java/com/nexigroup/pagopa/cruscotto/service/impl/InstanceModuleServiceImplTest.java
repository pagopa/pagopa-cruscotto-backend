package com.nexigroup.pagopa.cruscotto.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.nexigroup.pagopa.cruscotto.domain.AuthUser;
import com.nexigroup.pagopa.cruscotto.domain.Instance;
import com.nexigroup.pagopa.cruscotto.domain.InstanceModule;
import com.nexigroup.pagopa.cruscotto.domain.Module;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.AnalysisOutcome;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.AnalysisType;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.InstanceStatus;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.ModuleStatus;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import com.nexigroup.pagopa.cruscotto.repository.InstanceModuleRepository;
import com.nexigroup.pagopa.cruscotto.service.dto.InstanceModuleDTO;
import com.nexigroup.pagopa.cruscotto.service.qdsl.QueryBuilder;

/**
 * Unit tests for {@link InstanceModuleServiceImpl}.
 * These tests ensure comprehensive coverage of the updateInstanceModule method,
 * including all business rules and edge cases.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("InstanceModuleServiceImpl Tests")
class InstanceModuleServiceImplTest {

    @Mock
    private InstanceModuleRepository instanceModuleRepository;

    @Mock
    private QueryBuilder queryBuilder;

    @InjectMocks
    private InstanceModuleServiceImpl instanceModuleService;

    private InstanceModule instanceModule;
    private Instance instance;
    private Module module;
    private AuthUser currentUser;
    private InstanceModuleDTO inputDTO;

    @BeforeEach
    void setUp() {
        // Setup test data - create fresh objects for each test
        setupTestEntities();
        setupTestDTO();
    }

    private void setupTestEntities() {
        // Create test instance
        instance = new Instance();
        instance.setId(1L);
        instance.setStatus(InstanceStatus.BOZZA);

        // Create test module
        module = new Module();
        module.setId(100L);
        module.setCode("TEST_MODULE");

        // Create test user
        currentUser = new AuthUser();
        currentUser.setId(50L);
        currentUser.setFirstName("John");
        currentUser.setLastName("Doe");
        currentUser.setLogin("john.doe");

        // Create test instance module
        instanceModule = new InstanceModule();
        instanceModule.setId(10L);
        instanceModule.setInstance(instance);
        instanceModule.setModule(module);
        instanceModule.setModuleCode("TEST_MODULE");
        instanceModule.setAnalysisType(AnalysisType.AUTOMATICA);
        instanceModule.setStatus(ModuleStatus.ATTIVO);
        instanceModule.setAllowManualOutcome(true);
        instanceModule.setAutomaticOutcome(AnalysisOutcome.OK);
        instanceModule.setAutomaticOutcomeDate(Instant.now().minusSeconds(3600));
        instanceModule.setManualOutcome(null);
        instanceModule.setManualOutcomeDate(null);
        instanceModule.setManualOutcomeUser(null);
    }

    private void setupTestDTO() {
        inputDTO = new InstanceModuleDTO();
        inputDTO.setId(10L);
    }

    @Nested
    @DisplayName("Entity Not Found Tests")
    class EntityNotFoundTests {

        @Test
        @DisplayName("Should throw IllegalArgumentException when InstanceModule is not found")
        void shouldThrowExceptionWhenInstanceModuleNotFound() {
            // Given
            when(instanceModuleRepository.findById(10L)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> instanceModuleService.updateInstanceModule(inputDTO, currentUser))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("InstanceModule not found");

            verify(instanceModuleRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Status Update Tests")
    class StatusUpdateTests {

        @Test
        @DisplayName("Should successfully update status when instance is in BOZZA status")
        void shouldUpdateStatusWhenInstanceInDraft() {
            // Given
            when(instanceModuleRepository.findById(10L)).thenReturn(Optional.of(instanceModule));
            inputDTO.setStatus(ModuleStatus.NON_ATTIVO);

            // When
            InstanceModuleDTO result = instanceModuleService.updateInstanceModule(inputDTO, currentUser);

            // Then
            ArgumentCaptor<InstanceModule> captor = ArgumentCaptor.forClass(InstanceModule.class);
            verify(instanceModuleRepository).save(captor.capture());
            
            InstanceModule savedEntity = captor.getValue();
            assertThat(savedEntity.getStatus()).isEqualTo(ModuleStatus.NON_ATTIVO);
            assertThat(savedEntity.getLastModifiedDate()).isNotNull();
            
            assertThat(result.getStatus()).isEqualTo(ModuleStatus.NON_ATTIVO);
            assertThat(result.getId()).isEqualTo(10L);
        }

        @Test
        @DisplayName("Should throw RuntimeException when trying to update status with instance not in BOZZA")
        void shouldThrowExceptionWhenUpdatingStatusWithInstanceNotInDraft() {
            // Given
            instance.setStatus(InstanceStatus.PIANIFICATA);
            when(instanceModuleRepository.findById(10L)).thenReturn(Optional.of(instanceModule));
            inputDTO.setStatus(ModuleStatus.NON_ATTIVO);

            // When & Then
            assertThatThrownBy(() -> instanceModuleService.updateInstanceModule(inputDTO, currentUser))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Cannot update status: instance is not in BOZZA status");

            verify(instanceModuleRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should not update status when provided status is same as current")
        void shouldNotUpdateStatusWhenSameAsCurrent() {
            // Given
            when(instanceModuleRepository.findById(10L)).thenReturn(Optional.of(instanceModule));
            inputDTO.setStatus(ModuleStatus.ATTIVO); // Same as current

            // When
            InstanceModuleDTO result = instanceModuleService.updateInstanceModule(inputDTO, currentUser);

            // Then
            verify(instanceModuleRepository, never()).save(any());
            assertThat(result.getStatus()).isEqualTo(ModuleStatus.ATTIVO);
        }

        @Test
        @DisplayName("Should not update anything when status is null")
        void shouldNotUpdateWhenStatusIsNull() {
            // Given
            when(instanceModuleRepository.findById(10L)).thenReturn(Optional.of(instanceModule));
            inputDTO.setStatus(null);

            // When
            InstanceModuleDTO result = instanceModuleService.updateInstanceModule(inputDTO, currentUser);

            // Then
            verify(instanceModuleRepository, never()).save(any());
            assertThat(result.getStatus()).isEqualTo(ModuleStatus.ATTIVO);
        }
    }

    @Nested
    @DisplayName("AllowManualOutcome Update Tests")
    class AllowManualOutcomeUpdateTests {

        @Test
        @DisplayName("Should successfully update allowManualOutcome when instance is in BOZZA status")
        void shouldUpdateAllowManualOutcomeWhenInstanceInDraft() {
            // Given
            when(instanceModuleRepository.findById(10L)).thenReturn(Optional.of(instanceModule));
            inputDTO.setAllowManualOutcome(false);

            // When
            InstanceModuleDTO result = instanceModuleService.updateInstanceModule(inputDTO, currentUser);

            // Then
            ArgumentCaptor<InstanceModule> captor = ArgumentCaptor.forClass(InstanceModule.class);
            verify(instanceModuleRepository).save(captor.capture());
            
            InstanceModule savedEntity = captor.getValue();
            assertThat(savedEntity.isAllowManualOutcome()).isFalse();
            assertThat(savedEntity.getLastModifiedDate()).isNotNull();
            
            assertThat(result.getAllowManualOutcome()).isFalse();
        }

        @Test
        @DisplayName("Should throw RuntimeException when trying to update allowManualOutcome with instance not in BOZZA")
        void shouldThrowExceptionWhenUpdatingAllowManualOutcomeWithInstanceNotInDraft() {
            // Given
            instance.setStatus(InstanceStatus.IN_ESECUZIONE);
            when(instanceModuleRepository.findById(10L)).thenReturn(Optional.of(instanceModule));
            inputDTO.setAllowManualOutcome(false);

            // When & Then
            assertThatThrownBy(() -> instanceModuleService.updateInstanceModule(inputDTO, currentUser))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Cannot update allowManualOutcome: instance is not in BOZZA status");

            verify(instanceModuleRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should not update allowManualOutcome when provided value is same as current")
        void shouldNotUpdateAllowManualOutcomeWhenSameAsCurrent() {
            // Given
            when(instanceModuleRepository.findById(10L)).thenReturn(Optional.of(instanceModule));
            inputDTO.setAllowManualOutcome(true); // Same as current

            // When
            InstanceModuleDTO result = instanceModuleService.updateInstanceModule(inputDTO, currentUser);

            // Then
            verify(instanceModuleRepository, never()).save(any());
            assertThat(result.getAllowManualOutcome()).isTrue();
        }
    }

    @Nested
    @DisplayName("ManualOutcome Update Tests")
    class ManualOutcomeUpdateTests {

        @Test
        @DisplayName("Should successfully update manualOutcome when allowManualOutcome is true")
        void shouldUpdateManualOutcomeWhenAllowed() {
            // Given
            when(instanceModuleRepository.findById(10L)).thenReturn(Optional.of(instanceModule));
            inputDTO.setManualOutcome(AnalysisOutcome.KO);

            // When
            InstanceModuleDTO result = instanceModuleService.updateInstanceModule(inputDTO, currentUser);

            // Then
            ArgumentCaptor<InstanceModule> captor = ArgumentCaptor.forClass(InstanceModule.class);
            verify(instanceModuleRepository).save(captor.capture());
            
            InstanceModule savedEntity = captor.getValue();
            assertThat(savedEntity.getManualOutcome()).isEqualTo(AnalysisOutcome.KO);
            assertThat(savedEntity.getManualOutcomeDate()).isNotNull();
            assertThat(savedEntity.getManualOutcomeUser()).isEqualTo(currentUser);
            assertThat(savedEntity.getLastModifiedDate()).isNotNull();
            
            assertThat(result.getManualOutcome()).isEqualTo(AnalysisOutcome.KO);
            assertThat(result.getAssignedUserId()).isEqualTo(50L);
            assertThat(result.getAssignedUserFirstName()).isEqualTo("John");
            assertThat(result.getAssignedUserLastName()).isEqualTo("Doe");
        }

        @Test
        @DisplayName("Should throw RuntimeException when trying to update manualOutcome with allowManualOutcome false")
        void shouldThrowExceptionWhenUpdatingManualOutcomeNotAllowed() {
            // Given
            instanceModule.setAllowManualOutcome(false);
            when(instanceModuleRepository.findById(10L)).thenReturn(Optional.of(instanceModule));
            inputDTO.setManualOutcome(AnalysisOutcome.KO);

            // When & Then
            assertThatThrownBy(() -> instanceModuleService.updateInstanceModule(inputDTO, currentUser))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Cannot update manualOutcome: allowManualOutcome is false");

            verify(instanceModuleRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should not update manualOutcome when provided value is same as current")
        void shouldNotUpdateManualOutcomeWhenSameAsCurrent() {
            // Given
            instanceModule.setManualOutcome(AnalysisOutcome.KO);
            when(instanceModuleRepository.findById(10L)).thenReturn(Optional.of(instanceModule));
            inputDTO.setManualOutcome(AnalysisOutcome.KO); // Same as current

            // When
            InstanceModuleDTO result = instanceModuleService.updateInstanceModule(inputDTO, currentUser);

            // Then
            verify(instanceModuleRepository, never()).save(any());
            assertThat(result.getManualOutcome()).isEqualTo(AnalysisOutcome.KO);
        }

        @Test
        @DisplayName("Should not update anything when manualOutcome is null")
        void shouldNotUpdateWhenManualOutcomeIsNull() {
            // Given
            when(instanceModuleRepository.findById(10L)).thenReturn(Optional.of(instanceModule));
            inputDTO.setManualOutcome(null);

            // When
            InstanceModuleDTO result = instanceModuleService.updateInstanceModule(inputDTO, currentUser);

            // Then
            verify(instanceModuleRepository, never()).save(any());
            assertThat(result.getManualOutcome()).isNull();
        }

        @Test
        @DisplayName("Should handle manual outcome update when previous user was set")
        void shouldUpdateManualOutcomeWithPreviousUser() {
            // Given
            AuthUser previousUser = new AuthUser();
            previousUser.setId(99L);
            previousUser.setFirstName("Jane");
            previousUser.setLastName("Smith");
            instanceModule.setManualOutcome(AnalysisOutcome.OK);
            instanceModule.setManualOutcomeUser(previousUser);
            
            when(instanceModuleRepository.findById(10L)).thenReturn(Optional.of(instanceModule));
            inputDTO.setManualOutcome(AnalysisOutcome.STANDBY);

            // When
            InstanceModuleDTO result = instanceModuleService.updateInstanceModule(inputDTO, currentUser);

            // Then
            ArgumentCaptor<InstanceModule> captor = ArgumentCaptor.forClass(InstanceModule.class);
            verify(instanceModuleRepository).save(captor.capture());
            
            InstanceModule savedEntity = captor.getValue();
            assertThat(savedEntity.getManualOutcomeUser()).isEqualTo(currentUser);
            assertThat(result.getAssignedUserId()).isEqualTo(50L);
            assertThat(result.getAssignedUserFirstName()).isEqualTo("John");
            assertThat(result.getAssignedUserLastName()).isEqualTo("Doe");
        }
    }

    @Nested
    @DisplayName("Combined Updates Tests")
    class CombinedUpdatesTests {

        @Test
        @DisplayName("Should update multiple fields when all conditions are met")
        void shouldUpdateMultipleFields() {
            // Given - create a fresh InstanceModule for this test
            InstanceModule testInstanceModule = new InstanceModule();
            testInstanceModule.setId(10L);
            testInstanceModule.setInstance(instance);
            testInstanceModule.setModule(module);
            testInstanceModule.setModuleCode("TEST_MODULE");
            testInstanceModule.setAnalysisType(AnalysisType.AUTOMATICA);
            testInstanceModule.setStatus(ModuleStatus.ATTIVO);
            testInstanceModule.setAllowManualOutcome(true); // Ensure this is true
            testInstanceModule.setAutomaticOutcome(AnalysisOutcome.OK);
            testInstanceModule.setAutomaticOutcomeDate(Instant.now().minusSeconds(3600));
            testInstanceModule.setManualOutcome(null);
            testInstanceModule.setManualOutcomeDate(null);
            testInstanceModule.setManualOutcomeUser(null);
            
            when(instanceModuleRepository.findById(10L)).thenReturn(Optional.of(testInstanceModule));
            inputDTO.setStatus(ModuleStatus.NON_ATTIVO);
            inputDTO.setManualOutcome(AnalysisOutcome.STANDBY); // Update manual outcome first while allowManualOutcome is true
            inputDTO.setAllowManualOutcome(false); // Then disable it

            // When
            InstanceModuleDTO result = instanceModuleService.updateInstanceModule(inputDTO, currentUser);

            // Then
            ArgumentCaptor<InstanceModule> captor = ArgumentCaptor.forClass(InstanceModule.class);
            verify(instanceModuleRepository).save(captor.capture());
            
            InstanceModule savedEntity = captor.getValue();
            assertThat(savedEntity.getStatus()).isEqualTo(ModuleStatus.NON_ATTIVO);
            assertThat(savedEntity.isAllowManualOutcome()).isFalse();
            assertThat(savedEntity.getManualOutcome()).isEqualTo(AnalysisOutcome.STANDBY);
            assertThat(savedEntity.getManualOutcomeUser()).isEqualTo(currentUser);
            assertThat(savedEntity.getLastModifiedDate()).isNotNull();
            
            assertThat(result.getStatus()).isEqualTo(ModuleStatus.NON_ATTIVO);
            assertThat(result.getAllowManualOutcome()).isFalse();
            assertThat(result.getManualOutcome()).isEqualTo(AnalysisOutcome.STANDBY);
        }

        @Test
        @DisplayName("Should fail to update manualOutcome when original allowManualOutcome was false")
        void shouldFailToUpdateManualOutcomeWhenOriginallyNotAllowed() {
            // Given
            instanceModule.setAllowManualOutcome(false); // Set original value to false
            when(instanceModuleRepository.findById(10L)).thenReturn(Optional.of(instanceModule));
            inputDTO.setAllowManualOutcome(true); // Try to enable it
            inputDTO.setManualOutcome(AnalysisOutcome.KO); // And update manual outcome

            // When & Then
            assertThatThrownBy(() -> instanceModuleService.updateInstanceModule(inputDTO, currentUser))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Cannot update manualOutcome: allowManualOutcome is false");

            verify(instanceModuleRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should handle mixed valid and invalid updates")
        void shouldHandleMixedValidInvalidUpdates() {
            // Given - instance not in draft status but allowManualOutcome allows manual outcome update
            instance.setStatus(InstanceStatus.ESEGUITA);
            when(instanceModuleRepository.findById(10L)).thenReturn(Optional.of(instanceModule));
            inputDTO.setStatus(ModuleStatus.NON_ATTIVO); // This should fail
            inputDTO.setManualOutcome(AnalysisOutcome.KO); // This should succeed if we get there

            // When & Then - Should fail on status update before getting to manual outcome
            assertThatThrownBy(() -> instanceModuleService.updateInstanceModule(inputDTO, currentUser))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Cannot update status: instance is not in BOZZA status");

            verify(instanceModuleRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("DTO Mapping Tests")
    class DTOMappingTests {

        @Test
        @DisplayName("Should correctly map all fields from entity to DTO")
        void shouldMapAllFieldsCorrectly() {
            // Given
            instanceModule.setManualOutcome(AnalysisOutcome.OK);
            instanceModule.setManualOutcomeUser(currentUser);
            instanceModule.setManualOutcomeDate(Instant.now());
            
            when(instanceModuleRepository.findById(10L)).thenReturn(Optional.of(instanceModule));

            // When - no changes, just mapping
            InstanceModuleDTO result = instanceModuleService.updateInstanceModule(inputDTO, currentUser);

            // Then
            verify(instanceModuleRepository, never()).save(any());
            
            assertThat(result.getId()).isEqualTo(10L);
            assertThat(result.getInstanceId()).isEqualTo(1L);
            assertThat(result.getModuleId()).isEqualTo(100L);
            assertThat(result.getModuleCode()).isEqualTo("TEST_MODULE");
            assertThat(result.getAnalysisType()).isEqualTo(AnalysisType.AUTOMATICA);
            assertThat(result.getAllowManualOutcome()).isTrue();
            assertThat(result.getAutomaticOutcome()).isEqualTo(AnalysisOutcome.OK);
            assertThat(result.getAutomaticOutcomeDate()).isNotNull();
            assertThat(result.getManualOutcome()).isEqualTo(AnalysisOutcome.OK);
            assertThat(result.getManualOutcomeDate()).isNotNull();
            assertThat(result.getStatus()).isEqualTo(ModuleStatus.ATTIVO);
            assertThat(result.getAssignedUserId()).isEqualTo(50L);
            assertThat(result.getAssignedUserFirstName()).isEqualTo("John");
            assertThat(result.getAssignedUserLastName()).isEqualTo("Doe");
        }

        @Test
        @DisplayName("Should handle null manualOutcomeUser in DTO mapping")
        void shouldHandleNullManualOutcomeUser() {
            // Given
            instanceModule.setManualOutcomeUser(null);
            when(instanceModuleRepository.findById(10L)).thenReturn(Optional.of(instanceModule));

            // When
            InstanceModuleDTO result = instanceModuleService.updateInstanceModule(inputDTO, currentUser);

            // Then
            assertThat(result.getAssignedUserId()).isNull();
            assertThat(result.getAssignedUserFirstName()).isNull();
            assertThat(result.getAssignedUserLastName()).isNull();
        }
    }

    @Nested
    @DisplayName("Edge Cases Tests")
    class EdgeCasesTests {

        @Test
        @DisplayName("Should handle all analysis outcomes correctly")
        void shouldHandleAllAnalysisOutcomes() {
            // Test each analysis outcome
            AnalysisOutcome[] outcomes = {AnalysisOutcome.OK, AnalysisOutcome.KO, AnalysisOutcome.STANDBY};
            
            for (AnalysisOutcome outcome : outcomes) {
                // Given
                when(instanceModuleRepository.findById(10L)).thenReturn(Optional.of(instanceModule));
                inputDTO.setManualOutcome(outcome);

                // When
                InstanceModuleDTO result = instanceModuleService.updateInstanceModule(inputDTO, currentUser);

                // Then
                assertThat(result.getManualOutcome()).isEqualTo(outcome);
                
                // Reset for next iteration
                instanceModule.setManualOutcome(null);
                inputDTO.setManualOutcome(null);
            }
        }

        @Test
        @DisplayName("Should handle all instance statuses correctly")
        void shouldHandleAllInstanceStatuses() {
            // Test that non-BOZZA statuses prevent updates
            InstanceStatus[] nonDraftStatuses = {InstanceStatus.PIANIFICATA, InstanceStatus.IN_ESECUZIONE, InstanceStatus.ESEGUITA};
            
            for (InstanceStatus status : nonDraftStatuses) {
                // Given
                instance.setStatus(status);
                when(instanceModuleRepository.findById(10L)).thenReturn(Optional.of(instanceModule));
                inputDTO.setStatus(ModuleStatus.NON_ATTIVO);

                // When & Then
                assertThatThrownBy(() -> instanceModuleService.updateInstanceModule(inputDTO, currentUser))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Cannot update status: instance is not in BOZZA status");
            }
        }

        @Test
        @DisplayName("Should handle different analysis types correctly")
        void shouldHandleDifferentAnalysisTypes() {
            // Given - even though the business rule for AUTOMATICA is commented out, test with different types
            AnalysisType[] types = {AnalysisType.AUTOMATICA, AnalysisType.MANUALE};
            
            for (AnalysisType type : types) {
                instanceModule.setAnalysisType(type);
                when(instanceModuleRepository.findById(10L)).thenReturn(Optional.of(instanceModule));
                inputDTO.setManualOutcome(AnalysisOutcome.OK);

                // When
                InstanceModuleDTO result = instanceModuleService.updateInstanceModule(inputDTO, currentUser);

                // Then - should work for both since the AUTOMATICA check is commented out
                assertThat(result.getAnalysisType()).isEqualTo(type);
                assertThat(result.getManualOutcome()).isEqualTo(AnalysisOutcome.OK);
                
                // Reset
                instanceModule.setManualOutcome(null);
                inputDTO.setManualOutcome(null);
            }
        }
    }

    @Nested
    @DisplayName("Performance and Optimization Tests")
    class PerformanceTests {

        @Test
        @DisplayName("Should not call save when no changes are made")
        void shouldNotSaveWhenNoChanges() {
            // Given
            when(instanceModuleRepository.findById(10L)).thenReturn(Optional.of(instanceModule));
            // DTO with no changes (all fields null or same as current)

            // When
            InstanceModuleDTO result = instanceModuleService.updateInstanceModule(inputDTO, currentUser);

            // Then
            verify(instanceModuleRepository, never()).save(any());
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("Should call save only once when multiple changes are made")
        void shouldSaveOnlyOnceForMultipleChanges() {
            // Given
            when(instanceModuleRepository.findById(10L)).thenReturn(Optional.of(instanceModule));
            inputDTO.setStatus(ModuleStatus.NON_ATTIVO);
            inputDTO.setAllowManualOutcome(false);
            // Not setting manualOutcome since allowManualOutcome will be false

            // When
            instanceModuleService.updateInstanceModule(inputDTO, currentUser);

            // Then
            verify(instanceModuleRepository, times(1)).save(any());
        }
    }

    @Nested
    @DisplayName("UpdateAutomaticOutcome Tests")
    class UpdateAutomaticOutcomeTests {

        @Test
        @DisplayName("Should successfully update automatic outcome to OK")
        void shouldUpdateAutomaticOutcomeToOK() {
            // Given
            when(instanceModuleRepository.findById(10L)).thenReturn(Optional.of(instanceModule));
            when(instanceModuleRepository.save(any(InstanceModule.class))).thenReturn(instanceModule);

            // When
            instanceModuleService.updateAutomaticOutcome(10L, OutcomeStatus.OK);

            // Then
            ArgumentCaptor<InstanceModule> captor = ArgumentCaptor.forClass(InstanceModule.class);
            verify(instanceModuleRepository).save(captor.capture());
            
            InstanceModule savedEntity = captor.getValue();
            assertThat(savedEntity.getAutomaticOutcome()).isEqualTo(AnalysisOutcome.OK);
            assertThat(savedEntity.getAutomaticOutcomeDate()).isNotNull();
        }

        @Test
        @DisplayName("Should successfully update automatic outcome to KO")
        void shouldUpdateAutomaticOutcomeToKO() {
            // Given
            when(instanceModuleRepository.findById(10L)).thenReturn(Optional.of(instanceModule));
            when(instanceModuleRepository.save(any(InstanceModule.class))).thenReturn(instanceModule);

            // When
            instanceModuleService.updateAutomaticOutcome(10L, OutcomeStatus.KO);

            // Then
            ArgumentCaptor<InstanceModule> captor = ArgumentCaptor.forClass(InstanceModule.class);
            verify(instanceModuleRepository).save(captor.capture());
            
            InstanceModule savedEntity = captor.getValue();
            assertThat(savedEntity.getAutomaticOutcome()).isEqualTo(AnalysisOutcome.KO);
            assertThat(savedEntity.getAutomaticOutcomeDate()).isNotNull();
        }

        @Test
        @DisplayName("Should successfully update automatic outcome to STANDBY")
        void shouldUpdateAutomaticOutcomeToStandby() {
            // Given
            when(instanceModuleRepository.findById(10L)).thenReturn(Optional.of(instanceModule));
            when(instanceModuleRepository.save(any(InstanceModule.class))).thenReturn(instanceModule);

            // When
            instanceModuleService.updateAutomaticOutcome(10L, OutcomeStatus.STANDBY);

            // Then
            ArgumentCaptor<InstanceModule> captor = ArgumentCaptor.forClass(InstanceModule.class);
            verify(instanceModuleRepository).save(captor.capture());
            
            InstanceModule savedEntity = captor.getValue();
            assertThat(savedEntity.getAutomaticOutcome()).isEqualTo(AnalysisOutcome.STANDBY);
            assertThat(savedEntity.getAutomaticOutcomeDate()).isNotNull();
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when InstanceModule not found for automatic outcome update")
        void shouldThrowExceptionWhenInstanceModuleNotFoundForAutomaticOutcome() {
            // Given
            when(instanceModuleRepository.findById(10L)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> instanceModuleService.updateAutomaticOutcome(10L, OutcomeStatus.OK))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("InstanceModule not found");

            verify(instanceModuleRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw NullPointerException for null automatic outcome")
        void shouldThrowExceptionForNullAutomaticOutcome() {
            // Given
            when(instanceModuleRepository.findById(10L)).thenReturn(Optional.of(instanceModule));

            // When & Then
            assertThatThrownBy(() -> instanceModuleService.updateAutomaticOutcome(10L, null))
                .isInstanceOf(NullPointerException.class);

            verify(instanceModuleRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException for unsupported automatic outcome")
        void shouldThrowExceptionForUnsupportedAutomaticOutcome() {
            // Given
            when(instanceModuleRepository.findById(10L)).thenReturn(Optional.of(instanceModule));

            // When & Then
            assertThatThrownBy(() -> instanceModuleService.updateAutomaticOutcome(10L, OutcomeStatus.RUNNING))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid automatic outcome");

            verify(instanceModuleRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should set automatic outcome date correctly")
        void shouldSetAutomaticOutcomeDateCorrectly() {
            // Given
            Instant beforeUpdate = Instant.now();
            when(instanceModuleRepository.findById(10L)).thenReturn(Optional.of(instanceModule));
            when(instanceModuleRepository.save(any(InstanceModule.class))).thenReturn(instanceModule);

            // When
            instanceModuleService.updateAutomaticOutcome(10L, OutcomeStatus.OK);

            // Then
            Instant afterUpdate = Instant.now();
            assertThat(instanceModule.getAutomaticOutcomeDate()).isNotNull();
            assertThat(instanceModule.getAutomaticOutcomeDate()).isBetween(beforeUpdate, afterUpdate);
        }
    }

    @Nested
    @DisplayName("FindById Tests")
    class FindByIdTests {

        @Test
        @DisplayName("Should return InstanceModule when found")
        void shouldReturnInstanceModuleWhenFound() {
            // Given
            when(instanceModuleRepository.findById(10L)).thenReturn(Optional.of(instanceModule));

            // When
            Optional<InstanceModule> result = instanceModuleService.findById(10L);

            // Then
            assertThat(result).isPresent();
            assertThat(result.orElseThrow()).isEqualTo(instanceModule);
            verify(instanceModuleRepository).findById(10L);
        }

        @Test
        @DisplayName("Should return empty Optional when not found")
        void shouldReturnEmptyOptionalWhenNotFound() {
            // Given
            when(instanceModuleRepository.findById(10L)).thenReturn(Optional.empty());

            // When
            Optional<InstanceModule> result = instanceModuleService.findById(10L);

            // Then
            assertThat(result).isEmpty();
            verify(instanceModuleRepository).findById(10L);
        }

        @Test
        @DisplayName("Should handle null ID gracefully")
        void shouldHandleNullIdGracefully() {
            // Given
            when(instanceModuleRepository.findById(null)).thenReturn(Optional.empty());

            // When
            Optional<InstanceModule> result = instanceModuleService.findById(null);

            // Then
            assertThat(result).isEmpty();
            verify(instanceModuleRepository).findById(null);
        }
    }
}
