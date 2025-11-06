package com.nexigroup.pagopa.cruscotto.domain;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class PagoPaTaxonomyAggregatePositionTest {

    @Test
    void testGettersAndSetters() {
        PagoPaTaxonomyAggregatePosition position = new PagoPaTaxonomyAggregatePosition();

        Instant now = Instant.now();
        position.setId(1L);
        position.setCfPartner("ABC123");
        position.setStation("ST01");
        position.setTransferCategory("CATEGORY1");
        position.setStartDate(now);
        position.setEndDate(now.plusSeconds(3600));
        position.setTotal(100L);

        assertThat(position.getId()).isEqualTo(1L);
        assertThat(position.getCfPartner()).isEqualTo("ABC123");
        assertThat(position.getStation()).isEqualTo("ST01");
        assertThat(position.getTransferCategory()).isEqualTo("CATEGORY1");
        assertThat(position.getStartDate()).isEqualTo(now);
        assertThat(position.getEndDate()).isEqualTo(now.plusSeconds(3600));
        assertThat(position.getTotal()).isEqualTo(100L);
    }

    @Test
    void testEqualsAndHashCode() {
        Instant now = Instant.now();

        PagoPaTaxonomyAggregatePosition p1 = new PagoPaTaxonomyAggregatePosition();
        p1.setCfPartner("ABC123");
        p1.setStation("ST01");
        p1.setTransferCategory("CATEGORY1");
        p1.setStartDate(now);
        p1.setEndDate(now.plusSeconds(3600));

        PagoPaTaxonomyAggregatePosition p2 = new PagoPaTaxonomyAggregatePosition();
        p2.setCfPartner("ABC123");
        p2.setStation("ST01");
        p2.setTransferCategory("CATEGORY1");
        p2.setStartDate(now);
        p2.setEndDate(now.plusSeconds(3600));

        PagoPaTaxonomyAggregatePosition p3 = new PagoPaTaxonomyAggregatePosition();
        p3.setCfPartner("DIFFERENT");

        // Equality based on business fields, not ID
        assertThat(p1).isEqualTo(p2)
                      .hasSameHashCodeAs(p2)
                      .isNotEqualTo(p3);
    }

    @Test
    void testToString() {
        Instant now = Instant.now();

        PagoPaTaxonomyAggregatePosition position = new PagoPaTaxonomyAggregatePosition();
        position.setId(1L);
        position.setCfPartner("ABC123");
        position.setStation("ST01");
        position.setTransferCategory("CATEGORY1");
        position.setStartDate(now);
        position.setEndDate(now.plusSeconds(3600));
        position.setTotal(100L);

        String str = position.toString();

        assertThat(str).contains("id=1")
                       .contains("cfPartner=ABC123")
                       .contains("station=ST01")
                       .contains("transferCategory=CATEGORY1")
                       .contains("total=100");
    }
}
