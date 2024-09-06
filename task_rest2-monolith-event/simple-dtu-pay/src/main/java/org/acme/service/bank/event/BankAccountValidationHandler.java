package org.acme.service.bank.event;

import org.acme.model.event.BankAccountValidationRequestedEvent;
import org.acme.service.bank.BankService;
import org.eclipse.microprofile.reactive.messaging.Incoming;

import io.smallrye.reactive.messaging.annotations.Blocking;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class BankAccountValidationHandler {

    @Inject
    BankService bankService;

    @Inject
    BankAccountValidationProcessor processor;

    @Incoming("bank-account-validation-requested")
    @Blocking
    public void handle(BankAccountValidationRequestedEvent event) {
        processor.process(event.getCorrelationId(), event.getBankAccountId());
    }
}