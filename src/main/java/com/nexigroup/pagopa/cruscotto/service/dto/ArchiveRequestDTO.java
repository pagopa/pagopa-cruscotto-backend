package com.nexigroup.pagopa.cruscotto.service.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * A DTO for archiving instances request.
 */
@Getter
@Setter
@EqualsAndHashCode
public class ArchiveRequestDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotEmpty(message = "Instance IDs list cannot be empty")
    @Size(max = 100, message = "Cannot archive more than 100 instances at once")
    private List<Long> instanceIds;
}
