package com.nexigroup.pagopa.cruscotto.service.dto;

import org.junit.jupiter.api.Test;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class KpiB2AnalyticDrillDownDTOTest {

    @Test
    void testGettersAndSetters() {
        KpiB2AnalyticDrillDownDTO dto = new KpiB2AnalyticDrillDownDTO();

        Long id = 1L;
        Long dataId = 2L;
        Long totalRequests = 100L;
        Long okRequests = 90L;
        Double averageTime = 123.45;
        Instant fromHour = Instant.parse("2025-10-30T10:00:00Z");
        Instant endHour = Instant.parse("2025-10-30T11:00:00Z");

        dto.setId(id);
        dto.setKpiB2AnalyticDataId(dataId);
        dto.setTotalRequests(totalRequests);
        dto.setOkRequests(okRequests);
        dto.setAverageTimeMs(averageTime);
        dto.setFromHour(fromHour);
        dto.setEndHour(endHour);

        assertThat(dto.getId()).isEqualTo(id);
        assertThat(dto.getKpiB2AnalyticDataId()).isEqualTo(dataId);
        assertThat(dto.getTotalRequests()).isEqualTo(totalRequests);
        assertThat(dto.getOkRequests()).isEqualTo(okRequests);
        assertThat(dto.getAverageTimeMs()).isEqualTo(averageTime);
        assertThat(dto.getFromHour()).isEqualTo(fromHour);
        assertThat(dto.getEndHour()).isEqualTo(endHour);

        // Test toString contains some field values
        String dtoString = dto.toString();
        assertThat(dtoString).contains(id.toString(), dataId.toString(), totalRequests.toString());

        // Test equals and hashCode
        KpiB2AnalyticDrillDownDTO dto2 = new KpiB2AnalyticDrillDownDTO();
        dto2.setId(id);
        dto2.setKpiB2AnalyticDataId(dataId);
        dto2.setTotalRequests(totalRequests);
        dto2.setOkRequests(okRequests);
        dto2.setAverageTimeMs(averageTime);
        dto2.setFromHour(fromHour);
        dto2.setEndHour(endHour);

        assertThat(dto).isEqualTo(dto2);
        assertThat(dto.hashCode()).isEqualTo(dto2.hashCode());
    }
}
