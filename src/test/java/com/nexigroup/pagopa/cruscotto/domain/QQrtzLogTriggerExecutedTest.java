package com.nexigroup.pagopa.cruscotto.domain;

import com.querydsl.core.types.PathMetadata;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class QQrtzLogTriggerExecutedTest {

    @Test
    void testStaticInstanceNotNull() {
        assertThat(QQrtzLogTriggerExecuted.qrtzLogTriggerExecuted).isNotNull();
    }

    @Test
    void testFieldInitialization() {
        QQrtzLogTriggerExecuted q = new QQrtzLogTriggerExecuted("alias");

        assertThat(q.endFiredTime).isNotNull();
        assertThat(q.fireInstanceId).isNotNull();
        assertThat(q.id).isNotNull();
        assertThat(q.initFiredTime).isNotNull();
        assertThat(q.jobGroup).isNotNull();
        assertThat(q.jobName).isNotNull();
        assertThat(q.messageException).isNotNull();
        assertThat(q.scheduledTime).isNotNull();
        assertThat(q.schedulerName).isNotNull();
        assertThat(q.state).isNotNull();
        assertThat(q.triggerGroup).isNotNull();
        assertThat(q.triggerName).isNotNull();
    }

    @Test
    void testConstructors() {
        // Path constructor
        QQrtzLogTriggerExecuted original = new QQrtzLogTriggerExecuted("original");
        QQrtzLogTriggerExecuted fromPath = new QQrtzLogTriggerExecuted(original);
        assertThat(fromPath).isNotNull();

        // PathMetadata constructor
        PathMetadata metadata = original.getMetadata();
        QQrtzLogTriggerExecuted fromMetadata = new QQrtzLogTriggerExecuted(metadata);
        assertThat(fromMetadata).isNotNull();
    }

    @Test
    void testFieldTypes() {
        QQrtzLogTriggerExecuted q = new QQrtzLogTriggerExecuted("test");

        assertThat(q.id.getType()).isEqualTo(Long.class);
        assertThat(q.endFiredTime.getType()).isEqualTo(Instant.class);
        assertThat(q.initFiredTime.getType()).isEqualTo(Instant.class);

        // For ArrayPath, check the type of the path itself
        assertThat(q.messageException.getClass()).isEqualTo(com.querydsl.core.types.dsl.ArrayPath.class);
    }

}
