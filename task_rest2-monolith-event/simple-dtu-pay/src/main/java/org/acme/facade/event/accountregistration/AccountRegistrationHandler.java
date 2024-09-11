package org.acme.facade.event.accountregistration;

import java.util.concurrent.CompletableFuture;

import org.acme.model.event.AccountRegistrationProcessed;
import org.acme.model.event.CustomerAccountRegistrationCompleted;
import org.acme.model.event.CustomerAccountRegistrationFailed;
import org.acme.model.event.MerchantAccountRegistrationCompleted;
import org.acme.model.event.MerchantAccountRegistrationFailed;
import org.eclipse.microprofile.reactive.messaging.Incoming;

import io.quarkus.logging.Log;
import io.smallrye.reactive.messaging.annotations.Merge;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class AccountRegistrationHandler {

    @Inject
    AccountRegistrationEmitter emitter;

    @Merge
    @Incoming("account-registration-processed")
    public void handleAccountRegistrationProcessed(AccountRegistrationProcessed event) {
        CompletableFuture<String> future = this.emitter.remove(event.getCorrelationId());
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

    
}
