package com.nexigroup.pagopa.cruscotto.service.dto;

import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class KpiB6DetailResultDTOTest {

    @Test
    void testDefaultConstructorAndSettersGetters() {
        KpiB6DetailResultDTO dto = new KpiB6DetailResultDTO();

        dto.setId(1L);
        dto.setInstanceId(2L);
        dto.setInstanceModuleId(3L);
        dto.setAnagStationId(4L);
        dto.setKpiB6ResultId(5L);
        dto.setAnalysisDate(LocalDate.of(2025, 10, 29));
        dto.setTotalStations(10);
        dto.setStationsWithPaymentOptions(5);
        dto.setDifference(5);
        dto.setPercentageDifference(50.0);
        dto.setOutcome("OK");

        assertEquals(1L, dto.getId());
        assertEquals(2L, dto.getInstanceId());
        assertEquals(3L, dto.getInstanceModuleId());
        assertEquals(4L, dto.getAnagStationId());
        assertEquals(5L, dto.getKpiB6ResultId());
        assertEquals(LocalDate.of(2025, 10, 29), dto.getAnalysisDate());
        assertEquals(10, dto.getTotalStations());
        assertEquals(5, dto.getStationsWithPaymentOptions());
        assertEquals(5, dto.getDifference());
        assertEquals(50.0, dto.getPercentageDifference());
        assertEquals("OK", dto.getOutcome());
    }

    @Test
    void testConstructorWithGenericDtoWithValidJson() {
        String json = "{ \"anagStationId\": 100, \"totalStations\": 20, \"stationsWithPaymentOptions\": 15, " +
            "\"difference\": 5, \"percentageDifference\": 25.0 }";

        KpiDetailResultDTO genericDto = new KpiDetailResultDTO();
        genericDto.setId(1L);
        genericDto.setInstanceId(2L);
        genericDto.setInstanceModuleId(3L);
        genericDto.setKpiResultId(4L);
        genericDto.setAnalysisDate(LocalDate.of(2025, 10, 29));
        genericDto.setOutcome(OutcomeStatus.OK);
        genericDto.setAdditionalData(json);

        KpiB6DetailResultDTO dto = new KpiB6DetailResultDTO(genericDto);

        assertEquals(1L, dto.getId());
        assertEquals(2L, dto.getInstanceId());
        assertEquals(3L, dto.getInstanceModuleId());
        assertEquals(4L, dto.getKpiB6ResultId());
        assertEquals(LocalDate.of(2025, 10, 29), dto.getAnalysisDate());
        assertEquals("OK", dto.getOutcome());
        assertEquals(100L, dto.getAnagStationId());
        assertEquals(20, dto.getTotalStations());
        assertEquals(15, dto.getStationsWithPaymentOptions());
        assertEquals(5, dto.getDifference());
        assertEquals(25.0, dto.getPercentageDifference());
    }

    @Test
    void testConstructorWithGenericDtoWithMissingJsonFields() {
        String json = "{ \"totalStations\": 20 }";

        KpiDetailResultDTO genericDto = new KpiDetailResultDTO();
        genericDto.setAdditionalData(json);

        KpiB6DetailResultDTO dto = new KpiB6DetailResultDTO(genericDto);

        assertEquals(20, dto.getTotalStations());
        assertNull(dto.getAnagStationId());
        assertNull(dto.getStationsWithPaymentOptions());
        assertNull(dto.getDifference());
        assertNull(dto.getPercentageDifference());
    }

    @Test
    void testConstructorWithGenericDtoWithNullOrMalformedJson() {
        // Null additionalData
        KpiDetailResultDTO dtoNull = new KpiDetailResultDTO();
        dtoNull.setAdditionalData(null);
        KpiB6DetailResultDTO resultNull = new KpiB6DetailResultDTO(dtoNull);
        assertNull(resultNull.getAnagStationId());

        // Malformed JSON
        KpiDetailResultDTO dtoMalformed = new KpiDetailResultDTO();
        dtoMalformed.setAdditionalData("{ bad json ");
        KpiB6DetailResultDTO resultMalformed = new KpiB6DetailResultDTO(dtoMalformed);
        assertNull(resultMalformed.getAnagStationId()); // should not throw
    }

    @Test
    void testEqualsAndHashCode() {
        KpiB6DetailResultDTO dto1 = new KpiB6DetailResultDTO();
        dto1.setId(1L);

        KpiB6DetailResultDTO dto2 = new KpiB6DetailResultDTO();
        dto2.setId(1L);

        KpiB6DetailResultDTO dto3 = new KpiB6DetailResultDTO();
        dto3.setId(2L);

        assertEquals(dto1, dto2);
        assertNotEquals(dto1, dto3);

        assertEquals(dto1.hashCode(), dto2.hashCode());
        assertNotEquals(dto1.hashCode(), dto3.hashCode());
    }

    @Test
    void testToString() {
        KpiB6DetailResultDTO dto = new KpiB6DetailResultDTO();
        dto.setId(1L);
        String str = dto.toString();
        assertTrue(str.contains("id=1"));
    }
}
