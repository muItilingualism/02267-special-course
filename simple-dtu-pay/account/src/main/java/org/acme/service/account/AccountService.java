package org.acme.service.account;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

import org.acme.model.Account;
import org.acme.model.AccountRegistrationRequest;
import org.acme.model.Customer;
import org.acme.model.Merchant;
import org.acme.model.event.CustomerAccountRegistrationCompleted;
import org.acme.model.event.CustomerAccountRegistrationFailed;
import org.acme.model.event.CustomerAccountRegistrationRequested;
import org.acme.model.event.MerchantAccountRegistrationCompleted;
import org.acme.model.event.MerchantAccountRegistrationFailed;
import org.acme.model.event.MerchantAccountRegistrationRequested;
import org.acme.model.exception.UnknownBankAccountIdException;
import org.acme.service.account.event.BankAccountValidation.BankAccountValidationEmitter;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;

import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import io.smallrye.reactive.messaging.annotations.Broadcast;
import io.vertx.core.json.JsonObject;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class AccountService {
    private List<Account> registeredAccounts = new ArrayList<>();

    @Inject
    BankAccountValidationEmitter validationEmitter;

    @Inject
    @Broadcast
    @Channel("customer-account-registration-completed")
    Emitter<CustomerAccountRegistrationCompleted> customerCompleteEmitter;

    @Inject
    @Broadcast
    @Channel("customer-account-registration-failed")
    Emitter<CustomerAccountRegistrationFailed> customerFailEmitter;

    @Incoming("customer-account-registration-requested")
    public Uni<Void> processCustomerAccountRegistration(
            JsonObject jsonEvent) {
        Log.fatal("Received customer-account-registration-request");
        CustomerAccountRegistrationRequested event = jsonEvent.mapTo(CustomerAccountRegistrationRequested.class);

        AccountRegistrationRequest account = event.getRequest();
        Log.fatal("emitting and awaiting: " + account.getBankAccountId());
        return validationEmitter.emit(account.getBankAccountId())
                .onItem().transformToUni(isValid -> {
                    if (!isValid) {
                        Log.fatal("not valid! nooo: " + isValid);
                        return Uni.createFrom().failure(new UnknownBankAccountIdException(account.getBankAccountId()));
                    }
                    Log.fatal("valid wooo!");

                    String id = generateAccountId();
                    registerAccount(new Customer(id, account.getFirstName(), account.getLastName(), account.getCpr(),
                            account.getBankAccountId()));

                    customerCompleteEmitter.send(new CustomerAccountRegistrationCompleted(
                            event.getCorrelationId(), id));

                    return Uni.createFrom().voidItem();
                })
                .onFailure().recoverWithItem(e -> {
                    Log.fatalf("failed! uhooooh %s", e.getMessage());
                    customerFailEmitter.send(new CustomerAccountRegistrationFailed(event.getCorrelationId(), e));
                    return null;
                })
                .ifNoItem().after(Duration.ofSeconds(4)).failWith(
                        new TimeoutException("Timeout: Bank account ID request took too long"));
    }

    @Inject
    @Broadcast
    @Channel("merchant-account-registration-completed")
    Emitter<MerchantAccountRegistrationCompleted> merchantCompleteEmitter;

    @Inject
    @Broadcast
    @Channel("merchant-account-registration-failed")
    Emitter<MerchantAccountRegistrationFailed> merchantFailEmitter;

    @Incoming("merchant-account-registration-requested")
    @Broadcast
    @Outgoing("account-registration-processed")
    public Uni<Void> processMerchantAccountRegistration(
            JsonObject jsonEvent) {
        Log.fatal("Received merchant-account-registration-request");

        MerchantAccountRegistrationRequested event = jsonEvent.mapTo(MerchantAccountRegistrationRequested.class);

        AccountRegistrationRequest account = event.getRequest();
        return validationEmitter.emit(account.getBankAccountId())
                .onItem().transformToUni(isValid -> {
                    if (!isValid) {
                        return Uni.createFrom().failure(new UnknownBankAccountIdException(account.getBankAccountId()));
                    }
                    String id = generateAccountId();
                    registerAccount(new Merchant(id, account.getFirstName(), account.getLastName(), account.getCpr(),
                            account.getBankAccountId()));

                    merchantCompleteEmitter.send(new MerchantAccountRegistrationCompleted(
                            event.getCorrelationId(), id));

                    return Uni.createFrom().voidItem();
                })
                .onFailure().recoverWithItem(e -> {
                    merchantFailEmitter.send(new MerchantAccountRegistrationFailed(event.getCorrelationId(), e));
                    return null;
                })
                .ifNoItem().after(Duration.ofSeconds(5)).failWith(
                        new TimeoutException("Timeout: Bank account ID request took too long"));
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
