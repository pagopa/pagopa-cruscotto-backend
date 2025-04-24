package com.nexigroup.pagopa.cruscotto.service.dto;


import com.nexigroup.pagopa.cruscotto.domain.AnagPartner;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.PartnerStatus;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
/**
 * A DTO for the {@link AnagPartner} entity.
 */
@Getter
@Setter
@EqualsAndHashCode
public class AnagPartnerDTO implements Serializable {

    private static final long serialVersionUID = 3543581324420880777L;

    private Long id;

    private String fiscalCode;

    private String name;

    private PartnerStatus status;

}
