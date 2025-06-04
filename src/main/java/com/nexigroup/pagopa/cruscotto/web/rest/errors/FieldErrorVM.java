package com.nexigroup.pagopa.cruscotto.web.rest.errors;

import java.io.Serializable;

public class FieldErrorVM implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private final String applicationName;    

    private final String objectName;

    private final String field;

    private final String message;
    
    private final String type;
    

    public FieldErrorVM(String applicationName, String dto, String field, String message, String type) {
    	this.applicationName = applicationName;
        this.objectName = dto;
        this.field = field;
        this.message = message;
        this.type = type;
    }

    public String getApplicationName() {
		return applicationName;
	}

	public String getObjectName() {
        return objectName;
    }

    public String getField() {
        return field;
    }

    public String getMessage() {
        return message;
    }
    
	public String getType() {
		return type;
	}    
}
