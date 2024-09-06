package org.acme.facade;

import java.util.List;

import org.acme.model.AccountRegistrationRequest;
import org.acme.model.PaymentRequest;
import org.acme.model.exception.MoneyTransferException;
import org.acme.model.exception.UnknownCustomerException;
import org.acme.model.exception.UnknownMerchantException;
import org.acme.service.account.AccountService;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;

@ApplicationScoped
public class SimpleDTUPayFacade {

    @Inject
    AccountService accountService;

    @Inject
    PaymentEventHandler paymentHandler;

    public Uni<Void> processPayment(PaymentRequest paymentRequest)
            throws UnknownCustomerException, UnknownMerchantException, MoneyTransferException {
        return paymentHandler.emitprocessPaymentRequest(paymentRequest);
    }

    public Uni<List<PaymentRequest>> getAllPayments() {
        return paymentHandler.emitGetAllPaymentsRequest();
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
