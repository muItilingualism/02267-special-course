package org.acme.service.account.event;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeoutException;

import org.acme.model.event.BankAccountValidationRequestedEvent;
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
    Emitter<BankAccountValidationRequestedEvent> bankAccountValidationEmitter;

    private final ConcurrentHashMap<String, CompletableFuture<Boolean>> pendingValidations = new ConcurrentHashMap<>();

    public Uni<Boolean> emit(String bankAccountId) {
        String correlationId = UUID.randomUUID().toString();
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        pendingValidations.put(correlationId, future);

        bankAccountValidationEmitter.send(
                new BankAccountValidationRequestedEvent(correlationId, bankAccountId));

        return Uni.createFrom().completionStage(future)
                .ifNoItem().after(Duration.ofSeconds(5))
                .failWith(new TimeoutException("Timeout: Bank account validation took too long"));
    }

    public CompletableFuture<Boolean> removePendingValidation(String correlationId) {
        return pendingValidations.remove(correlationId);
    }
}
