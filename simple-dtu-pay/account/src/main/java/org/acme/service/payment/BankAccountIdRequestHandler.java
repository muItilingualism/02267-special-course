package org.acme.service.payment;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeoutException;

import org.acme.model.event.BankAccountIdAssembled;
import org.acme.model.event.BankAccountIdRequested;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Incoming;

import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import io.smallrye.reactive.messaging.annotations.Broadcast;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class BankAccountIdRequestHandler {
    
    @Inject
    @Channel("bank-account-id-requested")
    @Broadcast
    Emitter<BankAccountIdRequested> emitter;

    private final ConcurrentHashMap<String, CompletableFuture<String>> pendingRequests = new ConcurrentHashMap<>();

    public Uni<String> emit(String accountId) {
        String correlationId = UUID.randomUUID().toString();
        CompletableFuture<String> future = new CompletableFuture<>();
        pendingRequests.put(correlationId, future);
        emitter.send(new BankAccountIdRequested(correlationId, accountId));
    
        return Uni.createFrom().completionStage(future)
                .ifNoItem().after(Duration.ofSeconds(5))
                .failWith(new TimeoutException("Timeout: Bank account id request took too long"));
    }

    public CompletableFuture<String> removeRequest(String correlationId) {
        return pendingRequests.remove(correlationId);
    }

    @Incoming("bank-account-id-assembled")
    public void handle(BankAccountIdAssembled event) {
        this.process(event.getCorrelationId(), event.getBankAccountId());
    }

    public void process(String correlationId, String bankAccountId) {
        CompletableFuture<String> future = this.removeRequest(correlationId);
        if (future != null) {
            future.complete(bankAccountId);
        } else {
            Log.error("Received unknown or already removed correlationId: " + correlationId);
        }
    }


}
