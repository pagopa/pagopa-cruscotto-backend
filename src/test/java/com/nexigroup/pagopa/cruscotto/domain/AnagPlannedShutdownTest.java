package com.nexigroup.pagopa.cruscotto.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.nexigroup.pagopa.cruscotto.domain.enumeration.TypePlanned;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;

class AnagPlannedShutdownTest {

    private AnagPlannedShutdown shutdown;
    private AnagPartner partner;
    private AnagStation station;

    @BeforeEach
    void setUp() {
        shutdown = new AnagPlannedShutdown();
        partner = new AnagPartner();
        partner.setId(1L);
        station = new AnagStation();
        station.setId(1L);
    }

    @Test
    void testGettersAndSetters() {
        shutdown.setId(100L);
        shutdown.setTypePlanned(TypePlanned.PROGRAMMATO);
        shutdown.setStandInd(true);
        Instant now = Instant.now();
        shutdown.setShutdownStartDate(now);
        shutdown.setShutdownEndDate(now.plusSeconds(3600));
        shutdown.setYear(2025L);
        shutdown.setExternalId(500L);
        shutdown.setAnagPartner(partner);
        shutdown.setAnagStation(station);

        assertThat(shutdown.getId()).isEqualTo(100L);
        assertThat(shutdown.getTypePlanned()).isEqualTo(TypePlanned.PROGRAMMATO);
        assertThat(shutdown.isStandInd()).isTrue();
        assertThat(shutdown.getShutdownStartDate()).isEqualTo(now);
        assertThat(shutdown.getShutdownEndDate()).isEqualTo(now.plusSeconds(3600));
        assertThat(shutdown.getYear()).isEqualTo(2025L);
        assertThat(shutdown.getExternalId()).isEqualTo(500L);
        assertThat(shutdown.getAnagPartner()).isEqualTo(partner);
        assertThat(shutdown.getAnagStation()).isEqualTo(station);
    }

    @Test
    void testEqualsAndHashCode() {
        AnagPlannedShutdown shutdown2 = new AnagPlannedShutdown();

        // Both IDs null -> not equal
        assertThat(shutdown).isNotEqualTo(shutdown2);

        shutdown.setId(1L);
        shutdown2.setId(1L);

        assertThat(shutdown).isEqualTo(shutdown2);
        assertThat(shutdown.hashCode()).isEqualTo(shutdown2.hashCode());

        shutdown2.setId(2L);
        assertThat(shutdown).isNotEqualTo(shutdown2);
    }

    @Test
    void testToStringContainsFields() {
        shutdown.setId(100L);
        shutdown.setTypePlanned(TypePlanned.PROGRAMMATO); // matches enum value
        shutdown.setStandInd(true);
        shutdown.setShutdownStartDate(Instant.EPOCH);
        shutdown.setShutdownEndDate(Instant.EPOCH);
        shutdown.setYear(2025L);
        shutdown.setExternalId(500L);
        shutdown.setAnagPartner(partner);
        shutdown.setAnagStation(station);

        String str = shutdown.toString();

        assertThat(str).contains("id=100");
        assertThat(str).contains("typePlanned=PROGRAMMATO"); // fixed
        assertThat(str).contains("standInd=true");
        assertThat(str).contains("shutdownStartDate=1970-01-01T00:00:00Z");
        assertThat(str).contains("shutdownEndDate=1970-01-01T00:00:00Z");
        assertThat(str).contains("year=2025");
        assertThat(str).contains("externalId=500");
        assertThat(str).contains("anagPartner=" + partner);
        assertThat(str).contains("anagStation=" + station);
    }
}
