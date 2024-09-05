package org.acme.service.account;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.acme.model.Account;
import org.acme.model.AccountRegistrationRequest;
import org.acme.model.Customer;
import org.acme.model.Merchant;
import org.acme.model.exception.UnknownBankAccountIdException;
import org.acme.service.account.event.BankAccountValidationEmitter;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class AccountService {
    private List<Account> registeredAccounts = new ArrayList<>();

    @Inject
    BankAccountValidationEmitter validationEmitter;

    public Uni<String> processCustomerAccountRegistration(AccountRegistrationRequest account) {
        return validationEmitter.requestValidation(account.getBankAccountId())
                .onItem().transformToUni(isValid -> {
                    if (!isValid) {
                        return Uni.createFrom().failure(new UnknownBankAccountIdException(account.getBankAccountId()));
                    }
                    String id = generateAccountId();
                    registerAccount(new Customer(id, account.getFirstName(), account.getLastName(), account.getCpr(),
                            account.getBankAccountId()));
                    return Uni.createFrom().item(id);
                });
    }

    public Uni<String> processMerchantAccountRegistration(AccountRegistrationRequest account) {
        return validationEmitter.requestValidation(account.getBankAccountId())
                .onItem().transformToUni(isValid -> {
                    if (!isValid) {
                        return Uni.createFrom().failure(new UnknownBankAccountIdException(account.getBankAccountId()));
                    }
                    String id = generateAccountId();
                    registerAccount(new Merchant(id, account.getFirstName(), account.getLastName(), account.getCpr(),
                            account.getBankAccountId()));
                    return Uni.createFrom().item(id);
                });
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
