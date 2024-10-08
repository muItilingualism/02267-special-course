package org.acme.service.account.event.BankAccountValidation;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeoutException;

import org.acme.model.event.BankAccountValidationRequested;
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
    Emitter<BankAccountValidationRequested> bankAccountValidationEmitter;

    private final ConcurrentHashMap<String, CompletableFuture<Boolean>> pendingRequests = new ConcurrentHashMap<>();

    public Uni<Boolean> emit(String bankAccountId) {
        String correlationId = UUID.randomUUID().toString();
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        pendingRequests.put(correlationId, future);

        bankAccountValidationEmitter.send(
                new BankAccountValidationRequested(correlationId, bankAccountId));

        return Uni.createFrom().completionStage(future)
                .ifNoItem().after(Duration.ofSeconds(5))
                .failWith(new TimeoutException("Timeout: Bank account validation took too long"));
    }

    public CompletableFuture<Boolean> removeRequest(String correlationId) {
        return pendingRequests.remove(correlationId);
    }
}
