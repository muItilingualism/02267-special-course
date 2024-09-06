package org.acme.service.bank.event;

import org.acme.service.bank.BankService;

import jakarta.inject.Inject;

public class BankAccountValidationProcessor {

    @Inject
    BankAccountValidationEmitter emitter;

    @Inject
    BankService bankService;

    public void process(String correlationId, String bankAccountId) {
        boolean isValid = bankService.validateAccount(bankAccountId);
        emitter.emit(correlationId, isValid);
    }
}
