package org.acme.service.account.event;

import org.acme.model.event.BankAccountValidationCompleted;
import org.eclipse.microprofile.reactive.messaging.Incoming;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class BankAccountValidationConsumer {

    @Inject
    BankAccountValidationEmitter validationEmitter;

    @Incoming("bank-account-validated")
    public void consumeValidationResult(BankAccountValidationCompleted event) {
        validationEmitter.receiveValidationResult(event.getCorrelationId(), event.isValid());
    }
}
