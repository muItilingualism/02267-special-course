package org.acme.model.event;

import lombok.Getter;

@Getter
public class PaymentFailed extends PaymentProcessed {
    private Throwable cause;
    private String message = "";
    private String exceptionType;

    public PaymentFailed(String correlationId, Throwable cause) {
        super(correlationId);
        this.cause = cause;
        this.exceptionType = cause.getClass().getSimpleName();
    }
    public PaymentFailed(String correlationId, Throwable cause, String message) {
        super(correlationId);
        this.cause = cause;
        this.message = message;
        this.exceptionType = cause.getClass().getSimpleName();
    }
}
