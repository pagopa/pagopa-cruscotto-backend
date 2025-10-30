package com.nexigroup.pagopa.cruscotto.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AnagStationAnagInstitutionIdTest {

    @Test
    void testGettersAndSetters() {
        AnagStationAnagInstitutionId obj = new AnagStationAnagInstitutionId();

        // Test setting and getting anagStation
        Long stationId = 123L;
        obj.setAnagStation(stationId);
        assertEquals(stationId, obj.getAnagStation(), "anagStation should be set and retrieved correctly");

        // Test setting and getting anagInstitution
        Long institutionId = 456L;
        obj.setAnagInstitution(institutionId);
        assertEquals(institutionId, obj.getAnagInstitution(), "anagInstitution should be set and retrieved correctly");
    }

    @Test
    void testSerialization() throws Exception {
        AnagStationAnagInstitutionId obj = new AnagStationAnagInstitutionId();
        obj.setAnagStation(1L);
        obj.setAnagInstitution(2L);

        // Serialize and deserialize to ensure it implements Serializable correctly
        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
        java.io.ObjectOutputStream oos = new java.io.ObjectOutputStream(baos);
        oos.writeObject(obj);

        java.io.ByteArrayInputStream bais = new java.io.ByteArrayInputStream(baos.toByteArray());
        java.io.ObjectInputStream ois = new java.io.ObjectInputStream(bais);
        AnagStationAnagInstitutionId deserialized = (AnagStationAnagInstitutionId) ois.readObject();

        assertEquals(obj.getAnagStation(), deserialized.getAnagStation());
        assertEquals(obj.getAnagInstitution(), deserialized.getAnagInstitution());
    }
}
