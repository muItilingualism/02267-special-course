package org.acme.service.account.event.BankAccountValidation;

import org.acme.model.event.BankAccountValidationCompleted;
import org.eclipse.microprofile.reactive.messaging.Incoming;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class BankAccountValidationHandler {

    @Inject
    BankAccountValidationProcessor processor;

    @Incoming("bank-account-validated")
    public void handle(BankAccountValidationCompleted event) {
        processor.process(event.getCorrelationId(), event.isValid());
    }
}
