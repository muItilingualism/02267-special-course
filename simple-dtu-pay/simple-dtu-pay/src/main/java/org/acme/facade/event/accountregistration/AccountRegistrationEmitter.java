package org.acme.facade.event.accountregistration;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import org.acme.model.AccountRegistrationRequest;
import org.acme.model.event.CustomerAccountRegistrationRequested;
import org.acme.model.event.MerchantAccountRegistrationRequested;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

import io.smallrye.mutiny.Uni;
import io.smallrye.reactive.messaging.annotations.Broadcast;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class AccountRegistrationEmitter {
    
    @Inject
    @Broadcast
    @Channel("customer-account-registration-requested")
    Emitter<CustomerAccountRegistrationRequested> customerAccountRegistrationEmitter;

    @Inject
    @Broadcast
    @Channel("merchant-account-registration-requested")
    Emitter<MerchantAccountRegistrationRequested> merchantAccountRegistrationEmitter;

    private final ConcurrentHashMap<String, CompletableFuture<String>> pendingAccountRegistrations = new ConcurrentHashMap<>();

    public Uni<String> emitProcessCustomerAcountRegistration(AccountRegistrationRequest account) {
        String correlationId = UUID.randomUUID().toString();
        CompletableFuture<String> future = new CompletableFuture<>();
        pendingAccountRegistrations.put(correlationId, future);
        customerAccountRegistrationEmitter.send(new CustomerAccountRegistrationRequested(correlationId, account));
        return Uni.createFrom().completionStage(future)
                .onFailure().invoke(throwable -> {
                })
                .ifNoItem().after(Duration.ofSeconds(5)).failWith(
                        new TimeoutException("Timeout: Customer registration request took too long"));
    }

    public Uni<String> emitProcessMerchantAccountRegistration(AccountRegistrationRequest account) {
        String correlationId = UUID.randomUUID().toString();
        CompletableFuture<String> future = new CompletableFuture<>();
        pendingAccountRegistrations.put(correlationId, future);
        merchantAccountRegistrationEmitter.send(new MerchantAccountRegistrationRequested(correlationId, account));
        return Uni.createFrom().completionStage(future)
                .onFailure().invoke(throwable -> {
                })
                .ifNoItem().after(Duration.ofSeconds(5)).failWith(
                        new TimeoutException("Timeout: Merchant registration request took too long"));
    }

    public CompletableFuture<String> remove(String correlationId) {
        return this.pendingAccountRegistrations.remove(correlationId);
    }
}
