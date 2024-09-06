package org.acme.facade;

import java.util.List;

import org.acme.model.AccountRegistrationRequest;
import org.acme.model.PaymentRequest;
import org.acme.model.exception.MoneyTransferException;
import org.acme.model.exception.UnknownCustomerException;
import org.acme.model.exception.UnknownMerchantException;
import org.acme.service.account.AccountService;
import org.acme.service.payment.PaymentService;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;

@ApplicationScoped
public class SimpleDTUPayFacade {

    @Inject
    PaymentService paymentService;

    @Inject
    AccountService accountService;

    @Inject
    PaymentHandler paymentHandler;

    public Uni<Void> processPayment(PaymentRequest paymentRequest)
            throws UnknownCustomerException, UnknownMerchantException, MoneyTransferException {
        return paymentHandler.emitprocessPaymentRequest(paymentRequest);
    }

    public List<PaymentRequest> getAllPayments() {
        return paymentService.getAllPayments();
    }

    public Uni<Response> processCustomerAccountRegistration(AccountRegistrationRequest account) {
        return accountService.processCustomerAccountRegistration(account)
                .onItem().transform(id -> Response.ok(id).build());
    }

    public Uni<Response> processMerchantAccountRegistration(AccountRegistrationRequest account) {
        return accountService.processMerchantAccountRegistration(account)
                .onItem().transform(id -> Response.ok(id).build());
    }
}
