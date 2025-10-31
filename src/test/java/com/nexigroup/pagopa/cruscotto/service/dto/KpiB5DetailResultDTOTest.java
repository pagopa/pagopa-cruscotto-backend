package com.nexigroup.pagopa.cruscotto.service.dto;

import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class KpiB5DetailResultDTOTest {

    @Test
    void testGettersAndSetters() {
        KpiB5DetailResultDTO dto = new KpiB5DetailResultDTO();

        Long id = 1L;
        Long instanceId = 2L;
        Long instanceModuleId = 3L;
        Long kpiB5ResultId = 4L;
        LocalDate date = LocalDate.of(2025, 10, 30);
        Integer stationsPresent = 10;
        Integer stationsWithoutSpontaneous = 2;
        BigDecimal percentageNoSpontaneous = new BigDecimal("20.0");
        OutcomeStatus outcome = OutcomeStatus.OK;

        dto.setId(id);
        dto.setInstanceId(instanceId);
        dto.setInstanceModuleId(instanceModuleId);
        dto.setKpiB5ResultId(kpiB5ResultId);
        dto.setAnalysisDate(date);
        dto.setStationsPresent(stationsPresent);
        dto.setStationsWithoutSpontaneous(stationsWithoutSpontaneous);
        dto.setPercentageNoSpontaneous(percentageNoSpontaneous);
        dto.setOutcome(outcome);

        assertEquals(id, dto.getId());
        assertEquals(instanceId, dto.getInstanceId());
        assertEquals(instanceModuleId, dto.getInstanceModuleId());
        assertEquals(kpiB5ResultId, dto.getKpiB5ResultId());
        assertEquals(date, dto.getAnalysisDate());
        assertEquals(stationsPresent, dto.getStationsPresent());
        assertEquals(stationsWithoutSpontaneous, dto.getStationsWithoutSpontaneous());
        assertEquals(percentageNoSpontaneous, dto.getPercentageNoSpontaneous());
        assertEquals(outcome, dto.getOutcome());
    }

    @Test
    void testEqualsAndHashCode() {
        KpiB5DetailResultDTO dto1 = new KpiB5DetailResultDTO();
        dto1.setId(1L);

        KpiB5DetailResultDTO dto2 = new KpiB5DetailResultDTO();
        dto2.setId(1L);

        KpiB5DetailResultDTO dto3 = new KpiB5DetailResultDTO();
        dto3.setId(2L);

        KpiB5DetailResultDTO dto4 = new KpiB5DetailResultDTO();

        // Same object
        assertEquals(dto1, dto1);

        // Equal by id
        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());

        // Different id
        assertNotEquals(dto1, dto3);

        // Null id case
        assertNotEquals(dto1, dto4);

        // Different class
        assertNotEquals(dto1, "some string");

        // Null comparison
        assertNotEquals(dto1, null);
    }

    @Test
    void testToStringContainsFields() {
        KpiB5DetailResultDTO dto = new KpiB5DetailResultDTO();
        dto.setId(1L);
        dto.setInstanceId(2L);
        dto.setInstanceModuleId(3L);
        dto.setKpiB5ResultId(4L);
        dto.setAnalysisDate(LocalDate.of(2025, 10, 30));
        dto.setStationsPresent(10);
        dto.setStationsWithoutSpontaneous(5);
        dto.setPercentageNoSpontaneous(new BigDecimal("50.0"));
        dto.setOutcome(OutcomeStatus.OK);

        String toString = dto.toString();
        assertTrue(toString.contains("id=1"));
        assertTrue(toString.contains("instanceId=2"));
        assertTrue(toString.contains("stationsPresent=10"));
        assertTrue(toString.contains("outcome=OK"));
    }
}
