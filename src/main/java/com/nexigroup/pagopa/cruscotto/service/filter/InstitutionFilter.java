package com.nexigroup.pagopa.cruscotto.service.filter;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InstitutionFilter implements Serializable {

	private static final long serialVersionUID = -2955236848194944412L;

	private String name;
    
    private String fiscalCode;
    
}
