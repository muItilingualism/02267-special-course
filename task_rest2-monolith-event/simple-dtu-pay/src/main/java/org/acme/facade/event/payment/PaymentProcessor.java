package org.acme.facade.event.payment;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.acme.model.PaymentRequest;
import org.acme.model.event.AllPaymentsAssembled;
import org.acme.model.event.PaymentCompleted;
import org.acme.model.event.PaymentFailed;
import org.acme.model.event.PaymentProcessed;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class PaymentProcessor {

    @Inject
    PaymentEmitter emitter;

    public void processPaymentProcessed(PaymentProcessed event) {
        CompletableFuture<Void> future = this.emitter.removeProcessPaymentRequest(event.getCorrelationId());
        if (future == null) {
            Log.warn("Received unknown or already removed payment-processed correlationId: "
                    + event.getCorrelationId());
            return;
        }

        if (event instanceof PaymentCompleted) {
            future.complete(null);
        } else if (event instanceof PaymentFailed) {
            future.completeExceptionally(((PaymentFailed) event).getCause());
        } else {
            Log.warnf("Received unhandled PaymentProcess event %s with correlationId: %s", event.getClass(),
                    event.getCorrelationId());
            future.completeExceptionally(new IllegalStateException("Received unhandled PaymentProcess event"));
        }
    }

    public void processAllPaymentsAssembled(AllPaymentsAssembled event) {
        CompletableFuture<List<PaymentRequest>> future = this.emitter
                .removeAllPaymentsRequest(event.getCorrelationId());
        if (future == null) {
            Log.warn("Received unknown or already removed all-payments-assembled correlationId: "
                    + event.getCorrelationId());
        }
        future.complete(event.getAllPayments());
    }
    
}
