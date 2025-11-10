package com.nexigroup.pagopa.cruscotto.domain;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class PagopaSpontaneousTest {

    @Test
    void testNoArgsConstructor() {
        PagopaSpontaneous entity = new PagopaSpontaneous();
        assertThat(entity).isNotNull();
    }

    @Test
    void testAllArgsConstructorAndGetters() {
        Long id = 1L;
        String cfPartner = "CF123";
        String station = "StationA";
        Boolean spontaneousPayment = true;

        PagopaSpontaneous entity = new PagopaSpontaneous(id, cfPartner, station, spontaneousPayment);

        assertThat(entity.getId()).isEqualTo(id);
        assertThat(entity.getCfPartner()).isEqualTo(cfPartner);
        assertThat(entity.getStation()).isEqualTo(station);
        assertThat(entity.getSpontaneousPayment()).isEqualTo(spontaneousPayment);
    }

    @Test
    void testSetters() {
        PagopaSpontaneous entity = new PagopaSpontaneous();

        entity.setId(1L);
        entity.setCfPartner("CF123");
        entity.setStation("StationA");
        entity.setSpontaneousPayment(true);

        assertThat(entity.getId()).isEqualTo(1L);
        assertThat(entity.getCfPartner()).isEqualTo("CF123");
        assertThat(entity.getStation()).isEqualTo("StationA");
        assertThat(entity.getSpontaneousPayment()).isTrue();
    }

    @Test
    void testEqualsAndHashCode() {
        PagopaSpontaneous entity1 = new PagopaSpontaneous(1L, "CF123", "StationA", true);
        PagopaSpontaneous entity2 = new PagopaSpontaneous(1L, "CF123", "StationA", true);
        PagopaSpontaneous entity3 = new PagopaSpontaneous(2L, "CF456", "StationB", false);

        // Equals
        assertThat(entity1).isEqualTo(entity2);
        assertThat(entity1).isNotEqualTo(entity3);

        // HashCode
        assertThat(entity1.hashCode()).isEqualTo(entity2.hashCode());
        assertThat(entity1.hashCode()).isNotEqualTo(entity3.hashCode());
    }
}
