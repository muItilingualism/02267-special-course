package org.acme.model.event;

import java.util.concurrent.CompletableFuture;

import lombok.Getter;

@Getter
public class CustomerAccountRegistrationFailed extends AccountRegistrationProcessed {
    private Throwable cause;

    public CustomerAccountRegistrationFailed(String correlationId, Throwable cause) {
        super(correlationId);
        this.cause = cause;
    }

    @Override
    public void completeFuture(CompletableFuture<String> future) {
        future.completeExceptionally(this.cause);
    }
}
