package com.nexigroup.pagopa.cruscotto.service.dto;

import com.nexigroup.pagopa.cruscotto.domain.QrtzLogTriggerExecuted;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * A DTO for the {@link QrtzLogTriggerExecuted} entity.
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class QrtzLogTriggerExecutedDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 3295709024805276407L;

    private Long id;

    private String fireInstanceId;

    private String schedulerName;

    private String jobName;

    private String jobGroup;

    private Instant scheduledTime;

    private String triggerGroup;

    private String triggerName;

    private Instant initFiredTime;

    private Instant endFiredTime;

    private String state;

    private String messageException;
}
