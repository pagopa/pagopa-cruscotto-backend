package com.nexigroup.pagopa.cruscotto.service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AnagInstitutionDTO {
	
	private InstitutionIdentificationDTO institutionIdentification;
	
	private String stationName;
	
	private String partnerName;
	
	private String partnerFiscalCode;
	
	private Boolean enabled;
	
	private Boolean aca;
	
	private Boolean standIn;


}
