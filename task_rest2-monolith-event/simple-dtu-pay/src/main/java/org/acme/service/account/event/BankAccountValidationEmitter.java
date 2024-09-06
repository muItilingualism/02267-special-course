package org.acme.service.account.event;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeoutException;

import org.acme.model.event.BankAccountValidationEvent;
import org.acme.model.event.BankAccountValidationEventType;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

import io.smallrye.mutiny.Uni;
import io.smallrye.reactive.messaging.annotations.Broadcast;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class BankAccountValidationEmitter {

    @Inject
    @Channel("bank-account-validation-requested")
    @Broadcast
    Emitter<BankAccountValidationEvent> bankAccountValidationEmitter;

    private final ConcurrentHashMap<String, CompletableFuture<Boolean>> pendingValidations = new ConcurrentHashMap<>();

    public Uni<Boolean> requestValidation(String bankAccountId) {
        String correlationId = UUID.randomUUID().toString();
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        pendingValidations.put(correlationId, future);

        bankAccountValidationEmitter.send(
                new BankAccountValidationEvent(
                        correlationId, BankAccountValidationEventType.BANK_ACCOUNT_VALIDATION_REQUESTED,
                        bankAccountId));

        return Uni.createFrom().completionStage(future)
                .ifNoItem().after(Duration.ofSeconds(5))
                .failWith(new TimeoutException("Timeout: Bank account validation took too long"));
    }

    public void receiveValidationResult(String correlationId, boolean isValid) {
        CompletableFuture<Boolean> future = pendingValidations.remove(correlationId);
        if (future != null) {
            future.complete(isValid);
        } else {
            System.err.println("Received unknown or already removed correlationId: " + correlationId);
        }
    }
}
