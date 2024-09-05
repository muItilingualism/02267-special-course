package org.acme.service.account;

import org.acme.model.event.BankAccountValidationEvent;
import org.eclipse.microprofile.reactive.messaging.Incoming;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class BankAccountValidationConsumer {

    @Inject
    BankAccountValidationEmitter validationEmitter;

    @Incoming("bank-account-validation-response")
    public void consumeValidationResult(BankAccountValidationEvent event) {
        String correlationId = event.getCorrelationId();

        switch (event.getEventType()) {
            case BANK_ACCOUNT_VALIDATION_SUCCEEDED:
                validationEmitter.receiveValidationResult(correlationId, true);
                break;
            case BANK_ACCOUNT_VALIDATION_FAILED:
                validationEmitter.receiveValidationResult(correlationId, false);
                break;
            case BANK_ACCOUNT_VALIDATION_REQUESTED:
                // ignore
                break;
        }
    }
}
