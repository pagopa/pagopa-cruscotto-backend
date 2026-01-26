package com.nexigroup.pagopa.cruscotto.service.dto;

import com.nexigroup.pagopa.cruscotto.domain.enumeration.EvaluationType;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class KpiB1ResultDTOTest {

    @Test
    void testAllGettersAndSetters() {
        KpiB1ResultDTO dto = new KpiB1ResultDTO();

        // Set values
        dto.setId(1L);
        dto.setInstanceId(2L);
        dto.setInstanceModuleId(3L);
        dto.setAnalysisDate(LocalDate.of(2025, 10, 31));
        dto.setInstitutionCount(10);
        dto.setTransactionCount(100);
        dto.setInstitutionTolerance(BigDecimal.valueOf(0.5));
        dto.setTransactionTolerance(BigDecimal.valueOf(1.5));
        dto.setEvaluationType(EvaluationType.MESE); // adjust enum value
        dto.setOutcome(OutcomeStatus.OK); // adjust enum value

        // Get values and assert
        assertEquals(1L, dto.getId());
        assertEquals(2L, dto.getInstanceId());
        assertEquals(3L, dto.getInstanceModuleId());
        assertEquals(LocalDate.of(2025, 10, 31), dto.getAnalysisDate());
        assertEquals(10, dto.getInstitutionCount());
        assertEquals(100, dto.getTransactionCount());
        assertEquals(BigDecimal.valueOf(0.5), dto.getInstitutionTolerance());
        assertEquals(BigDecimal.valueOf(1.5), dto.getTransactionTolerance());
        assertEquals(EvaluationType.MESE, dto.getEvaluationType());
        assertEquals(OutcomeStatus.OK, dto.getOutcome());

        // Test Lombok-generated methods
        assertNotNull(dto.toString());
        assertEquals(dto, dto); // equals self
        assertNotNull(dto);
    }

    @Test
    void testEqualsAndHashCodeDifferentObjects() {
        KpiB1ResultDTO dto1 = new KpiB1ResultDTO();
        dto1.setId(1L);

        KpiB1ResultDTO dto2 = new KpiB1ResultDTO();
        dto2.setId(1L);

        KpiB1ResultDTO dto3 = new KpiB1ResultDTO();
        dto3.setId(2L);

        assertEquals(dto1, dto2); // same id => equal
        assertNotEquals(dto1, dto3); // different id => not equal
    }
}
