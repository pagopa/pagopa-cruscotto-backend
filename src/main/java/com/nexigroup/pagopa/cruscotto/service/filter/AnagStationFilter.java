package com.nexigroup.pagopa.cruscotto.service.filter;

import java.io.Serial;
import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AnagStationFilter implements Serializable {

	@Serial
	private static final long serialVersionUID = 4769113909045723465L;
	
    private Long partnerId;
    
    private Long stationId;
    
    private Boolean showNotActive;


}
