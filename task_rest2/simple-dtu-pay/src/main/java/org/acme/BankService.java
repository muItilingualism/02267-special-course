package org.acme;

import java.util.Optional;

import org.acme.model.Account;
import org.acme.model.AccountCreationRequest;
import org.acme.model.Payment;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import io.cucumber.core.internal.com.fasterxml.jackson.core.JsonProcessingException;
import io.cucumber.core.internal.com.fasterxml.jackson.databind.ObjectMapper;
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

    public String createAccount(AccountCreationRequest account) {
        Client client = ClientBuilder.newBuilder().build();
        WebTarget target = client.target(bankUrl + "/rest/accounts");

        Response response = target.request(MediaType.TEXT_PLAIN)
                //.post(Entity.entity(account, MediaType.APPLICATION_JSON)); direct object->json does not work???
                .post(Entity.entity(toJsonString(account), MediaType.APPLICATION_JSON));

        if (response.getStatus() != 200) {
            throw new Error("Failed to create bank account with error " + response.getStatus() + " and issue " + response.readEntity(String.class));
        }

        String createdAccountId = response.readEntity(String.class);
        response.close();
        client.close();

        return createdAccountId;
    }

    public void deleteAccount(String accountId) {
        Client client = ClientBuilder.newBuilder().build();
        WebTarget target = client.target(bankUrl + "/rest/accounts/" + accountId);

        Response response = target.request(MediaType.TEXT_PLAIN).delete();
        if (response.getStatus() != 204) {
            throw new Error("Failed to delete bank account with error " + response.getStatus() + " and issue " + response.readEntity(String.class));
        }

        response.close();
        client.close();
    }

    public String getAccountId(String accountCpr) {
        Client client = ClientBuilder.newBuilder().build();
        WebTarget target = client.target(bankUrl + "/rest/accounts/cpr/" + accountCpr);

        Response response = target.request(MediaType.APPLICATION_JSON).get();
        if (response.getStatus() != 200) {
            throw new Error("Failed to get bank account id of " + accountCpr + " with error " + response.getStatus()
                    + " and issue " + response.readEntity(String.class));
        }

        String jsonResponse = response.readEntity(String.class);

        Account account = toObject(jsonResponse, Account.class)
                .orElseThrow(() -> new Error("Failed to convert accountjson to account object."));

        response.close();
        client.close();

        return account.getId();
    }

    public void transferMoney(Payment payment) {
        Client client = ClientBuilder.newBuilder().build();
        WebTarget target = client.target(bankUrl + "/rest/payments");

        Response response = target.request(MediaType.TEXT_PLAIN)
                .post(Entity.entity(toJsonString(payment), MediaType.APPLICATION_JSON));

        if (response.getStatus() != 204) {
            throw new Error("Failed to transfer money with error " + response.getStatus() + " and issue "
                    + response.readEntity(String.class));
        }
    }

    private String toJsonString(Object object) {
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonObject = "";

        try {
            jsonObject = objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }

    private <T> Optional<T> toObject(String json, Class<T> valueType) {
        Optional<T> object;
        try {
            object = Optional.of(new ObjectMapper().readValue(json, valueType));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            object = Optional.empty();
        }

        return object;
    }
}
