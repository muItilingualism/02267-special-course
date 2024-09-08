package org.acme.model.event;

import lombok.Getter;

@Getter
public class CustomerAccountRegistrationFailed extends AccountRegistrationProcessed {
    private Throwable cause;

    public CustomerAccountRegistrationFailed(String correlationId, Throwable cause) {
        super(correlationId);
        this.cause = cause;
    }
}
