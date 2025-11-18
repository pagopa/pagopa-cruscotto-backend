package com.nexigroup.pagopa.cruscotto.domain;

import com.nexigroup.pagopa.cruscotto.domain.enumeration.ModuleCode;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class KpiResultTest {

    @Test
    void testGetterAndSetter() {
        KpiResult kpiResult = new KpiResult();

        kpiResult.setId(1L);
        kpiResult.setModuleCode(ModuleCode.B3);
        kpiResult.setInstanceId(100L);
        kpiResult.setInstanceModuleId(200L);
        kpiResult.setAnalysisDate(LocalDate.of(2025, 11, 17));
        kpiResult.setOutcome(OutcomeStatus.OK);
        kpiResult.setAdditionalData("{\"key\":\"value\"}");

        assertThat(kpiResult.getId()).isEqualTo(1L);
        assertThat(kpiResult.getModuleCode()).isEqualTo(ModuleCode.B3);
        assertThat(kpiResult.getInstanceId()).isEqualTo(100L);
        assertThat(kpiResult.getInstanceModuleId()).isEqualTo(200L);
        assertThat(kpiResult.getAnalysisDate()).isEqualTo(LocalDate.of(2025, 11, 17));
        assertThat(kpiResult.getOutcome()).isEqualTo(OutcomeStatus.OK);
        assertThat(kpiResult.getAdditionalData()).isEqualTo("{\"key\":\"value\"}");
    }

    @Test
    void testSerialization() throws IOException, ClassNotFoundException {
        KpiResult kpiResult = new KpiResult();
        kpiResult.setId(1L);
        kpiResult.setModuleCode(ModuleCode.B3);
        kpiResult.setOutcome(OutcomeStatus.OK);

        // Serialize
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(kpiResult);
        }

        // Deserialize
        KpiResult deserialized;
        try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()))) {
            deserialized = (KpiResult) ois.readObject();
        }

        assertThat(deserialized).isNotNull();
        assertThat(deserialized.getId()).isEqualTo(1L);
        assertThat(deserialized.getModuleCode()).isEqualTo(ModuleCode.B3);
        assertThat(deserialized.getOutcome()).isEqualTo(OutcomeStatus.OK);
    }

    @Test
    void testEqualsAndHashCode() {
        KpiResult kpi1 = new KpiResult();
        KpiResult kpi2 = new KpiResult();

        kpi1.setId(1L);
        kpi2.setId(1L);

        assertThat(kpi1).isEqualTo(kpi2);
        assertThat(kpi1.hashCode()).isEqualTo(kpi2.hashCode());

        kpi2.setId(2L);
        assertThat(kpi1).isNotEqualTo(kpi2);
    }

    @Test
    void testToStringContainsFields() {
        KpiResult kpiResult = new KpiResult();
        kpiResult.setId(123L);
        kpiResult.setModuleCode(ModuleCode.B3);

        String str = kpiResult.toString();
        assertThat(str).contains("123");
        assertThat(str).contains("B3");
    }
}
