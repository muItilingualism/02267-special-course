package org.acme.facade.event.payment;

import java.time.Duration;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeoutException;

import org.acme.model.PaymentRequest;
import org.acme.model.event.AllPaymentsRequested;
import org.acme.model.event.PaymentRequested;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

import io.smallrye.mutiny.Uni;
import io.smallrye.reactive.messaging.annotations.Broadcast;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class PaymentEmitter {

    @Inject
    @Channel("payment-requested")
    @Broadcast
    Emitter<PaymentRequested> paymentRequestedEmitter;

    private final ConcurrentHashMap<String, CompletableFuture<Void>> pendingProcessPaymentRequests = new ConcurrentHashMap<>();

    @Inject
    @Channel("all-payments-requested")
    @Broadcast
    Emitter<AllPaymentsRequested> allPaymentsRequestedEmitter;

    private final ConcurrentHashMap<String, CompletableFuture<List<PaymentRequest>>> pendingAllPaymentsRequests = new ConcurrentHashMap<>();

    public Uni<Void> emitProcessPaymentRequest(PaymentRequest request) {
        String correlationId = UUID.randomUUID().toString();
        CompletableFuture<Void> future = new CompletableFuture<>();
        pendingProcessPaymentRequests.put(correlationId, future);

        PaymentRequested event = new PaymentRequested(correlationId, request);
        paymentRequestedEmitter.send(event);

        return Uni.createFrom().completionStage(future)
                .onFailure().invoke(throwable -> {
                })
                .ifNoItem().after(Duration.ofSeconds(5)).failWith(
                        new TimeoutException("Timeout: Payment request took too long"));
    }

    public CompletableFuture<Void> removeProcessPaymentRequest(String correlationId) {
        return pendingProcessPaymentRequests.remove(correlationId);
    }
    
    public Uni<List<PaymentRequest>> emitGetAllPaymentsRequest() {
        String correlationId = UUID.randomUUID().toString();
        CompletableFuture<List<PaymentRequest>> future = new CompletableFuture<>();
        pendingAllPaymentsRequests.put(correlationId, future);

        AllPaymentsRequested event = new AllPaymentsRequested(correlationId);
        allPaymentsRequestedEmitter.send(event);

        return Uni.createFrom().completionStage(future)
                .onFailure().invoke(throwable -> {
                })
                .ifNoItem().after(Duration.ofSeconds(5)).failWith(
                        new TimeoutException("Timeout: Payment request took too long"));
    }

    public CompletableFuture<List<PaymentRequest>> removeAllPaymentsRequest(String correlationId) {
        return pendingAllPaymentsRequests.remove(correlationId);
    }
}
