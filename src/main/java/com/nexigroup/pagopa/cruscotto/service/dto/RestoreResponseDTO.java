package com.nexigroup.pagopa.cruscotto.service.dto;

import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A DTO for restoring instances response.
 */
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class RestoreResponseDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private int successCount;

    private int failureCount;

    private List<InstanceRestoreResult> results;
}
