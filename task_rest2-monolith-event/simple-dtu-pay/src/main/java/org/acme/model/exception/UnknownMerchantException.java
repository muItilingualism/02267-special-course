package org.acme.model.exception;

public class UnknownMerchantException extends RuntimeException {
    public final String id;

    public UnknownMerchantException(String id) {
        this.id = id;
    }
}