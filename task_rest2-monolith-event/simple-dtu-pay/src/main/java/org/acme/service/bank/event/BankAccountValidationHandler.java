package org.acme.service.bank.event;

import org.acme.model.event.BankAccountValidationCompleted;
import org.acme.model.event.BankAccountValidationRequestedEvent;
import org.acme.service.bank.BankService;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Incoming;

import io.smallrye.reactive.messaging.annotations.Blocking;
import io.smallrye.reactive.messaging.annotations.Broadcast;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class BankAccountValidationHandler {

    @Inject
    BankService bankService;

    @Inject
    @Channel("bank-account-validated")
    @Broadcast
    Emitter<BankAccountValidationCompleted> validationResultEmitter;

    @Incoming("bank-account-validation-requested")
    @Blocking
    public void processValidationRequest(BankAccountValidationRequestedEvent event) {
        this.handleBankAccountValidationRequest(event.getCorrelationId(), event.getBankAccountId());
    }

    public void handleBankAccountValidationRequest(String correlationId, String bankAccountId) {
        boolean isValid = bankService.validateAccount(bankAccountId);

        BankAccountValidationCompleted resultEvent = new BankAccountValidationCompleted(correlationId, isValid);
        validationResultEmitter.send(resultEvent);
    }
}