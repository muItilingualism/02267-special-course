package org.acme.model.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentFailed {
    private String correlationId;
    private Throwable cause;
    private String message = "";
    private String exceptionType;

    public PaymentFailed(String correlationId, Throwable cause) {
        this.correlationId = correlationId;
        this.cause = cause;
        this.exceptionType = cause.getClass().getSimpleName();
    }

    public PaymentFailed(String correlationId, Throwable cause, String message) {
        this.correlationId = correlationId;
        this.cause = cause;
        this.message = message;
        this.exceptionType = cause.getClass().getSimpleName();
    }
}
