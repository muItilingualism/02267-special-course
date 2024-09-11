package org.acme.facade.event.accountregistration;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import org.acme.model.AccountRegistrationRequest;
import org.acme.model.event.AccountRegistrationProcessed;
import org.acme.model.event.CustomerAccountRegistrationCompleted;
import org.acme.model.event.CustomerAccountRegistrationFailed;
import org.acme.model.event.CustomerAccountRegistrationRequested;
import org.acme.model.event.MerchantAccountRegistrationCompleted;
import org.acme.model.event.MerchantAccountRegistrationFailed;
import org.acme.model.event.MerchantAccountRegistrationRequested;
import org.acme.service.account.AccountService;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Incoming;

import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import io.smallrye.reactive.messaging.annotations.Broadcast;
import io.smallrye.reactive.messaging.annotations.Merge;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class AccountRegistrationHandler {

    @Inject
    AccountService accountService;

    @Inject
    @Broadcast
    @Channel("customer-account-registration-requested")
    Emitter<CustomerAccountRegistrationRequested> customerAccountRegistrationEmitter;

    @Inject
    @Broadcast
    @Channel("merchant-account-registration-requested")
    Emitter<MerchantAccountRegistrationRequested> merchantAccountRegistrationEmitter;

    private final ConcurrentHashMap<String, CompletableFuture<String>> pendingAccountRegistrations = new ConcurrentHashMap<>();

    public Uni<String> emitProcessCustomerAcountRegistration(AccountRegistrationRequest account) {
        String correlationId = UUID.randomUUID().toString();
        CompletableFuture<String> future = new CompletableFuture<>();
        pendingAccountRegistrations.put(correlationId, future);
        customerAccountRegistrationEmitter.send(new CustomerAccountRegistrationRequested(correlationId, account));
        return Uni.createFrom().completionStage(future);
    }

    @Merge
    @Incoming("account-registration-processed")
    public void handleAccountRegistrationProcessed(AccountRegistrationProcessed event) {
        CompletableFuture<String> future = pendingAccountRegistrations.remove(event.getCorrelationId());
        if (future == null) {
            Log.warn("Received unknown or already removed account-registration-processed correlationId: "
                    + event.getCorrelationId());
            return;
        }

        if (event instanceof CustomerAccountRegistrationCompleted) {
            future.complete(((CustomerAccountRegistrationCompleted) event).getId());
        } else if (event instanceof MerchantAccountRegistrationCompleted) {
            future.complete(((MerchantAccountRegistrationCompleted) event).getId());
        } else if (event instanceof MerchantAccountRegistrationFailed) {
            future.completeExceptionally(((MerchantAccountRegistrationFailed) event).getCause());
        } else if (event instanceof CustomerAccountRegistrationFailed) {
            future.completeExceptionally(((CustomerAccountRegistrationFailed) event).getCause());
        } else {
            Log.warnf("Received unhandled AccountRegistrationProcessed event %s with correlationId: %s", event.getClass(),
                    event.getCorrelationId());
            future.completeExceptionally(new IllegalStateException("Received unhandled AccountRegistrationProcessed event"));
        }
    }

    public Uni<String> emitProcessMerchantAccountRegistration(AccountRegistrationRequest account) {
        String correlationId = UUID.randomUUID().toString();
        CompletableFuture<String> future = new CompletableFuture<>();
        pendingAccountRegistrations.put(correlationId, future);
        merchantAccountRegistrationEmitter.send(new MerchantAccountRegistrationRequested(correlationId, account));
        return Uni.createFrom().completionStage(future);
    }
}
