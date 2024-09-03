package org.acme.model.exception;

public class UnknownCustomerException extends RuntimeException {
    public final String id;

    public UnknownCustomerException(String id) {
        this.id = id;
    }
}
