package org.acme.facade.event.accountregistration;

import java.util.concurrent.CompletableFuture;

import org.acme.model.event.AccountRegistrationProcessed;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class AccountRegistrationProcessor {

    @Inject
    AccountRegistrationEmitter emitter;

    public void processAccountRegistrationProcessed(AccountRegistrationProcessed event) {
        CompletableFuture<String> future = this.emitter.remove(event.getCorrelationId());
        if (future == null) {
            Log.warn("Received unknown or already removed account-registration-processed correlationId: "
                    + event.getCorrelationId());
            return;
        }

        try {
            event.completeFuture(future);
        } catch (Exception e) {
            Log.warnf("Received unhandled AccountRegistrationProcessed event %s with correlationId: %s",
                    event.getClass(),
                    event.getCorrelationId());
            future.completeExceptionally(
                    new IllegalStateException("Received unhandled AccountRegistrationProcessed event", e));
        }
    }
}
