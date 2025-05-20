package com.nexigroup.pagopa.cruscotto.service.dto;

import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JobsDTO implements Serializable {

    private static final long serialVersionUID = 9163988528260290579L;

    private String jobName;

    private String groupName;

    private Date scheduleTime;

    private Date lastFiredTime;

    private Date nextFireTime;

    private String jobStatus;

    @Override
    public String toString() {
        return (
            "JobsDTO{" +
            "jobName='" +
            jobName +
            '\'' +
            ", groupName='" +
            groupName +
            '\'' +
            ", scheduleTime=" +
            scheduleTime +
            ", lastFiredTime=" +
            lastFiredTime +
            ", nextFireTime=" +
            nextFireTime +
            ", jobStatus='" +
            jobStatus +
            '\'' +
            '}'
        );
    }
}
