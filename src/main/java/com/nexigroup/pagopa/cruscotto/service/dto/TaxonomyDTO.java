package com.nexigroup.pagopa.cruscotto.service.dto;

import com.nexigroup.pagopa.cruscotto.domain.Taxonomy;
import jakarta.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * A DTO for the {@link Taxonomy} entity.
 */
@Getter
@Setter
@EqualsAndHashCode
public class TaxonomyDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -5947564598045644907L;

    private Long id;

    private String institutionTypeCode;

    private String institutionType;

    private String areaProgressiveCode;

    private String areaName;

    private String areaDescription;

    private String serviceTypeCode;

    private String serviceType;

    private String serviceTypeDescription;

    private String version;

    private String reasonCollection;

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
        return (
            "TaxonomyDTO{" +
            "id=" +
            id +
            ", takingsIdentifier='" +
            takingsIdentifier +
            '\'' +
            ", validityStartDate=" +
            validityStartDate +
            ", validityEndDate=" +
            validityEndDate +
            ", createdBy='" +
            createdBy +
            '\'' +
            ", createdDate=" +
            createdDate +
            ", lastModifiedBy='" +
            lastModifiedBy +
            '\'' +
            ", lastModifiedDate=" +
            lastModifiedDate +
            '}'
        );
    }
}
