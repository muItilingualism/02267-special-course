package org.acme.service.bank.event;

import org.acme.model.event.BankAccountValidationEvent;
import org.acme.model.event.BankAccountValidationEventType;
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
    Emitter<BankAccountValidationEvent> validationResultEmitter;

    @Incoming("bank-account-validation-requested")
    @Blocking
    public void processValidationRequest(BankAccountValidationEvent event) {
        String bankAccountId = event.getBankAccountId();
        String correlationId = event.getCorrelationId();
        this.handleBankAccountValidationRequest(correlationId, bankAccountId);
    }

    public void handleBankAccountValidationRequest(String correlationId, String bankAccountId) {
        boolean isValid = bankService.validateAccount(bankAccountId);

        BankAccountValidationEventType resultEventType = isValid
                ? BankAccountValidationEventType.BANK_ACCOUNT_VALIDATION_SUCCEEDED
                : BankAccountValidationEventType.BANK_ACCOUNT_VALIDATION_FAILED;

        BankAccountValidationEvent resultEvent = new BankAccountValidationEvent(
                correlationId, resultEventType, bankAccountId);
        validationResultEmitter.send(resultEvent);
    }
}