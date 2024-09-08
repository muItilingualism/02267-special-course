package org.acme.model.event;

import lombok.Getter;

@Getter
public class CustomerAccountRegistrationCompleted extends AccountRegistrationProcessed {
    private String id;

    public CustomerAccountRegistrationCompleted(String correlationId, String id) {
        super(correlationId);
        this.id = id;
    }
}