package dtu.example;

import dtu.example.model.Payment;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

public class SimpleDTUPay {

    public boolean pay(int amount, String customerId, String merchantId) {
        Payment payment = new Payment(amount, customerId, merchantId);
        Client client = ClientBuilder.newBuilder().build();
        WebTarget target = client.target("http://localhost:8080/payment");

        Response response = target.request()
                .post(Entity.entity(payment, MediaType.APPLICATION_JSON));

        response.close();

        return response.getStatus() == 200;
    }
}
