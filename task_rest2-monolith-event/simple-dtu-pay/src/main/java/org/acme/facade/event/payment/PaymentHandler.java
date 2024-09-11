package org.acme.facade.event.payment;

import org.acme.model.event.AllPaymentsAssembled;
import org.acme.model.event.PaymentProcessed;
import org.eclipse.microprofile.reactive.messaging.Incoming;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class PaymentHandler {

    @Inject
    PaymentProcessor processor;

    @Incoming("payment-processed")
    public void handlePaymentProcessed(PaymentProcessed event) {
        this.processor.processPaymentProcessed(event);
    }

    @Incoming("all-payments-assembled")
    public void handleAllPaymentsAssembled(AllPaymentsAssembled event) {
        this.processor.processAllPaymentsAssembled(event);
    }
}
