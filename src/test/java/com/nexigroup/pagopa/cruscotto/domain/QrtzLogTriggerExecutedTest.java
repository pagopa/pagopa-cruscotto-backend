package com.nexigroup.pagopa.cruscotto.domain;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.lang.reflect.Field;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class QrtzLogTriggerExecutedTest {

    @Test
    void gettersSettersAndToString() {
        QrtzLogTriggerExecuted e = new QrtzLogTriggerExecuted();
        e.setId(123L);
        e.setFireInstanceId("fire-1");
        e.setSchedulerName("sched-A");
        e.setJobName("job-X");
        e.setJobGroup("group-1");
        Instant now = Instant.now();
        e.setScheduledTime(now);
        e.setTriggerGroup("tgroup");
        e.setTriggerName("tname");
        e.setInitFiredTime(now.minusSeconds(30));
        e.setEndFiredTime(now.plusSeconds(30));
        e.setState("COMPLETED");
        byte[] ex = "err".getBytes();
        e.setMessageException(ex);

        assertAll(
            () -> assertEquals(123L, e.getId()),
            () -> assertEquals("fire-1", e.getFireInstanceId()),
            () -> assertEquals("sched-A", e.getSchedulerName()),
            () -> assertEquals("job-X", e.getJobName()),
            () -> assertEquals("group-1", e.getJobGroup()),
            () -> assertEquals(now, e.getScheduledTime()),
            () -> assertEquals("tgroup", e.getTriggerGroup()),
            () -> assertEquals("tname", e.getTriggerName()),
            () -> assertEquals(now.minusSeconds(30), e.getInitFiredTime()),
            () -> assertEquals(now.plusSeconds(30), e.getEndFiredTime()),
            () -> assertEquals("COMPLETED", e.getState()),
            () -> assertArrayEquals(ex, e.getMessageException())
        );

        String s = e.toString();
        assertTrue(s.contains("fireInstanceId"));
        assertTrue(s.contains("fire-1"));
        assertTrue(s.contains("COMPLETED"));
    }

    @Test
    void serializationRoundTripPreservesState() throws Exception {
        QrtzLogTriggerExecuted e = new QrtzLogTriggerExecuted();
        e.setId(9L);
        e.setFireInstanceId("f-9");
        e.setSchedulerName("s-9");
        e.setJobName("j-9");
        e.setJobGroup("g-9");
        Instant t = Instant.parse("2020-01-01T00:00:00Z");
        e.setScheduledTime(t);
        e.setTriggerGroup("tg");
        e.setTriggerName("tn");
        e.setState("OK");
        e.setMessageException(new byte[] {1, 2, 3});

        byte[] serialized;
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(e);
            oos.flush();
            serialized = baos.toByteArray();
        }

        assertTrue(serialized.length > 0);

        QrtzLogTriggerExecuted read;
        try (ByteArrayInputStream bais = new ByteArrayInputStream(serialized);
             ObjectInputStream ois = new ObjectInputStream(bais)) {
            Object obj = ois.readObject();
            assertNotNull(obj);
            assertTrue(obj instanceof QrtzLogTriggerExecuted);
            read = (QrtzLogTriggerExecuted) obj;
        }

        assertAll(
            () -> assertEquals(e.getId(), read.getId()),
            () -> assertEquals(e.getFireInstanceId(), read.getFireInstanceId()),
            () -> assertEquals(e.getSchedulerName(), read.getSchedulerName()),
            () -> assertEquals(e.getJobName(), read.getJobName()),
            () -> assertEquals(e.getJobGroup(), read.getJobGroup()),
            () -> assertEquals(e.getScheduledTime(), read.getScheduledTime()),
            () -> assertEquals(e.getTriggerGroup(), read.getTriggerGroup()),
            () -> assertEquals(e.getTriggerName(), read.getTriggerName()),
            () -> assertEquals(e.getState(), read.getState()),
            () -> assertArrayEquals(e.getMessageException(), read.getMessageException())
        );
    }

    @Test
    void expectedValidationAnnotationsArePresent() throws NoSuchFieldException {
        Field fireInstanceId = QrtzLogTriggerExecuted.class.getDeclaredField("fireInstanceId");
        assertTrue(fireInstanceId.isAnnotationPresent(NotNull.class));
        assertTrue(fireInstanceId.isAnnotationPresent(Size.class));

        Field scheduledTime = QrtzLogTriggerExecuted.class.getDeclaredField("scheduledTime");
        assertTrue(scheduledTime.isAnnotationPresent(NotNull.class));

        Field state = QrtzLogTriggerExecuted.class.getDeclaredField("state");
        assertTrue(state.isAnnotationPresent(NotNull.class));
        assertTrue(state.isAnnotationPresent(Size.class));

        Field messageException = QrtzLogTriggerExecuted.class.getDeclaredField("messageException");
        assertFalse(messageException.isAnnotationPresent(NotNull.class));
    }
}
