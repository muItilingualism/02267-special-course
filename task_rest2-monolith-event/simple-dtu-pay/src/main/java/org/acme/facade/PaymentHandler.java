package org.acme.facade;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeoutException;

import org.acme.model.PaymentRequest;
import org.acme.model.event.PaymentCompleted;
import org.acme.model.event.PaymentFailed;
import org.acme.model.event.PaymentProcessed;
import org.acme.model.event.PaymentRequested;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Incoming;

import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import io.smallrye.reactive.messaging.annotations.Broadcast;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class PaymentHandler {

    @Inject
    @Channel("payment-requested")
    @Broadcast
    Emitter<PaymentRequested> emitter;

    private final ConcurrentHashMap<String, CompletableFuture<Void>> pendingProcessPaymentRequests = new ConcurrentHashMap<>();

    public Uni<Void> emitprocessPaymentRequest(PaymentRequest request) {
        String correlationId = UUID.randomUUID().toString();
        CompletableFuture<Void> future = new CompletableFuture<>();
        pendingProcessPaymentRequests.put(correlationId, future);

        PaymentRequested event = new PaymentRequested(correlationId, request);
        emitter.send(event);
        
        return Uni.createFrom().completionStage(future)
                .onFailure().invoke(throwable -> {})
                .ifNoItem().after(Duration.ofSeconds(5)).failWith(
                        new TimeoutException("Timeout: Payment request took too long"));
    }

    public CompletableFuture<Void> removeProcessPaymentRequest(String correlationId) {
        return pendingProcessPaymentRequests.remove(correlationId);
    }

    @Incoming("payment-processed")
    public void handle(PaymentProcessed event) {
        CompletableFuture<Void> future = removeProcessPaymentRequest(event.getCorrelationId());
        if (future == null) {
            Log.warn("Received unknown or already removed correlationId: " + event.getCorrelationId());
        }

        if (event instanceof PaymentCompleted) {
            future.complete(null);
        } else if (event instanceof PaymentFailed) {
            future.completeExceptionally(((PaymentFailed) event).getCause());
        } else {
            Log.warnf("Received unhandled PaymentProcess event %s with correlationId: %s",event.getClass(), event.getCorrelationId());
            future.completeExceptionally(new IllegalStateException("Received unhandled PaymentProcess event"));
        }
    }
}
