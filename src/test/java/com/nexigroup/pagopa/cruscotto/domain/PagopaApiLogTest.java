package com.nexigroup.pagopa.cruscotto.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.*;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class PagopaApiLogTest {

    @Test
    void testGettersAndSetters() {
        PagopaApiLog log = new PagopaApiLog();

        log.setCfPartner("Partner123");
        log.setDate(LocalDate.of(2025, 10, 30));
        log.setStation("Station01");
        log.setCfEnte("CFENTE001");
        log.setApi("API001");
        log.setTotReq(100);
        log.setReqOk(90);
        log.setReqKo(10);

        assertThat(log.getCfPartner()).isEqualTo("Partner123");
        assertThat(log.getDate()).isEqualTo(LocalDate.of(2025, 10, 30));
        assertThat(log.getStation()).isEqualTo("Station01");
        assertThat(log.getCfEnte()).isEqualTo("CFENTE001");
        assertThat(log.getApi()).isEqualTo("API001");
        assertThat(log.getTotReq()).isEqualTo(100);
        assertThat(log.getReqOk()).isEqualTo(90);
        assertThat(log.getReqKo()).isEqualTo(10);
    }

    @Test
    void testAllArgsConstructorAndEqualsHashCode() {
        LocalDate date = LocalDate.now();
        PagopaApiLog log1 = new PagopaApiLog("CFP1", date, "ST001", "CFE1", "API1", 10, 8, 2);
        PagopaApiLog log2 = new PagopaApiLog("CFP1", date, "ST001", "CFE1", "API1", 10, 8, 2);
        PagopaApiLog log3 = new PagopaApiLog("CFP2", date, "ST002", "CFE2", "API2", 20, 18, 2);

        assertThat(log1).isEqualTo(log2);
        assertThat(log1).hasSameHashCodeAs(log2);
        assertThat(log1).isNotEqualTo(log3);

        assertThat(log1.toString()).contains("CFP1");
    }

    @Test
    void testPagopaApiLogIdEqualsHashCode() {
        LocalDate date = LocalDate.now();
        PagopaApiLog.PagopaApiLogId id1 = new PagopaApiLog.PagopaApiLogId("CFP1", date, "ST001", "CFE1", "API1");
        PagopaApiLog.PagopaApiLogId id2 = new PagopaApiLog.PagopaApiLogId("CFP1", date, "ST001", "CFE1", "API1");
        PagopaApiLog.PagopaApiLogId id3 = new PagopaApiLog.PagopaApiLogId("CFP2", date, "ST002", "CFE2", "API2");

        assertThat(id1).isEqualTo(id2);
        assertThat(id1.hashCode()).isEqualTo(id2.hashCode());
        assertThat(id1).isNotEqualTo(id3);
    }

    @Test
    void testSerialization() throws IOException, ClassNotFoundException {
        PagopaApiLog log = new PagopaApiLog("CFP1", LocalDate.now(), "ST001", "CFE1", "API1", 10, 8, 2);

        // Serialize
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(log);
        oos.close();

        // Deserialize
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bais);
        PagopaApiLog deserialized = (PagopaApiLog) ois.readObject();

        assertThat(deserialized).isEqualTo(log);
    }
}
