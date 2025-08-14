package com.nexigroup.pagopa.cruscotto.service.dto;

import java.io.Serializable;
import java.time.Instant;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * A DTO for incorrect taxonomy aggregate position data, aggregated by transfer category.
 */
@Getter
@Setter
@EqualsAndHashCode
public class PagoPaTaxonomyIncorrectDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Ora più precoce nel giorno per questa transfer_category
     */
    private Instant fromHour;

    /**
     * Ora più tardiva nel giorno per questa transfer_category
     */
    private Instant endHour;

    /**
     * Categoria di trasferimento (chiave di aggregazione)
     */
    private String transferCategory;

    /**
     * Totale aggregato per questa transfer_category nella giornata
     */
    private Long total;

    @Override
    public String toString() {
        return (
            "PagoPaTaxonomyIncorrectDTO [fromHour=" +
            fromHour +
            ", endHour=" +
            endHour +
            ", transferCategory=" +
            transferCategory +
            ", total=" +
            total +
            "]"
        );
    }
}
