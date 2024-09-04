package org.acme.service.account;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

import org.acme.model.Account;
import org.acme.model.AccountRegistrationRequest;
import org.acme.model.Customer;
import org.acme.model.Merchant;
import org.acme.model.exception.UnknownBankAccountIdException;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class AccountService {
    private List<Account> registeredAccounts = new ArrayList<>();

    @Inject
    BankAccountValidationEmitter validationEmitter;

    public Uni<String> processCustomerAccountRegistration(AccountRegistrationRequest account) throws TimeoutException {
        return validationEmitter.requestValidation(account.getBankAccountId())
            .onItem().transform(isValid -> {
                if (!isValid) {
                    throw new UnknownBankAccountIdException(account.getBankAccountId());
                }
                String id = generateAccountId();
                registerAccount(new Customer(id, account.getFirstName(), account.getLastName(), account.getCpr(), account.getBankAccountId()));
                return id;
            });
    }

    public Uni<String> processMerchantAccountRegistration(AccountRegistrationRequest account) {
        return validationEmitter.requestValidation(account.getBankAccountId())
            .onItem().transform(isValid -> {
                if (!isValid) {
                    throw new UnknownBankAccountIdException(account.getBankAccountId());
                }
                String id = generateAccountId();
                registerAccount(new Merchant(id, account.getFirstName(), account.getLastName(), account.getCpr(), account.getBankAccountId()));
                return id;
            });
    }

    private Uni<Boolean> isValid(String bankAccountId) {
        return validationEmitter.requestValidation(bankAccountId);
    }

    private String generateAccountId() {
        return UUID.randomUUID().toString();
    }

    private void registerAccount(Account account) {
        registeredAccounts.add(account);
    }

    public Optional<String> getAccountBankId(String accountId) {
        for (Account account : registeredAccounts) {
            if (account.getId().equals(accountId)) {
                return Optional.of(account.getBankAccountId());
            }
        }
        return Optional.empty();
    }
}
