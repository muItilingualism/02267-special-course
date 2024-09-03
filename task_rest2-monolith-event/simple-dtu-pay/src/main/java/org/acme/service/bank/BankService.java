package org.acme.service.bank;

import java.util.Optional;

import org.acme.model.bank.Account;
import org.acme.model.bank.Payment;
import org.acme.model.exception.MoneyTransferException;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@ApplicationScoped
public class BankService {

    @ConfigProperty(name = "bank.url")
    String bankUrl;

    public Optional<Account> getAccount(String accountId) {
        Client client = ClientBuilder.newBuilder().build();
        WebTarget target = client.target(bankUrl + "/rest/accounts/" + accountId);

        Response response = target.request(MediaType.APPLICATION_JSON).get();
        if (response.getStatus() != 200) {
            return Optional.empty();
        }

        Account account = response.readEntity(Account.class);

        response.close();
        client.close();

        return Optional.of(account);
    }

    public void transferMoney(Payment payment) {
        Client client = ClientBuilder.newBuilder().build();
        WebTarget target = client.target(bankUrl + "/rest/payments");

        Response response = target.request(MediaType.TEXT_PLAIN)
                .post(Entity.entity(payment, MediaType.APPLICATION_JSON));

        if (response.getStatus() != 204) {
            throw new MoneyTransferException(response.getStatus(), response.readEntity(String.class));
        }
    }

    public boolean validateAccount(String accountId) {
        Optional<Account> account = getAccount(accountId);
        return account.isPresent();
    }
}
