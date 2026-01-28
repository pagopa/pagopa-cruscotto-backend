package com.nexigroup.pagopa.cruscotto.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;


class QKpiB2AnalyticDrillDownTest  {

    @Test
    void testConstructors() {
        // constructor with variable name
        QKpiB2AnalyticDrillDown q1 = new QKpiB2AnalyticDrillDown("variable");
        assertNotNull(q1);

        // constructor with Path
        new QKpiB2AnalyticDrillDown(QKpiB2AnalyticDrillDown.kpiB2AnalyticDrillDown);

        // constructor with metadata
        new QKpiB2AnalyticDrillDown(QKpiB2AnalyticDrillDown.kpiB2AnalyticDrillDown.getMetadata());
    }
}
