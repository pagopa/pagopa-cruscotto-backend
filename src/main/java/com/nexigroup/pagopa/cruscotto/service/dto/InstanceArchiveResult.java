package com.nexigroup.pagopa.cruscotto.service.dto;

import java.io.Serializable;
import java.util.Map;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A DTO representing the result of archiving a single instance.
 */
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
public class InstanceArchiveResult implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long instanceId;

    private boolean success;

    private String errorMessage;

    private Map<String, String> params;

    public InstanceArchiveResult(Long instanceId, boolean success) {
        this.instanceId = instanceId;
        this.success = success;
    }

    public InstanceArchiveResult(Long instanceId, boolean success, String errorMessage) {
        this.instanceId = instanceId;
        this.success = success;
        this.errorMessage = errorMessage;
    }

    public InstanceArchiveResult(Long instanceId, boolean success, String errorMessage, Map<String, String> params) {
        this.instanceId = instanceId;
        this.success = success;
        this.errorMessage = errorMessage;
        this.params = params;
    }
}
