package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.enumeration.ModuleCode;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import com.nexigroup.pagopa.cruscotto.repository.InstanceRepository;
import com.nexigroup.pagopa.cruscotto.repository.KpiB4DetailResultRepository;
import com.nexigroup.pagopa.cruscotto.service.KpiB4Service;
import com.nexigroup.pagopa.cruscotto.service.dto.InstanceDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.InstanceModuleDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiConfigurationDTO;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class KpiB4DataServiceImplTest {

    @Mock
    private KpiB4Service kpiB4Service;
    
    @Mock
    private InstanceRepository instanceRepository;
    
    @Mock
    private KpiB4DetailResultRepository kpiB4DetailResultRepository;

    private KpiB4DataServiceImpl kpiB4DataService;

    @BeforeEach
    void setUp() {
        kpiB4DataService = new KpiB4DataServiceImpl(kpiB4Service, instanceRepository, kpiB4DetailResultRepository);
    }

    @Test
    void saveKpiB4Results_shouldReturnOutcome() {
        // Given
        InstanceDTO instanceDTO = createTestInstanceDTO();
        InstanceModuleDTO instanceModuleDTO = createTestInstanceModuleDTO();
        KpiConfigurationDTO kpiConfigurationDTO = createTestKpiConfigurationDTO();
        LocalDate analysisDate = LocalDate.now();
        OutcomeStatus outcome = OutcomeStatus.OK;

        // When & Then
        OutcomeStatus result = assertDoesNotThrow(() -> 
            kpiB4DataService.saveKpiB4Results(instanceDTO, instanceModuleDTO, 
                                            kpiConfigurationDTO, analysisDate, outcome)
        );
        
        // Verify that a result is returned
        assertNotNull(result);
    }

    private InstanceDTO createTestInstanceDTO() {
        InstanceDTO instanceDTO = new InstanceDTO();
        instanceDTO.setId(1L);
        instanceDTO.setInstanceIdentification("TEST_INSTANCE");
        return instanceDTO;
    }

    private InstanceModuleDTO createTestInstanceModuleDTO() {
        InstanceModuleDTO instanceModuleDTO = new InstanceModuleDTO();
        instanceModuleDTO.setId(1L);
        instanceModuleDTO.setInstanceId(1L);
        instanceModuleDTO.setModuleCode(ModuleCode.B4.code);
        return instanceModuleDTO;
    }

    private KpiConfigurationDTO createTestKpiConfigurationDTO() {
        KpiConfigurationDTO kpiConfigDTO = new KpiConfigurationDTO();
        kpiConfigDTO.setId(1L);
        kpiConfigDTO.setModuleId(1L);
        kpiConfigDTO.setModuleCode(ModuleCode.B4.code);
        return kpiConfigDTO;
    }
}