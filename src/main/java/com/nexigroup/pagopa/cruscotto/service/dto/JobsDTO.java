package com.nexigroup.pagopa.cruscotto.service.dto;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class JobsDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 9163988528260290579L;

    private String schedulerName;

    private String jobName;

    private String groupName;

    private Date scheduleTime;

    private Date lastFiredTime;

    private Date nextFireTime;

    private String jobStatus;

    private String cron;
}
