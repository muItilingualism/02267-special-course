package org.acme.service.account.event;

import java.util.concurrent.CompletableFuture;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class BankAccountValidationProcessor {

    @Inject
    BankAccountValidationEmitter emitter;

    public void process(String correlationId, boolean isValid) {
        CompletableFuture<Boolean> future = emitter.removePendingValidation(correlationId);
        if (future != null) {
            future.complete(isValid);
        } else {
            System.err.println("Received unknown or already removed correlationId: " + correlationId);
        }
    }
}
