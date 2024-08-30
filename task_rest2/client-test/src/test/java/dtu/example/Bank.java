package dtu.example;

import java.util.Optional;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import dtu.example.model.AccountCreationRequest;
import io.cucumber.core.internal.com.fasterxml.jackson.core.JsonProcessingException;
import io.cucumber.core.internal.com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

public class Bank {

    @ConfigProperty(name = "bank.url")
    String bankUrl;

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
