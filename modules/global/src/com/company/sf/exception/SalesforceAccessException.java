package com.company.sf.exception;

import com.haulmont.cuba.core.global.SupportedByClient;

/**
 */
@SupportedByClient
public class SalesforceAccessException extends RuntimeException {
    public SalesforceAccessException() {
    }

    public SalesforceAccessException(String message) {
        super(message);
    }

    public SalesforceAccessException(String message, Throwable cause) {
        super(message, cause);
    }

    public SalesforceAccessException(Throwable cause) {
        super(cause);
    }

    public SalesforceAccessException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
