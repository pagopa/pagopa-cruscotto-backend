package com.nexigroup.pagopa.cruscotto.service.dto;

import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

class JobsDTOTest {

    @Test
    void testGettersAndSetters() {
        JobsDTO dto = new JobsDTO();

        Date now = new Date();

        dto.setSchedulerName("Scheduler1");
        dto.setJobName("Job1");
        dto.setGroupName("Group1");
        dto.setScheduleTime(now);
        dto.setLastFiredTime(now);
        dto.setNextFireTime(now);
        dto.setJobStatus("RUNNING");
        dto.setCron("0 0 * * * ?");

        assertThat(dto.getSchedulerName()).isEqualTo("Scheduler1");
        assertThat(dto.getJobName()).isEqualTo("Job1");
        assertThat(dto.getGroupName()).isEqualTo("Group1");
        assertThat(dto.getScheduleTime()).isEqualTo(now);
        assertThat(dto.getLastFiredTime()).isEqualTo(now);
        assertThat(dto.getNextFireTime()).isEqualTo(now);
        assertThat(dto.getJobStatus()).isEqualTo("RUNNING");
        assertThat(dto.getCron()).isEqualTo("0 0 * * * ?");
    }

    @Test
    void testToStringContainsFields() {
        JobsDTO dto = new JobsDTO();
        dto.setSchedulerName("Scheduler1");
        dto.setJobName("Job1");

        String str = dto.toString();

        assertThat(str).contains("Scheduler1")
                        .contains("Job1");
    }

    @Test
    void testSerialization() throws IOException, ClassNotFoundException {
        JobsDTO dto = new JobsDTO();
        dto.setJobName("Job1");

        // Serialize
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(dto);
        }

        // Deserialize
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        JobsDTO deserialized;
        try (ObjectInputStream ois = new ObjectInputStream(bais)) {
            deserialized = (JobsDTO) ois.readObject();
        }

        assertThat(deserialized.getJobName()).isEqualTo("Job1");
    }
}
