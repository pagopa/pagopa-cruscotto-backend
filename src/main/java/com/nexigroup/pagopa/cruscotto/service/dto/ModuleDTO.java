package com.nexigroup.pagopa.cruscotto.service.dto;

import com.nexigroup.pagopa.cruscotto.domain.Instance;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.AnalysisType;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.ModuleStatus;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * A DTO for the {@link Module} entity.
 */

@Getter
@Setter
@EqualsAndHashCode
public class ModuleDTO implements Serializable {

    private static final long serialVersionUID = 8436953315566863920L;

    private Long id;

    private String code;

    private String name;

    private String description;

    private AnalysisType analysisType;

    private Boolean allowManualOutcome;

    private ModuleStatus status;
}
