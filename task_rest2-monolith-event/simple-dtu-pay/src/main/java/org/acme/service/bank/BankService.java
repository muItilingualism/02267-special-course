package org.acme.service.bank;

import java.util.Optional;

import org.acme.model.bank.Account;
import org.acme.model.bank.BankError;
import org.acme.model.bank.Payment;
import org.acme.model.exception.MoneyTransferException;
import org.acme.model.exception.UnknownCustomerException;
import org.acme.model.exception.UnknownMerchantException;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.SneakyThrows;

@ApplicationScoped
public class BankService {

    @ConfigProperty(name = "bank.url")
    String bankUrl;

    public Optional<Account> getAccount(String accountId) {
        Client client = ClientBuilder.newBuilder().build();
        WebTarget target = client.target(bankUrl + "/rest/accounts/" + accountId);

        try (Response response = target.request(MediaType.APPLICATION_JSON).get()) {
            if (response.getStatus() != 200) {
                return Optional.empty();
            }

            Account account = response.readEntity(Account.class);
            return Optional.of(account);
        } finally {
            client.close();
        }
    }

    @SneakyThrows
    public void transferMoney(Payment payment) {
        Client client = ClientBuilder.newBuilder().build();
        WebTarget target = client.target(bankUrl + "/rest/payments");

        try (Response response = target.request(MediaType.TEXT_PLAIN)
                .post(Entity.entity(payment, MediaType.APPLICATION_JSON))) {
            if (response.getStatus() == 204) {
                return;
            } else {
                this.handleTransferMoneyError(payment, response);
            }
        } finally {
            client.close();
        }
    }

    private void handleTransferMoneyError(Payment payment, Response response)
            throws UnknownCustomerException, UnknownMerchantException, MoneyTransferException {
        int errorStatus = response.getStatus();
        String errorBody = response.readEntity(String.class);

        switch (response.getStatus()) {
            case 400:
                if (BankError.NO_DEBTOR.getErrorMessage().equals(errorBody)) {
                    throw new UnknownMerchantException(payment.getDebtor());
                } else if (BankError.NO_CREDITOR.getErrorMessage().equals(errorBody)) {
                    throw new UnknownCustomerException(payment.getCreditor());
                }
                // fall-through
            default:
                throw new MoneyTransferException(errorStatus, errorBody);
        }
    }

    public boolean validateAccount(String accountId) {
        Optional<Account> account = getAccount(accountId);
        return account.isPresent();
    }
}
