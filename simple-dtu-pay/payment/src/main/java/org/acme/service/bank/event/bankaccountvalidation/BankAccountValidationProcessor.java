package org.acme.service.bank.event.bankaccountvalidation;

import org.acme.service.bank.BankService;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class BankAccountValidationProcessor {

    @Inject
    BankService bankService;

    @Inject
    BankAccountValidationEmitter emitter;

    public void process(String correlationId, String bankAccountId) {
        boolean isValid = bankService.isAccountValid(bankAccountId);
        emitter.emit(correlationId, isValid);
    }
}
