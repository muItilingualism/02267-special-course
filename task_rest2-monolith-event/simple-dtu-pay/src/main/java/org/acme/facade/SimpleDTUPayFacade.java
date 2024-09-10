package org.acme.facade;

import java.util.List;

import org.acme.model.AccountRegistrationRequest;
import org.acme.model.PaymentRequest;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class SimpleDTUPayFacade {

    @Inject
    AccountEventHandler accountHandler;

    @Inject
    PaymentEventHandler paymentHandler;

    public Uni<Void> processPayment(PaymentRequest paymentRequest) {
        return paymentHandler.emitProcessPaymentRequest(paymentRequest);
    }

    public Uni<List<PaymentRequest>> getAllPayments() {
        return paymentHandler.emitGetAllPaymentsRequest();
    }

    public Uni<String> processCustomerAccountRegistration(AccountRegistrationRequest account) {
        return accountHandler.emitProcessCustomerAcountRegistration(account);
        
    }

    public Uni<String> processMerchantAccountRegistration(AccountRegistrationRequest account) {
        return accountHandler.emitProcessMerchantAccountRegistration(account);
    }
}
