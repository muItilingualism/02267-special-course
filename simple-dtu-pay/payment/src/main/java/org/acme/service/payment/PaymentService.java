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
import org.acme.service.bank.BankService;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;

import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.infrastructure.Infrastructure;
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
    BankAccountIdRequestHandler bankAccountIdRequestEmitter;

    @Inject
    @Broadcast
    @Channel("payment-processed")
    Emitter<PaymentProcessed> paymentProcessedEmitter;

    @Incoming("payment-requested")
    @Blocking
    public void onPaymentRequested(PaymentRequested event) {
        this.processPayment(event)
                .subscribe().with(
                        paymentProcessed -> emitPaymentProcessed(paymentProcessed),
                        failure -> emitPaymentProcessed(new PaymentFailed(event.getCorrelationId(), failure)));
    }

    public void emitPaymentProcessed(PaymentProcessed paymentProcessed) {
        paymentProcessedEmitter.send(paymentProcessed);
    }

    public Uni<PaymentProcessed> processPayment(PaymentRequested event) {
        PaymentRequest request = event.getPaymentRequest();
        paymentsRequests.add(request);

        String customerId = request.getCustomerId();
        String merchantId = request.getMerchantId();

        Uni<String> customerBankIdUni = bankAccountIdRequestEmitter.emit(customerId);
        Uni<String> merchantBankIdUni = bankAccountIdRequestEmitter.emit(merchantId);

        return Uni.combine().all().unis(customerBankIdUni, merchantBankIdUni).asTuple()
                .onItem().transformToUni(tuple -> {
                    String customerBankId = tuple.getItem1();
                    String merchantBankId = tuple.getItem2();

                    if (customerBankId == null) {
                        return Uni.createFrom()
                                .failure(new UnknownCustomerException(customerId));
                    }
                    if (merchantBankId == null) {
                        return Uni.createFrom()
                                .failure(new UnknownMerchantException(merchantId));
                    }

                    Payment payment = new Payment(request.getAmount(),
                            customerBankId,
                            merchantBankId,
                            String.format("TRANSACTION OF %d BY %s TO %s",
                                    request.getAmount(), customerId, merchantId));

                    return Uni.createFrom().item(payment)
                        .runSubscriptionOn(Infrastructure.getDefaultWorkerPool())
                        .onItem().invoke(() -> bankService.transferMoney(payment))
                        .replaceWith((PaymentProcessed) new PaymentCompleted(event.getCorrelationId()));
                })
                .onFailure().recoverWithItem(e -> new PaymentFailed(event.getCorrelationId(), e));
    }

    @Incoming("all-payments-requested")
    @Broadcast
    @Outgoing("all-payments-assembled")
    @Blocking
    public AllPaymentsAssembled getAllPayments(AllPaymentsRequested event) {
        return new AllPaymentsAssembled(event.getCorrelationId(), new ArrayList<>(paymentsRequests));
    }
}
