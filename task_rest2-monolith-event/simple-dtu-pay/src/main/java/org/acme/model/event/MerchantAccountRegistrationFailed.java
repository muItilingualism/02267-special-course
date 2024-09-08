package org.acme.model.event;

import lombok.Getter;

@Getter
public class MerchantAccountRegistrationFailed extends AccountRegistrationProcessed {
    private Throwable cause;

    public MerchantAccountRegistrationFailed(String correlationId, Throwable cause) {
        super(correlationId);
        this.cause = cause;
    }
}
