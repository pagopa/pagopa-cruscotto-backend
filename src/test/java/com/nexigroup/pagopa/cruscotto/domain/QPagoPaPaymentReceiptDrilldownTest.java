package com.nexigroup.pagopa.cruscotto.domain;

import com.querydsl.core.types.PathMetadata;
import org.junit.jupiter.api.Test;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;
import static org.assertj.core.api.Assertions.assertThat;

public class QPagoPaPaymentReceiptDrilldownTest {

    @Test
    void testStringConstructor() {
        QPagoPaPaymentReceiptDrilldown q = new QPagoPaPaymentReceiptDrilldown("alias");

        assertThat(q).isNotNull();
        assertThat(q.id).isNotNull();
        assertThat(q.analysisDate).isNotNull();
        assertThat(q.evaluationDate).isNotNull();
        assertThat(q.resKo).isNotNull();
        assertThat(q.resOk).isNotNull();
        assertThat(q.totRes).isNotNull();
        assertThat(q.startTime).isNotNull();
        assertThat(q.endTime).isNotNull();
    }

    @Test
    void testPathConstructor() {
        QPagoPaPaymentReceiptDrilldown original = new QPagoPaPaymentReceiptDrilldown("original");
        QPagoPaPaymentReceiptDrilldown copy = new QPagoPaPaymentReceiptDrilldown(original);

        assertThat(copy).isNotNull();
        assertThat(copy.id).isNotNull();
        assertThat(copy.analysisDate).isNotNull();
    }

    @Test
    void testMetadataConstructor() {
        PathMetadata metadata = forVariable("meta");
        QPagoPaPaymentReceiptDrilldown q = new QPagoPaPaymentReceiptDrilldown(metadata);

        assertThat(q).isNotNull();
        assertThat(q.id).isNotNull();
        assertThat(q.analysisDate).isNotNull();
    }

    @Test
    void testAllNestedFieldsInitialized() {
        QPagoPaPaymentReceiptDrilldown q = new QPagoPaPaymentReceiptDrilldown("test");

        // Nested Q objects
        assertThat(q.instance).isNotNull();
        assertThat(q.instanceModule).isNotNull();
        assertThat(q.station).isNotNull();
    }
}
