package com.nexigroup.pagopa.cruscotto.service.dto;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A DTO representing the result of restoring a single instance.
 */
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class InstanceRestoreResult implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long instanceId;

    private boolean success;

    private String errorMessage;

    public InstanceRestoreResult(Long instanceId, boolean success) {
        this.instanceId = instanceId;
        this.success = success;
    }
}
