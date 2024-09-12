package org.acme.model.exception;

public class UnknownMerchantException extends Exception {
    public final String id;

    public UnknownMerchantException(String id) {
        this.id = id;
    }
}