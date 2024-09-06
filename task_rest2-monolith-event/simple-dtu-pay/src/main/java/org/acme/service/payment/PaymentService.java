package org.acme.service.payment;

import java.util.ArrayList;
import java.util.List;

import org.acme.model.PaymentRequest;
import org.acme.model.bank.Payment;
import org.acme.model.event.PaymentCompleted;
import org.acme.model.event.PaymentFailed;
import org.acme.model.event.PaymentProcessed;
import org.acme.model.event.PaymentRequested;
import org.acme.model.exception.UnknownCustomerException;
import org.acme.model.exception.UnknownMerchantException;
import org.acme.service.account.AccountService;
import org.acme.service.bank.BankService;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Incoming;

import io.smallrye.reactive.messaging.annotations.Blocking;
import io.smallrye.reactive.messaging.annotations.Broadcast;
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

    @Inject
    @Channel("payment-processed")
    @Broadcast
    Emitter<PaymentProcessed> paymentProcessedEmitter;

    @Incoming("payment-requested")
    @Blocking
    public void processPayment(PaymentRequested event) {
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

            paymentProcessedEmitter.send(new PaymentCompleted(event.getCorrelationId()));
        } catch (Exception e) {
            paymentProcessedEmitter.send(new PaymentFailed(event.getCorrelationId(), e));
        }
    }

    public List<PaymentRequest> getAllPayments() {
        return new ArrayList<>(paymentsRequests);
    }
}
