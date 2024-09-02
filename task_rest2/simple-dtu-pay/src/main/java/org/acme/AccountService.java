package org.acme;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.acme.model.Account;
import org.acme.model.exception.UnknownBankAccountIdException;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class AccountService {
    private List<Account> registeredAccounts = new ArrayList<>();

    @Inject
    BankService bankService;

    public boolean isValidBankAccount(String id) {
        return bankService.getAccount(id).isPresent();
    }

    public void processAccountRegistration(String bankAccountId) {
        Optional<Account> bankAccount = bankService.getAccount(bankAccountId);

        if (bankAccount.isPresent()) {
            registerAccount(bankAccount.get());
        } else {
            throw new UnknownBankAccountIdException(bankAccountId);
        }
    }

    private void registerAccount(Account bankAccount) {
        registeredAccounts.add(bankAccount);
    }
}
