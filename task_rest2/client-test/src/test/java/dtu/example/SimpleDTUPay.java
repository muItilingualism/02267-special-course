package dtu.example;

import java.util.ArrayList;
import java.util.List;

import dtu.example.model.Payment;
import dtu.example.model.PaymentResult;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

public class SimpleDTUPay {

    public PaymentResult pay(int amount, String customerId, String merchantId) {
        Payment payment = new Payment(amount, customerId, merchantId);
        Client client = ClientBuilder.newBuilder().build();
        WebTarget target = client.target("http://localhost:8080/payment");

        Response response = target.request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(payment, MediaType.APPLICATION_JSON));

        int statusCode = response.getStatus();
        String message = response.readEntity(String.class);

        response.close();

        return new PaymentResult(statusCode, message);
    }

    public List<Payment> list() {
        Client client = ClientBuilder.newBuilder().build();
        WebTarget target = client.target("http://localhost:8080/payment");

        String response = target.request(MediaType.APPLICATION_JSON).get(String.class);

        Jsonb jsonb = JsonbBuilder.create();
        List<Payment> list = jsonb.fromJson(response, new ArrayList<Payment>() {
        }.getClass().getGenericSuperclass());

        client.close();

        return list;
    }
}
