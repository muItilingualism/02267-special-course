package org.acme.model.event;

import lombok.Getter;

@Getter
public class MerchantAccountRegistrationCompleted extends AccountRegistrationProcessed {
    private String id;

    public MerchantAccountRegistrationCompleted(String correlationId, String id) {
        super(correlationId);
        this.id = id;
    }
}