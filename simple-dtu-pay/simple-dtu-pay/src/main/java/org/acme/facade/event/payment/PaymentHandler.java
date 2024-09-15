package org.acme.facade.event.payment;

import org.acme.model.event.AllPaymentsAssembled;
import org.acme.model.event.PaymentCompleted;
import org.acme.model.event.PaymentFailed;
import org.eclipse.microprofile.reactive.messaging.Incoming;

import io.vertx.core.json.JsonObject;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class PaymentHandler {

    @Inject
    PaymentProcessor processor;

    @Incoming("payment-completed")
    public void handlePaymentCompleted(JsonObject jsonEvent) {
        PaymentCompleted event = jsonEvent.mapTo(PaymentCompleted.class);
        this.processor.processPaymentCompleted(event);
    }

    @Incoming("payment-failed")
    public void handlePaymentProcessed(JsonObject jsonEvent) {
        PaymentFailed event = jsonEvent.mapTo(PaymentFailed.class);
        this.processor.processPaymentFailed(event);
    }

    @Incoming("all-payments-assembled")
    public void handleAllPaymentsAssembled(JsonObject jsonEvent) {
        AllPaymentsAssembled event = jsonEvent.mapTo(AllPaymentsAssembled.class);
        this.processor.processAllPaymentsAssembled(event);
    }
}
