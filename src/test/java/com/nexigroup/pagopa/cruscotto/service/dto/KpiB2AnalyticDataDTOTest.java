package com.nexigroup.pagopa.cruscotto.service.dto;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class KpiB2AnalyticDataDTOTest {

    private KpiB2AnalyticDataDTO dto;

    @BeforeEach
    void setUp() {
        dto = new KpiB2AnalyticDataDTO();
    }

    @Test
    void testSettersAndGetters() {
        Long id = 1L;
        Long instanceId = 10L;
        Long instanceModuleId = 20L;
        LocalDate analysisDate = LocalDate.of(2025, 10, 31);
        Long stationId = 100L;
        String method = "POST";
        LocalDate evaluationDate = LocalDate.of(2025, 10, 30);
        Long totReq = 500L;
        Long reqOk = 480L;
        Long reqTimeout = 20L;
        Double avgTime = 2.5;
        Long kpiB2DetailResultId = 99L;
        String stationName = "Test Station";

        dto.setId(id);
        dto.setInstanceId(instanceId);
        dto.setInstanceModuleId(instanceModuleId);
        dto.setAnalysisDate(analysisDate);
        dto.setStationId(stationId);
        dto.setMethod(method);
        dto.setEvaluationDate(evaluationDate);
        dto.setTotReq(totReq);
        dto.setReqOk(reqOk);
        dto.setReqTimeout(reqTimeout);
        dto.setAvgTime(avgTime);
        dto.setKpiB2DetailResultId(kpiB2DetailResultId);
        dto.setStationName(stationName);

        assertEquals(id, dto.getId());
        assertEquals(instanceId, dto.getInstanceId());
        assertEquals(instanceModuleId, dto.getInstanceModuleId());
        assertEquals(analysisDate, dto.getAnalysisDate());
        assertEquals(stationId, dto.getStationId());
        assertEquals(method, dto.getMethod());
        assertEquals(evaluationDate, dto.getEvaluationDate());
        assertEquals(totReq, dto.getTotReq());
        assertEquals(reqOk, dto.getReqOk());
        assertEquals(reqTimeout, dto.getReqTimeout());
        assertEquals(avgTime, dto.getAvgTime());
        assertEquals(kpiB2DetailResultId, dto.getKpiB2DetailResultId());
        assertEquals(stationName, dto.getStationName());
    }

    @Test
    void testToStringContainsAllFields() {
        dto.setId(1L);
        dto.setInstanceId(10L);
        dto.setInstanceModuleId(20L);
        dto.setAnalysisDate(LocalDate.of(2025, 10, 31));
        dto.setStationId(100L);
        dto.setMethod("GET");
        dto.setEvaluationDate(LocalDate.of(2025, 10, 30));
        dto.setTotReq(500L);
        dto.setReqOk(480L);
        dto.setReqTimeout(20L);
        dto.setAvgTime(2.5);
        dto.setKpiB2DetailResultId(99L);

        String result = dto.toString();

        assertTrue(result.contains("id=1"));
        assertTrue(result.contains("instanceId=10"));
        assertTrue(result.contains("instanceModuleId=20"));
        assertTrue(result.contains("analysisDate=2025-10-31"));
        assertTrue(result.contains("method='GET'"));
        assertTrue(result.contains("avgTime=2.5"));
        assertTrue(result.contains("kpiB2DetailResultId=99"));
    }

    @Test
    void testDefaultValues() {
        KpiB2AnalyticDataDTO emptyDto = new KpiB2AnalyticDataDTO();

        assertNull(emptyDto.getId());
        assertNull(emptyDto.getInstanceId());
        assertNull(emptyDto.getInstanceModuleId());
        assertNull(emptyDto.getAnalysisDate());
        assertNull(emptyDto.getStationId());
        assertNull(emptyDto.getMethod());
        assertNull(emptyDto.getEvaluationDate());
        assertNull(emptyDto.getTotReq());
        assertNull(emptyDto.getReqOk());
        assertNull(emptyDto.getReqTimeout());
        assertNull(emptyDto.getAvgTime());
        assertNull(emptyDto.getKpiB2DetailResultId());
        assertNull(emptyDto.getStationName());
    }
}
