package org.acme.model.exception;

public class UnknownBankAccountIdException extends Exception {
    public final String id;

    public UnknownBankAccountIdException(String id) {
        this.id = id;
    }
}
