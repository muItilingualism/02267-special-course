package org.acme.service.bank.event.bankaccountvalidation;

import org.acme.model.event.BankAccountValidationRequested;
import org.eclipse.microprofile.reactive.messaging.Incoming;

import io.smallrye.reactive.messaging.annotations.Blocking;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class BankAccountValidationHandler {

    @Inject
    BankAccountValidationProcessor processor;

    @Incoming("bank-account-validation-requested")
    @Blocking
    public void handle(BankAccountValidationRequested event) {
        processor.process(event.getCorrelationId(), event.getBankAccountId());
    }
}