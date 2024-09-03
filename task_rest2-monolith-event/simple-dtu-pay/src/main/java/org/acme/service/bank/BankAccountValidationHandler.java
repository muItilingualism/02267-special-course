package org.acme.service.bank;

import io.smallrye.reactive.messaging.annotations.Blocking;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Incoming;

@ApplicationScoped
public class BankAccountValidationHandler {

    @Inject
    BankService bankService;

    @Incoming("bank-account-validation-in")
    @Blocking
    public boolean handleValidationRequest(String accountId) {
        return bankService.validateAccount(accountId);
    }
}