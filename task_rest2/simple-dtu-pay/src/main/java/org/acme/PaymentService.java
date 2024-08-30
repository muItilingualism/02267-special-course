package org.acme;

import java.util.ArrayList;
import java.util.List;

import org.acme.model.Payment;
import org.acme.model.PaymentRequest;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class PaymentService {
    private List<PaymentRequest> paymentsRequests = new ArrayList<>();

    @ConfigProperty(name = "bank.url")
    String bankUrl;

    @Inject
    BankService bankService;

    public void processPayment(PaymentRequest paymentRequest) {
        paymentsRequests.add(paymentRequest);

        Payment payment = new Payment(paymentRequest.getAmount(),
                paymentRequest.getCustomerId(),
                paymentRequest.getMerchantId(),
                String.format("TRANSACTION OF %d BY %s TO %s",
                        paymentRequest.getAmount(), paymentRequest.getCustomerId(), paymentRequest.getMerchantId()));
        bankService.transferMoney(payment);
    }

    public List<PaymentRequest> getAllPayments() {
        return new ArrayList<>(paymentsRequests);
    }
}
