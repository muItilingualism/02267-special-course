package org.acme.service.payment;

import java.util.ArrayList;
import java.util.List;

import org.acme.model.PaymentRequest;
import org.acme.model.bank.Payment;
import org.acme.model.event.AllPaymentsAssembled;
import org.acme.model.event.AllPaymentsRequested;
import org.acme.model.event.PaymentCompleted;
import org.acme.model.event.PaymentFailed;
import org.acme.model.event.PaymentProcessed;
import org.acme.model.event.PaymentRequested;
import org.acme.model.exception.UnknownCustomerException;
import org.acme.model.exception.UnknownMerchantException;
import org.acme.service.account.AccountService;
import org.acme.service.bank.BankService;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;

import io.smallrye.reactive.messaging.annotations.Blocking;
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

    @Incoming("payment-requested")
    @Outgoing("payment-processed")
    @Blocking
    public PaymentProcessed processPayment(PaymentRequested event) {
        try {
            PaymentRequest request = event.getPaymentRequest();
            paymentsRequests.add(request);

            String customerId = request.getCustomerId();
            String merchantId = request.getMerchantId();

            String customerBankId = accountService.getAccountBankId(customerId)
                    .orElseThrow(() -> new UnknownCustomerException(customerId));
            String mechantBankId = accountService.getAccountBankId(merchantId)
                    .orElseThrow(() -> new UnknownMerchantException(merchantId));
            
            Payment payment = new Payment(request.getAmount(),
                    customerBankId,
                    mechantBankId,
                    String.format("TRANSACTION OF %d BY %s TO %s",
                            request.getAmount(), customerId, merchantId));
            bankService.transferMoney(payment);

            return new PaymentCompleted(event.getCorrelationId());
        } catch (Exception e) {
            return new PaymentFailed(event.getCorrelationId(), e);
        }
    }

    @Incoming("all-payments-requested")
    @Broadcast
    @Outgoing("all-payments-assembled")
    @Blocking
    public AllPaymentsAssembled getAllPayments(AllPaymentsRequested event) {
        return new AllPaymentsAssembled(event.getCorrelationId(), new ArrayList<>(paymentsRequests));
    }
}
