package org.acme.service.account;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.acme.model.Account;
import org.acme.model.AccountRegistrationRequest;
import org.acme.model.Customer;
import org.acme.model.Merchant;
import org.acme.model.event.AccountRegistrationProcessed;
import org.acme.model.event.CustomerAccountRegistrationCompleted;
import org.acme.model.event.CustomerAccountRegistrationFailed;
import org.acme.model.event.CustomerAccountRegistrationRequested;
import org.acme.model.event.MerchantAccountRegistrationCompleted;
import org.acme.model.event.MerchantAccountRegistrationFailed;
import org.acme.model.event.MerchantAccountRegistrationRequested;
import org.acme.model.exception.UnknownBankAccountIdException;
import org.acme.service.account.event.BankAccountValidation.BankAccountValidationEmitter;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;

import io.smallrye.mutiny.Uni;
import io.smallrye.reactive.messaging.annotations.Broadcast;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class AccountService {
    private List<Account> registeredAccounts = new ArrayList<>();

    @Inject
    BankAccountValidationEmitter validationEmitter;

    @Incoming("customer-account-registration-requested")
    @Broadcast
    @Outgoing("account-registration-processed")
    public Uni<AccountRegistrationProcessed> processCustomerAccountRegistration(
            CustomerAccountRegistrationRequested event) {
        AccountRegistrationRequest account = event.getRequest();
        return validationEmitter.emit(account.getBankAccountId())
                .onItem().transformToUni(isValid -> {
                    if (!isValid) {
                        return Uni.createFrom().failure(new UnknownBankAccountIdException(account.getBankAccountId()));
                    }
                    String id = generateAccountId();
                    registerAccount(new Customer(id, account.getFirstName(), account.getLastName(), account.getCpr(),
                            account.getBankAccountId()));

                    return Uni.createFrom()
                            .item((AccountRegistrationProcessed) new CustomerAccountRegistrationCompleted(
                                    event.getCorrelationId(), id));
                })
                .onFailure().recoverWithItem(e -> new CustomerAccountRegistrationFailed(event.getCorrelationId(), e));
    }

    @Incoming("merchant-account-registration-requested")
    @Broadcast
    @Outgoing("account-registration-processed")
    public Uni<AccountRegistrationProcessed> processMerchantAccountRegistration(
            MerchantAccountRegistrationRequested event) {
        AccountRegistrationRequest account = event.getRequest();
        return validationEmitter.emit(account.getBankAccountId())
                .onItem().transformToUni(isValid -> {
                    if (!isValid) {
                        return Uni.createFrom().failure(new UnknownBankAccountIdException(account.getBankAccountId()));
                    }
                    String id = generateAccountId();
                    registerAccount(new Merchant(id, account.getFirstName(), account.getLastName(), account.getCpr(),
                            account.getBankAccountId()));

                    return Uni.createFrom()
                            .item((AccountRegistrationProcessed) new MerchantAccountRegistrationCompleted(
                                    event.getCorrelationId(), id));
                })
                .onFailure().recoverWithItem(e -> new MerchantAccountRegistrationFailed(event.getCorrelationId(), e));
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
