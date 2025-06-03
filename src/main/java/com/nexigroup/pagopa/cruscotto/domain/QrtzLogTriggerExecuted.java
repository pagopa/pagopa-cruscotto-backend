package com.nexigroup.pagopa.cruscotto.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "QRTZ_LOG_TRIGGER_EXECUTED")
@Getter
@Setter
@ToString
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class QrtzLogTriggerExecuted implements Serializable {

    @Serial
    private static final long serialVersionUID = -6534673441738929810L;

    @Id
    @Column(name = "ID")
    @SequenceGenerator(name = "SQCRUSC8_QRTZLOGTRIGEXEC", sequenceName = "SQCRUSC8_QRTZLOGTRIGEXEC", allocationSize = 1)
    @GeneratedValue(generator = "SQCRUSC8_QRTZLOGTRIGEXEC", strategy = GenerationType.SEQUENCE)
    private Long id;

    @NotNull
    @Size(min = 1, max = 250)
    @Column(name = "fire_instance_id", nullable = false, length = 120)
    private String fireInstanceId;

    @NotNull
    @Size(min = 1, max = 120)
    @Column(name = "sched_name", nullable = false, length = 120)
    private String schedulerName;

    @NotNull
    @Size(min = 1, max = 200)
    @Column(name = "job_name", nullable = false, length = 200)
    private String jobName;

    @NotNull
    @Size(min = 1, max = 200)
    @Column(name = "job_group", nullable = false, length = 200)
    private String jobGroup;

    @NotNull
    @Column(name = "scheduled_time", nullable = false)
    private Instant scheduledTime;

    @NotNull
    @Size(min = 1, max = 200)
    @Column(name = "trigger_group", nullable = false, length = 200)
    private String triggerGroup;

    @NotNull
    @Size(min = 1, max = 200)
    @Column(name = "trigger_name", nullable = false, length = 200)
    private String triggerName;

    @Column(name = "init_fired_time")
    private Instant initFiredTime;

    @Column(name = "end_fired_time")
    private Instant endFiredTime;

    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "state", length = 50)
    private String state;

    @Column(name = "message_exception")
    private byte[] messageException;
}
