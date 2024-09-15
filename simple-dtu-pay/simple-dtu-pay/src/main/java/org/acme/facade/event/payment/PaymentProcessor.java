package org.acme.facade.event.payment;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.acme.model.PaymentRequest;
import org.acme.model.event.AllPaymentsAssembled;
import org.acme.model.event.PaymentCompleted;
import org.acme.model.event.PaymentFailed;
import org.acme.model.exception.UnknownCustomerException;
import org.acme.model.exception.UnknownMerchantException;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class PaymentProcessor {

    @Inject
    PaymentEmitter emitter;

    public void processAllPaymentsAssembled(AllPaymentsAssembled event) {
        CompletableFuture<List<PaymentRequest>> future = this.emitter
                .removeAllPaymentsRequest(event.getCorrelationId());
        if (future == null) {
            Log.warn("Received unknown or already removed all-payments-assembled correlationId: "
                    + event.getCorrelationId());
        }
        future.complete(event.getAllPayments());
    }

    public void processPaymentCompleted(PaymentCompleted event) {
        CompletableFuture<Void> future = this.emitter.removeProcessPaymentRequest(event.getCorrelationId());
        if (future == null) {
            Log.warn("Received unknown or already removed payment-processed correlationId: "
                    + event.getCorrelationId());
            return;
        }

        future.complete(null);
    }

    public void processPaymentFailed(PaymentFailed event) {
        CompletableFuture<Void> future = this.emitter.removeProcessPaymentRequest(event.getCorrelationId());
        if (future == null) {
            Log.warn("Received unknown or already removed payment-processed correlationId: "
                    + event.getCorrelationId());
            return;
        }

        String type = event.getExceptionType();
        Throwable cause = event.getCause();

        if (type.equals("UnknownCustomerException")) {
            future.completeExceptionally(new UnknownCustomerException());
        } else if (type.equals("UnknownMerchantException")) {
            future.completeExceptionally(new UnknownMerchantException());
        } else {
            future.completeExceptionally(cause);
        }
    }

}
