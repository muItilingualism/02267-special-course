package org.acme.facade;

import java.util.List;

import org.acme.facade.event.accountregistration.AccountRegistrationHandler;
import org.acme.facade.event.payment.PaymentEmitter;
import org.acme.model.AccountRegistrationRequest;
import org.acme.model.PaymentRequest;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class SimpleDTUPayFacade {

    @Inject
    AccountRegistrationHandler accountHandler;

    @Inject
    PaymentEmitter paymentEmitter;

    public Uni<Void> processPayment(PaymentRequest paymentRequest) {
        return paymentEmitter.emitProcessPaymentRequest(paymentRequest);
    }

    public Uni<List<PaymentRequest>> getAllPayments() {
        return paymentEmitter.emitGetAllPaymentsRequest();
    }

    public Uni<String> processCustomerAccountRegistration(AccountRegistrationRequest account) {
        return accountHandler.emitProcessCustomerAcountRegistration(account);
        
    }

    public Uni<String> processMerchantAccountRegistration(AccountRegistrationRequest account) {
        return accountHandler.emitProcessMerchantAccountRegistration(account);
    }
}
