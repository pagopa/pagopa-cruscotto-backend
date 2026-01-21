package com.nexigroup.pagopa.cruscotto.service.dto;

import java.io.Serializable;

/**
 * DTO for filtering report list queries.
 */
public class ReportFilterDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long instanceId;
    private String status;

    public ReportFilterDTO() {}

    public ReportFilterDTO(Long instanceId, String status) {
        this.instanceId = instanceId;
        this.status = status;
    }

    // Getters and Setters

    public Long getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(Long instanceId) {
        this.instanceId = instanceId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "ReportFilterDTO{" + "instanceId=" + instanceId + ", status='" + status + '\'' + '}';
    }
}
