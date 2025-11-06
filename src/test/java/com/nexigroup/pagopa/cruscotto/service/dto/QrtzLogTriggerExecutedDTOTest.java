package com.nexigroup.pagopa.cruscotto.service.dto;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class QrtzLogTriggerExecutedDTOTest {

    @Test
    void testGettersAndSetters() {
        QrtzLogTriggerExecutedDTO dto = new QrtzLogTriggerExecutedDTO();

        Long id = 1L;
        String fireInstanceId = "fire-123";
        String schedulerName = "scheduler";
        String jobName = "job";
        String jobGroup = "group";
        Instant scheduledTime = Instant.now();
        String triggerGroup = "triggerGroup";
        String triggerName = "triggerName";
        Instant initFiredTime = Instant.now();
        Instant endFiredTime = Instant.now();
        String state = "SUCCESS";
        String messageException = "None";

        dto.setId(id);
        dto.setFireInstanceId(fireInstanceId);
        dto.setSchedulerName(schedulerName);
        dto.setJobName(jobName);
        dto.setJobGroup(jobGroup);
        dto.setScheduledTime(scheduledTime);
        dto.setTriggerGroup(triggerGroup);
        dto.setTriggerName(triggerName);
        dto.setInitFiredTime(initFiredTime);
        dto.setEndFiredTime(endFiredTime);
        dto.setState(state);
        dto.setMessageException(messageException);

        assertEquals(id, dto.getId());
        assertEquals(fireInstanceId, dto.getFireInstanceId());
        assertEquals(schedulerName, dto.getSchedulerName());
        assertEquals(jobName, dto.getJobName());
        assertEquals(jobGroup, dto.getJobGroup());
        assertEquals(scheduledTime, dto.getScheduledTime());
        assertEquals(triggerGroup, dto.getTriggerGroup());
        assertEquals(triggerName, dto.getTriggerName());
        assertEquals(initFiredTime, dto.getInitFiredTime());
        assertEquals(endFiredTime, dto.getEndFiredTime());
        assertEquals(state, dto.getState());
        assertEquals(messageException, dto.getMessageException());
    }

    @Test
    void testEqualsAndHashCode() {
        QrtzLogTriggerExecutedDTO dto1 = new QrtzLogTriggerExecutedDTO();
        QrtzLogTriggerExecutedDTO dto2 = new QrtzLogTriggerExecutedDTO();

        dto1.setId(1L);
        dto2.setId(1L);

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());

        dto2.setId(2L);
        assertNotEquals(dto1, dto2);
    }

    @Test
    void testToString() {
        QrtzLogTriggerExecutedDTO dto = new QrtzLogTriggerExecutedDTO();
        dto.setId(1L);
        dto.setFireInstanceId("fire-123");

        String str = dto.toString();
        assertTrue(str.contains("id=1"));
        assertTrue(str.contains("fireInstanceId=fire-123"));
    }
}
