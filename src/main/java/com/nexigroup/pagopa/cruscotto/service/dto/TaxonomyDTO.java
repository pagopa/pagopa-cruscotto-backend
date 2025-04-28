package com.nexigroup.pagopa.cruscotto.service.dto;


import com.nexigroup.pagopa.cruscotto.domain.Taxonomy;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;

/**
 * A DTO for the {@link Taxonomy} entity.
 */
@Getter
@Setter
@EqualsAndHashCode
public class TaxonomyDTO implements Serializable {

    private static final long serialVersionUID = -5947564598045644907L;

    private Long id;

    @NotNull
    private String takingsIdentifier;

    private LocalDate validityStartDate;

    private LocalDate validityEndDate;

    private String createdBy;

    private Instant createdDate;

    private String lastModifiedBy;

    private Instant lastModifiedDate;

    @Override
    public String toString() {
        return "TaxonomyDTO{" +
            "id=" + id +
            ", takingsIdentifier='" + takingsIdentifier + '\'' +
            ", validityStartDate=" + validityStartDate +
            ", validityEndDate=" + validityEndDate +
            ", createdBy='" + createdBy + '\'' +
            ", createdDate=" + createdDate +
            ", lastModifiedBy='" + lastModifiedBy + '\'' +
            ", lastModifiedDate=" + lastModifiedDate +
            '}';
    }
}
