package org.acme.model.exception;

public class MoneyTransferException extends RuntimeException {
    public final int status;
    public final String message;

    public MoneyTransferException(int status, String message) {
        this.status = status;
        this.message = message;
    }
}
