package org.acme;

import java.util.ArrayList;
import java.util.List;

import org.acme.model.Payment;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PaymentService {
    private List<Payment> payments = new ArrayList<>();

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
}
