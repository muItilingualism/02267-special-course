package org.acme;

import java.util.ArrayList;
import java.util.List;

import org.acme.model.Account;
import org.acme.model.Payment;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class PaymentService {
    private List<Payment> payments = new ArrayList<>();
    private List<String> accountIds = new ArrayList<>();

    @ConfigProperty(name = "bank.url")
    String bankUrl;

    @Inject
    BankService bankService;

    @PostConstruct
    public void postConstruct() {
        createDefaultAccounts();
    }

    @PreDestroy
    public void preDestroy() {
        deleteDefaultAccounts();
    }

    private void createDefaultAccounts() {
        Account customer = new Account(1000, "cid1", "Cust", "Omer");
        Account merchant = new Account(1000, "mid1", "Mer", "Chant");

        String customerId = bankService.createAccount(customer);
        String merchantId = bankService.createAccount(merchant);

        accountIds.add(customerId);
        accountIds.add(merchantId);
    }

    private void deleteDefaultAccounts() {
        for (String id : accountIds) {
            bankService.deleteAccount(id);
        }
    }

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
