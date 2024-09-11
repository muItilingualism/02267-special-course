package org.acme.model.event;

import java.util.concurrent.CompletableFuture;

import lombok.Getter;

@Getter
public class CustomerAccountRegistrationCompleted extends AccountRegistrationProcessed {
    private String id;

    public CustomerAccountRegistrationCompleted(String correlationId, String id) {
        super(correlationId);
        this.id = id;
    }

    @Override
    public void completeFuture(CompletableFuture<String> future) {
        future.complete(this.id);
    }
}