package com.nexigroup.pagopa.cruscotto.service.filter;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AnagInstitutionFilter implements Serializable {

	private static final long serialVersionUID = -2955236848194944412L;

	private Long institutionId;
	
	private Long partnerId;
	
	private Long stationId;
	
	private Boolean showNotEnabled;


}
