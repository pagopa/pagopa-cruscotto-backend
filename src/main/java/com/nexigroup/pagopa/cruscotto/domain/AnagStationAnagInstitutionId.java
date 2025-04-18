package com.nexigroup.pagopa.cruscotto.domain;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AnagStationAnagInstitutionId implements Serializable {

    private Long anagStation;

    private Long anagInstitution;
}
