package org.acme.service.account;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeoutException;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

import io.smallrye.mutiny.Uni;
import io.vertx.core.json.JsonObject;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class BankAccountValidationEmitter {

    @Inject
    @Channel("bank-account-validation-requests")
    Emitter<JsonObject> bankAccountValidationEmitter;

    private final ConcurrentHashMap<String, CompletableFuture<Boolean>> pendingValidations = new ConcurrentHashMap<>();

    public Uni<Boolean> requestValidation(String bankAccountId) {
        String correlationId = UUID.randomUUID().toString();
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        pendingValidations.put(correlationId, future);

        JsonObject message = new JsonObject()
                .put("bankAccountId", bankAccountId)
                .put("correlationId", correlationId);
        bankAccountValidationEmitter.send(message);

        return Uni.createFrom().completionStage(future)
                .ifNoItem().after(Duration.ofSeconds(5))
                .failWith(new TimeoutException("Timeout: Bank account validation took too long"));
    }

    public void receiveValidationResult(String correlationId, boolean isValid) {
        CompletableFuture<Boolean> future = pendingValidations.remove(correlationId);
        if (future != null) {
            future.complete(isValid);
        }
    }
}
