package com.nexigroup.pagopa.cruscotto.service;

public class GenericServiceException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final String entityName;

    private final String errorKey;

    public GenericServiceException(String message, String entityName, String errorKey) {
        super(message);
        this.entityName = entityName;
        this.errorKey = errorKey;
    }

    public String getErrorKey() {
        return errorKey;
    }

    public String getEntityName() {
        return entityName;
    }
}
