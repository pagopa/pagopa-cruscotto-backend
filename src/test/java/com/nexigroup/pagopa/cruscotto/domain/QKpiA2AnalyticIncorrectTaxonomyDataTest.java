package com.nexigroup.pagopa.cruscotto.domain;

import static org.junit.jupiter.api.Assertions.*;

import com.querydsl.core.types.PathMetadata;
import org.junit.jupiter.api.Test;


class QKpiA2AnalyticIncorrectTaxonomyDataTest {

    @Test
    void testStaticInstanceNotNull() {
        assertNotNull(QKpiA2AnalyticIncorrectTaxonomyData.kpiA2AnalyticIncorrectTaxonomyData);
    }

    @Test
    void testConstructorWithVariable() {
        QKpiA2AnalyticIncorrectTaxonomyData qData =
            new QKpiA2AnalyticIncorrectTaxonomyData("testVar");
        assertNotNull(qData);
        assertNotNull(qData.id);
        assertNotNull(qData.total);
        assertNotNull(qData.transferCategory);
        assertNotNull(qData.fromHour);
        assertNotNull(qData.endHour);
        assertNotNull(qData.kpiA2AnalyticDataId);
    }

    @Test
    void testConstructorWithPath() {
        QKpiA2AnalyticIncorrectTaxonomyData qData =
            new QKpiA2AnalyticIncorrectTaxonomyData(
                QKpiA2AnalyticIncorrectTaxonomyData.kpiA2AnalyticIncorrectTaxonomyData);
        assertNotNull(qData);
    }

    @Test
    void testConstructorWithMetadata() {
        PathMetadata metadata = QKpiA2AnalyticIncorrectTaxonomyData.kpiA2AnalyticIncorrectTaxonomyData.getMetadata();
        QKpiA2AnalyticIncorrectTaxonomyData qData = new QKpiA2AnalyticIncorrectTaxonomyData(metadata);
        assertNotNull(qData);
    }
}
