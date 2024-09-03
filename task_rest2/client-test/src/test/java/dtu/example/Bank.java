package dtu.example;

import java.util.Optional;

import dtu.example.model.bank.Account;
import dtu.example.model.bank.AccountCreationRequest;
import io.cucumber.core.internal.com.fasterxml.jackson.core.JsonProcessingException;
import io.cucumber.core.internal.com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

public class Bank {

    public String createAccount(AccountCreationRequest account) {
        Client client = ClientBuilder.newBuilder().build();
        WebTarget target = client.target("http://localhost:8081" + "/rest/accounts");

        Response response = target.request(MediaType.TEXT_PLAIN)
                // .post(Entity.entity(account, MediaType.APPLICATION_JSON)); direct object->json does not work???
                .post(Entity.entity(toJsonString(account), MediaType.APPLICATION_JSON));

        if (response.getStatus() != 200) {
            throw new Error("Failed to create bank account with error " + response.getStatus() + " and issue "
                    + response.readEntity(String.class));
        }

        String createdAccountId = response.readEntity(String.class);
        response.close();
        client.close();

        return createdAccountId;
    }

    public void deleteAccount(String accountId) {
        Client client = ClientBuilder.newBuilder().build();
        WebTarget target = client.target("http://localhost:8081" + "/rest/accounts/" + accountId);

        Response response = target.request(MediaType.TEXT_PLAIN).delete();
        if (response.getStatus() != 204) {
            // ignore
        }

        response.close();
        client.close();
    }

    public Account getAccount(String accountId) {
        Client client = ClientBuilder.newBuilder().build();
        WebTarget target = client.target("http://localhost:8081" + "/rest/accounts/" + accountId);

        Response response = target.request(MediaType.APPLICATION_JSON).get();
        if (response.getStatus() != 200) {
            throw new Error("Failed to get bank account by bankaccountID: " + accountId);
        }

        Account account = toObject(response.readEntity(String.class), Account.class).get();

        response.close();
        client.close();

        return account;
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
