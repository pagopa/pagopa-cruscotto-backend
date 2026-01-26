package com.nexigroup.pagopa.cruscotto.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class QKpiA1AnalyticDrillDownTest {

    @Test
    void testDefaultInstance() {
        // Should create an instance without throwing exceptions
        QKpiA1AnalyticDrillDown instance = new QKpiA1AnalyticDrillDown("testVariable");
        assertNotNull(instance);

        // Check that paths are not null
        assertNotNull(instance.id);
        assertNotNull(instance.kpiA1AnalyticDataId);
        assertNotNull(instance.okRequests);
        assertNotNull(instance.reqTimeout);
        assertNotNull(instance.totalRequests);
        assertNotNull(instance.fromHour);
        assertNotNull(instance.toHour);
    }

    @Test
    void testPathValues() {
        QKpiA1AnalyticDrillDown instance = QKpiA1AnalyticDrillDown.kpiA1AnalyticDrillDown;

        // Check that the variable names match
        assertEquals("id", instance.id.getMetadata().getName());
        assertEquals("kpiA1AnalyticDataId", instance.kpiA1AnalyticDataId.getMetadata().getName());
        assertEquals("okRequests", instance.okRequests.getMetadata().getName());
        assertEquals("reqTimeout", instance.reqTimeout.getMetadata().getName());
        assertEquals("totalRequests", instance.totalRequests.getMetadata().getName());
        assertEquals("fromHour", instance.fromHour.getMetadata().getName());
        assertEquals("toHour", instance.toHour.getMetadata().getName());
    }

    @Test
    void testConstructors() {
        // Path-based constructor
        QKpiA1AnalyticDrillDown instanceFromPath = new QKpiA1AnalyticDrillDown(QKpiA1AnalyticDrillDown.kpiA1AnalyticDrillDown);
        assertNotNull(instanceFromPath);

        // Metadata-based constructor
        QKpiA1AnalyticDrillDown instanceFromMetadata = new QKpiA1AnalyticDrillDown(QKpiA1AnalyticDrillDown.kpiA1AnalyticDrillDown.getMetadata());
        assertNotNull(instanceFromMetadata);
    }
}
