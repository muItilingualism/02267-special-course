package org.acme;

import java.util.ArrayList;
import java.util.List;

import org.acme.model.Payment;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;

@ApplicationScoped
public class PaymentService {
    private List<Payment> payments = new ArrayList<>();

    @ConfigProperty(name = "bank.url")
    String bankUrl;

    public void savePayment(Payment payment) {
        payments.add(payment);
    }

    public List<Payment> getAllPayments() {
        return new ArrayList<>(payments);
    }

    public boolean isValidCustomer(String customerId) {
        return "cid1".equals(customerId);
    }

    public boolean isValidMerchant(String merchantId) {
        return "mid1".equals(merchantId);
    }

    public int testBank() {
        Client client = ClientBuilder.newBuilder().build();
        WebTarget target = client.target(bankUrl + "/index.html");

        Response response = target.request().get();
        int statusCode = response.getStatus();

        response.close();
        return statusCode;
    }
}
