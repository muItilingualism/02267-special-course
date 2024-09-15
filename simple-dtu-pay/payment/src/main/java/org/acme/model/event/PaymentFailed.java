package org.acme.model.event;

import lombok.Getter;

@Getter
public class PaymentFailed extends PaymentProcessed {
    private Throwable cause;
    private String message = "";

    public PaymentFailed(String correlationId, Throwable cause) {
        super(correlationId);
        this.cause = cause;
    }
    public PaymentFailed(String correlationId, Throwable cause, String message) {
        super(correlationId);
        this.cause = cause;
        this.message = message;
    }
}
