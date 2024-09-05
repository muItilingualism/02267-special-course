package org.acme.service.payment;

import java.util.ArrayList;
import java.util.List;

import org.acme.model.PaymentRequest;
import org.acme.model.bank.Payment;
import org.acme.model.exception.MoneyTransferException;
import org.acme.model.exception.UnknownCustomerException;
import org.acme.model.exception.UnknownMerchantException;
import org.acme.service.account.AccountService;
import org.acme.service.bank.BankService;
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

    @Inject
    AccountService accountService;

    public void processPayment(PaymentRequest paymentRequest)
            throws UnknownCustomerException, UnknownMerchantException, MoneyTransferException {
        paymentsRequests.add(paymentRequest);

        String customerId = paymentRequest.getCustomerId();
        String merchantId = paymentRequest.getMerchantId();

        String customerBankId = accountService.getAccountBankId(customerId)
                .orElseThrow(() -> new UnknownCustomerException(customerId));
        String mechantBankId = accountService.getAccountBankId(merchantId)
                .orElseThrow(() -> new UnknownMerchantException(merchantId));

        Payment payment = new Payment(paymentRequest.getAmount(),
                customerBankId,
                mechantBankId,
                String.format("TRANSACTION OF %d BY %s TO %s",
                        paymentRequest.getAmount(), customerId, merchantId));
        bankService.transferMoney(payment);
    }

    public List<PaymentRequest> getAllPayments() {
        return new ArrayList<>(paymentsRequests);
    }
}
