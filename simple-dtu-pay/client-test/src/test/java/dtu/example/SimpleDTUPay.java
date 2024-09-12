package dtu.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import dtu.example.model.simpledtupay.AccountRegistrationRequest;
import dtu.example.model.simpledtupay.Payment;
import dtu.example.model.simpledtupay.ResponseResult;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

public class SimpleDTUPay {

    private static final String url = "http://localhost:8080";

    public ResponseResult pay(int amount, String customerId, String merchantId) {
        Payment payment = new Payment(amount, customerId, merchantId);
        Client client = ClientBuilder.newBuilder().build();
        WebTarget target = client.target(url + "/payments");

        Response response = target.request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(toJsonString(payment), MediaType.APPLICATION_JSON));

        int statusCode = response.getStatus();
        String message = response.readEntity(String.class);

        response.close();

        return new ResponseResult(statusCode, message);
    }

    public List<Payment> list() {
        Client client = ClientBuilder.newBuilder().build();
        WebTarget target = client.target(url + "/payments");

        String response = target.request(MediaType.APPLICATION_JSON).get(String.class);

        Jsonb jsonb = JsonbBuilder.create();
        List<Payment> list = jsonb.fromJson(response, new ArrayList<Payment>() {
        }.getClass().getGenericSuperclass());

        client.close();

        return list;
    }

    public ResponseResult registerCustomer(String customerId, String bankAccountId) {
        AccountRegistrationRequest request = new AccountRegistrationRequest("Cust", "Omer", customerId, bankAccountId);
        Client client = ClientBuilder.newBuilder().build();
        WebTarget target = client.target(url + "/customers");

        Response response = target.request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(toJsonString(request), MediaType.APPLICATION_JSON));

        int statusCode = response.getStatus();
        String message = response.readEntity(String.class);

        response.close();

        return new ResponseResult(statusCode, message);
    }

    public ResponseResult registerMerchant(String merchantId, String bankAccountId) {
        AccountRegistrationRequest request = new AccountRegistrationRequest("Mer", "Chant", merchantId, bankAccountId);
        Client client = ClientBuilder.newBuilder().build();
        WebTarget target = client.target(url + "/merchants");

        Response response = target.request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(toJsonString(request), MediaType.APPLICATION_JSON));

        int statusCode = response.getStatus();
        String message = response.readEntity(String.class);

        response.close();

        return new ResponseResult(statusCode, message);
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
