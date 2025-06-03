package com.nexigroup.pagopa.cruscotto.service.filter;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serial;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class JobExecutionFilter implements Serializable {

    @Serial
    private static final long serialVersionUID = -7846373878267327L;

    @NotNull
    @Size(max = 120)
    private String schedulerName;

    @NotNull
    @Size(max = 200)
    private String jobName;

    @NotNull
    @Size(max = 200)
    private String jobGroup;
}
