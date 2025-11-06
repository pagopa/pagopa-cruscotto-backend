package com.nexigroup.pagopa.cruscotto.service.dto;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.ModuleStatus;

class InstanceModuleUpdateStatusDTOTest {

    @Test
    void testGetterAndSetter() {
        InstanceModuleUpdateStatusDTO dto = new InstanceModuleUpdateStatusDTO();

        dto.setId(1L);
        dto.setAllowManualOutcome(true);
        dto.setStatus(ModuleStatus.ATTIVO);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getAllowManualOutcome()).isTrue();
        assertThat(dto.getStatus()).isEqualTo(ModuleStatus.ATTIVO);
    }

    @Test
    void testEqualsAndHashCode() {
        InstanceModuleUpdateStatusDTO dto1 = new InstanceModuleUpdateStatusDTO();
        InstanceModuleUpdateStatusDTO dto2 = new InstanceModuleUpdateStatusDTO();

        dto1.setId(1L);
        dto2.setId(1L);

        dto1.setAllowManualOutcome(true);
        dto2.setAllowManualOutcome(true);

        dto1.setStatus(ModuleStatus.ATTIVO);
        dto2.setStatus(ModuleStatus.ATTIVO);

        // equals
        assertThat(dto1).isEqualTo(dto2)
                        .hasSameHashCodeAs(dto2);
    }

    @Test
    void testNotEquals() {
        InstanceModuleUpdateStatusDTO dto1 = new InstanceModuleUpdateStatusDTO();
        InstanceModuleUpdateStatusDTO dto2 = new InstanceModuleUpdateStatusDTO();

        dto1.setId(1L);
        dto2.setId(2L);

        assertThat(dto1).isNotEqualTo(dto2);
    }
}
