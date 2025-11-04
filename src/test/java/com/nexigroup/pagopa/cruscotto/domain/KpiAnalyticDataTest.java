package com.nexigroup.pagopa.cruscotto.domain;

import com.nexigroup.pagopa.cruscotto.domain.enumeration.ModuleCode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class KpiAnalyticDataTest {

    @Test
    void testNotEquals() {
        KpiAnalyticData data1 = new KpiAnalyticData();
        data1.setId(1L);

        KpiAnalyticData data2 = new KpiAnalyticData();
        data2.setId(2L);

        assertNotEquals(data1, data2);
    }

    @Test
    void testToString() {
        KpiAnalyticData data = new KpiAnalyticData();
        data.setId(5L);
        data.setModuleCode(ModuleCode.B3);

        String str = data.toString();
        assertTrue(str.contains("id=5"));
        assertTrue(str.contains("moduleCode=B3"));
    }
}
