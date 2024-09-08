package org.acme.facade;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import org.acme.model.AccountRegistrationRequest;
import org.acme.model.event.CustomerAccountRegistrationCompleted;
import org.acme.model.event.CustomerAccountRegistrationFailed;
import org.acme.model.event.CustomerAccountRegistrationProcessed;
import org.acme.model.event.CustomerAccountRegistrationRequested;
import org.acme.service.account.AccountService;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Incoming;

import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import io.smallrye.reactive.messaging.annotations.Broadcast;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;

@ApplicationScoped
public class AccountEventHandler {

    @Inject
    AccountService accountService;

    @Inject
    @Channel("customer-account-registration-requested")
    @Broadcast
    Emitter<CustomerAccountRegistrationRequested> customerAccountRegistrationEmitter;

    private final ConcurrentHashMap<String, CompletableFuture<String>> pendingAccountRegistrations = new ConcurrentHashMap<>();

    public Uni<String> emitProcessCustomerAcountRegistration(AccountRegistrationRequest account) {
        String correlationId = UUID.randomUUID().toString();
        CompletableFuture<String> future = new CompletableFuture<>();
        pendingAccountRegistrations.put(correlationId, future);
        customerAccountRegistrationEmitter.send(new CustomerAccountRegistrationRequested(correlationId, account));
        return Uni.createFrom().completionStage(future);
    }

    @Incoming("account-registration-processed")
    public void handleAccountRegistrationProcessed(CustomerAccountRegistrationProcessed event) {
        CompletableFuture<String> future = pendingAccountRegistrations.remove(event.getCorrelationId());
        if (future == null) {
            Log.warn("Received unknown or already removed account-registration-processed correlationId: "
                    + event.getCorrelationId());
            return;
        }

        if (event instanceof CustomerAccountRegistrationCompleted) {
            future.complete(((CustomerAccountRegistrationCompleted) event).getId());
        } else if (event instanceof CustomerAccountRegistrationFailed) {
            future.completeExceptionally(((CustomerAccountRegistrationFailed) event).getCause());
        } else {
            Log.warnf("Received unhandled AccountRegistrationProcessed event %s with correlationId: %s", event.getClass(),
                    event.getCorrelationId());
            future.completeExceptionally(new IllegalStateException("Received unhandled AccountRegistrationProcessed event"));
        }
    }

    public Uni<Response> emitProcessMerchantAccountRegistration(AccountRegistrationRequest account) {
        return accountService.processMerchantAccountRegistration(account)
                .onItem().transform(id -> Response.ok(id).build());
    }
}
