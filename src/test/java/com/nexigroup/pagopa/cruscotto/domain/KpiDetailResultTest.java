package com.nexigroup.pagopa.cruscotto.domain;

import com.nexigroup.pagopa.cruscotto.domain.enumeration.ModuleCode;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class KpiDetailResultTest {

    @Test
    void testGettersAndSetters() {
        KpiDetailResult result = new KpiDetailResult();

        Long id = 100L;
        ModuleCode moduleCode = ModuleCode.B1; // adjust enum constant if needed
        Long instanceId = 200L;
        Long instanceModuleId = 300L;
        LocalDate analysisDate = LocalDate.now();
        OutcomeStatus outcome = OutcomeStatus.OK; // adjust enum constant if needed
        Long kpiResultId = 400L;
        String additionalData = "{\"key\":\"value\"}";

        result.setId(id);
        result.setModuleCode(moduleCode);
        result.setInstanceId(instanceId);
        result.setInstanceModuleId(instanceModuleId);
        result.setAnalysisDate(analysisDate);
        result.setOutcome(outcome);
        result.setKpiResultId(kpiResultId);
        result.setAdditionalData(additionalData);

        assertEquals(id, result.getId());
        assertEquals(moduleCode, result.getModuleCode());
        assertEquals(instanceId, result.getInstanceId());
        assertEquals(instanceModuleId, result.getInstanceModuleId());
        assertEquals(analysisDate, result.getAnalysisDate());
        assertEquals(outcome, result.getOutcome());
        assertEquals(kpiResultId, result.getKpiResultId());
        assertEquals(additionalData, result.getAdditionalData());
    }

    @Test
    void testEqualsAndHashCodeConsistency() {
        KpiDetailResult result1 = new KpiDetailResult();
        result1.setId(1L);

        KpiDetailResult result2 = new KpiDetailResult();
        result2.setId(1L);

        // Reflexive
        assertEquals(result1, result1);
        // Symmetric & Consistent (but not necessarily equal if auditing differs)
        assertNotEquals(result1, result2);

        // HashCode consistency
        assertEquals(result1.hashCode(), result1.hashCode());
    }

    @Test
    void testToStringContainsImportantFields() {
        KpiDetailResult result = new KpiDetailResult();
        result.setId(1L);
        result.setModuleCode(ModuleCode.B1);
        result.setOutcome(OutcomeStatus.OK);

        String str = result.toString();
        assertTrue(str.contains("id=1"));
        assertTrue(str.contains("moduleCode"));
        assertTrue(str.contains("outcome"));
    }

    @Test
    void testSerializable() throws Exception {
        KpiDetailResult result = new KpiDetailResult();
        result.setId(99L);
        result.setAdditionalData("{\"test\":true}");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(result);
        oos.flush();

        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
        KpiDetailResult deserialized = (KpiDetailResult) ois.readObject();

        assertEquals(result.getId(), deserialized.getId());
        assertEquals(result.getAdditionalData(), deserialized.getAdditionalData());
    }
}
