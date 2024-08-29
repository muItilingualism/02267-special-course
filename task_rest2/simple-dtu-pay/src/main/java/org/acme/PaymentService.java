package org.acme;

import java.util.ArrayList;
import java.util.List;

import org.acme.model.AccountCreationRequest;
import org.acme.model.Payment;
import org.acme.model.PaymentRequest;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class PaymentService {
    private List<PaymentRequest> paymentsRequests = new ArrayList<>();
    private List<String> accountIds = new ArrayList<>();
    private AccountCreationRequest customer = new AccountCreationRequest(1000, "cid1", "Cust", "Omer");
    private AccountCreationRequest merchant = new AccountCreationRequest(1000, "mid1", "Mer", "Chant");

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

    public void savePayment(PaymentRequest payment) {
        payments.add(payment);
    }

    public List<PaymentRequest> getAllPayments() {
        return new ArrayList<>(paymentsRequests);
    }

    public boolean isValidCustomer(String customerCpr) {
        return customer.getUser().getCprNumber().equals(customerCpr);
    }

    public boolean isValidMerchant(String merchantCpr) {
        return merchant.getUser().getCprNumber().equals(merchantCpr);
    }
}
