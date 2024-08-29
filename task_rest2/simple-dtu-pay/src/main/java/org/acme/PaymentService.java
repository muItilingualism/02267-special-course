package org.acme;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.acme.model.Account;
import org.acme.model.Payment;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class PaymentService {
    private List<Payment> payments = new ArrayList<>();

    @ConfigProperty(name = "bank.url")
    String bankUrl;

    @Inject
    BankService bankService;

    public void savePayment(Payment payment) {
        payments.add(payment);

        Random random = new Random();

        Account account = new Account(1000, String.valueOf(random.nextInt()), "John", "Doe");
        
        String accountId = bankService.createAccount(account);
        bankService.deleteAccount(accountId);
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
