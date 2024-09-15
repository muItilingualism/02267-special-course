package org.acme.facade.event.accountregistration;

import java.util.concurrent.CompletableFuture;

import org.acme.model.event.CustomerAccountRegistrationCompleted;
import org.acme.model.event.CustomerAccountRegistrationFailed;
import org.acme.model.event.MerchantAccountRegistrationCompleted;
import org.acme.model.event.MerchantAccountRegistrationFailed;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class AccountRegistrationProcessor {

    @Inject
    AccountRegistrationEmitter emitter;

    public void processCustomerAccountRegistrationCompleted(CustomerAccountRegistrationCompleted event) {
        CompletableFuture<String> future = this.emitter.remove(event.getCorrelationId());
        if (future == null) {
            Log.warn("Received unknown or already removed account-registration-processed correlationId: "
                    + event.getCorrelationId());
            return;
        }
        event.completeFuture(future);
    }

    public void processCustomerAccountRegistrationFailed(CustomerAccountRegistrationFailed event) {
        CompletableFuture<String> future = this.emitter.remove(event.getCorrelationId());
        if (future == null) {
            Log.warn("Received unknown or already removed account-registration-processed correlationId: "
                    + event.getCorrelationId());
            return;
        }
        event.completeFuture(future);
    }

    public void processMerchantAccountRegistrationCompleted(MerchantAccountRegistrationCompleted event) {
        CompletableFuture<String> future = this.emitter.remove(event.getCorrelationId());
        if (future == null) {
            Log.warn("Received unknown or already removed account-registration-processed correlationId: "
                    + event.getCorrelationId());
            return;
        }
        event.completeFuture(future);
    }

    public void processMerchantAccountRegistrationFailed(MerchantAccountRegistrationFailed event) {
        CompletableFuture<String> future = this.emitter.remove(event.getCorrelationId());
        if (future == null) {
            Log.warn("Received unknown or already removed account-registration-processed correlationId: "
                    + event.getCorrelationId());
            return;
        }
        event.completeFuture(future);
    }
}
