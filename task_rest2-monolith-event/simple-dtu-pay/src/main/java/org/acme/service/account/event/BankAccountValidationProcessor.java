package org.acme.service.account.event;

import org.acme.model.event.BankAccountValidationCompleted;
import org.eclipse.microprofile.reactive.messaging.Incoming;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class BankAccountValidationProcessor {

    @Inject
    BankAccountValidationHandler handler;

    @Incoming("bank-account-validated")
    public void process(BankAccountValidationCompleted event) {
        handler.handle(event.getCorrelationId(), event.isValid());
    }
}
