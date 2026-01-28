package com.nexigroup.pagopa.cruscotto.domain;

import org.junit.jupiter.api.Test;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.TypePlanned;

import static org.junit.jupiter.api.Assertions.*;

class QAnagPlannedShutdownTest {

    @Test
    void testQueryDSLPaths() {
        QAnagPlannedShutdown q = QAnagPlannedShutdown.anagPlannedShutdown;

        // Test basic path initialization
        assertNotNull(q);
        assertNotNull(q.id);
        assertNotNull(q.externalId);
        assertNotNull(q.shutdownStartDate);
        assertNotNull(q.shutdownEndDate);
        assertNotNull(q.standInd);
        assertNotNull(q.typePlanned);
        assertNotNull(q.year);

        // Test auditing fields
        assertNotNull(q.createdBy);
        assertNotNull(q.createdDate);
        assertNotNull(q.lastModifiedBy);
        assertNotNull(q.lastModifiedDate);

        // Test associations
        if (q.anagPartner != null) {
            assertNotNull(q.anagPartner.id); // assuming QAnagPartner has id
        }

        if (q.anagStation != null) {
            assertNotNull(q.anagStation.id); // assuming QAnagStation has id
        }

        // Test enum type path
        assertEquals(TypePlanned.class, q.typePlanned.getType());
    }

    @Test
    void testCreatingWithVariable() {
        QAnagPlannedShutdown q = new QAnagPlannedShutdown("customVariable");
        assertEquals("customVariable", q.getMetadata().getName());
    }

    @Test
    void testCreatingWithPath() {
        QAnagPlannedShutdown original = QAnagPlannedShutdown.anagPlannedShutdown;
        QAnagPlannedShutdown copy = new QAnagPlannedShutdown(original);
        assertEquals(original.getMetadata().getName(), copy.getMetadata().getName());
    }

    @Test
    void testCreatingWithMetadata() {
        QAnagPlannedShutdown q = new QAnagPlannedShutdown(QAnagPlannedShutdown.class.getSimpleName());
        assertNotNull(q);
    }
}
