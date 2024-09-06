package org.acme.service.bank.event;

import org.acme.model.event.BankAccountValidationCompleted;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

import io.smallrye.reactive.messaging.annotations.Broadcast;
import jakarta.inject.Inject;

public class BankAccountValidationEmitter {

    @Inject
    @Channel("bank-account-validated")
    @Broadcast
    Emitter<BankAccountValidationCompleted> validationResultEmitter;

    public void emit(String correlationId, Boolean isValid) {
        BankAccountValidationCompleted resultEvent = new BankAccountValidationCompleted(correlationId, isValid);
        validationResultEmitter.send(resultEvent);
    }
}
