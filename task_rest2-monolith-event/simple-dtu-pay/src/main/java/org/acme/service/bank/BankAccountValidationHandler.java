package org.acme.service.bank;

import org.acme.model.event.BankAccountValidationEvent;
import org.acme.model.event.BankAccountValidationEventType;
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
    @Channel("bank-account-validation-events")
    @Broadcast
    Emitter<BankAccountValidationEvent> validationResultEmitter;

    @Incoming("bank-account-validation-events")
    @Blocking
    public void handleValidationRequest(BankAccountValidationEvent event) {
        String bankAccountId = event.getBankAccountId();
        String correlationId = event.getCorrelationId();
        boolean isValid = bankService.validateAccount(bankAccountId);

        BankAccountValidationEventType resultEventType = isValid
                ? BankAccountValidationEventType.BANK_ACCOUNT_VALIDATION_SUCCEEDED
                : BankAccountValidationEventType.BANK_ACCOUNT_VALIDATION_FAILED;

        BankAccountValidationEvent resultEvent = new BankAccountValidationEvent(
                correlationId, resultEventType, bankAccountId);
        validationResultEmitter.send(resultEvent);

    }
}