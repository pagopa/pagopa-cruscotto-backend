package com.nexigroup.pagopa.cruscotto.service.dto;

import static org.junit.jupiter.api.Assertions.*;

import com.nexigroup.pagopa.cruscotto.domain.enumeration.AnalysisType;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.ModuleStatus;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;

class ModuleDTOTest {

    @Test
    void testGettersAndSetters() {
        ModuleDTO dto = new ModuleDTO();
        ZonedDateTime now = ZonedDateTime.now();

        dto.setId(1L);
        dto.setCode("MOD001");
        dto.setName("Modulo Test");
        dto.setDescription("Descrizione del modulo");
        dto.setAnalysisType(AnalysisType.AUTOMATICA);
        dto.setAllowManualOutcome(true);
        dto.setStatus(ModuleStatus.ATTIVO);
        dto.setConfigExcludePlannedShutdown(true);
        dto.setConfigExcludeUnplannedShutdown(false);
        dto.setConfigEligibilityThreshold(true);
        dto.setConfigTolerance(false);
        dto.setConfigAverageTimeLimit(true);
        dto.setConfigEvaluationType(false);
        dto.setDeleted(true);
        dto.setDeletedDate(now);

        assertEquals(1L, dto.getId());
        assertEquals("MOD001", dto.getCode());
        assertEquals("Modulo Test", dto.getName());
        assertEquals("Descrizione del modulo", dto.getDescription());
        assertEquals(AnalysisType.AUTOMATICA, dto.getAnalysisType());
        assertTrue(dto.getAllowManualOutcome());
        assertEquals(ModuleStatus.ATTIVO, dto.getStatus());
        assertTrue(dto.getConfigExcludePlannedShutdown());
        assertFalse(dto.getConfigExcludeUnplannedShutdown());
        assertTrue(dto.getConfigEligibilityThreshold());
        assertFalse(dto.getConfigTolerance());
        assertTrue(dto.getConfigAverageTimeLimit());
        assertFalse(dto.getConfigEvaluationType());
        assertTrue(dto.isDeleted());
        assertEquals(now, dto.getDeletedDate());
    }

    @Test
    void testEqualsAndHashCode() {
        ModuleDTO dto1 = new ModuleDTO();
        dto1.setId(1L);
        dto1.setCode("MOD001");

        ModuleDTO dto2 = new ModuleDTO();
        dto2.setId(1L);
        dto2.setCode("MOD001");

        ModuleDTO dto3 = new ModuleDTO();
        dto3.setId(2L);
        dto3.setCode("MOD002");

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
        assertNotEquals(dto1, dto3);
        assertNotEquals(dto1.hashCode(), dto3.hashCode());
        assertNotEquals(null, dto1);
        assertNotEquals("string", dto1);
    }

    @Test
    void testToString() {
        ModuleDTO dto = new ModuleDTO();
        String toString = dto.toString();
        assertNotNull(toString);
    }
}
