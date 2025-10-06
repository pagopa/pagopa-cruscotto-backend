package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.enumeration.ModuleCode;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import com.nexigroup.pagopa.cruscotto.service.dto.InstanceDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.InstanceModuleDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiConfigurationDTO;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@ExtendWith(MockitoExtension.class)
class KpiB4DataServiceImplTest {

    private KpiB4DataServiceImpl kpiB4DataService;

    @BeforeEach
    void setUp() {
        kpiB4DataService = new KpiB4DataServiceImpl();
    }

    @Test
    void saveKpiB4Results_shouldNotThrowException() {
        // Given
        InstanceDTO instanceDTO = createTestInstanceDTO();
        InstanceModuleDTO instanceModuleDTO = createTestInstanceModuleDTO();
        KpiConfigurationDTO kpiConfigurationDTO = createTestKpiConfigurationDTO();
        LocalDate analysisDate = LocalDate.now();
        OutcomeStatus outcome = OutcomeStatus.OK;

        // When & Then
        assertDoesNotThrow(() -> 
            kpiB4DataService.saveKpiB4Results(instanceDTO, instanceModuleDTO, 
                                            kpiConfigurationDTO, analysisDate, outcome)
        );
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