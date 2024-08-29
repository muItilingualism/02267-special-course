package org.acme;

import org.acme.model.AccountCreationRequest;
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

    private String toJsonString(AccountCreationRequest account) {
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonAccount = "";

        try {
            jsonAccount = objectMapper.writeValueAsString(account);
            System.out.println("JSON payload: " + jsonAccount);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return jsonAccount;
    }
}
