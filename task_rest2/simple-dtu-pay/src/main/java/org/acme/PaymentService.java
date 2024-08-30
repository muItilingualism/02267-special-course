package org.acme;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.acme.model.Account;
import org.acme.model.Payment;
import org.acme.model.PaymentRequest;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class PaymentService {
    private List<PaymentRequest> paymentsRequests = new ArrayList<>();
    private List<Account> registeredAccounts = new ArrayList<>();

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

    //TODO remove duplicate
    public boolean isValidCustomer(String id) {
        return bankService.getAccount(id).isPresent();
    }

    public boolean isValidMerchant(String id) {
        return bankService.getAccount(id).isPresent();
    }

    public void processAccountRegistration(String bankAccountId) {
        Optional<Account> bankAccount = bankService.getAccount(bankAccountId);

        if (bankAccount.isPresent()) {
            registerAccount(bankAccount.get());
        } else {
            throw new Error("Failed to get bank account by id from bank.");
        }
    }

    private void registerAccount(Account bankAccount) {
        registeredAccounts.add(bankAccount);
    }
}
