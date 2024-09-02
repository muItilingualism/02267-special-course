package org.acme;

import org.acme.model.PaymentRequest;
import org.acme.model.exception.MoneyTransferException;
import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/payments")
public class PaymentResource {

    @Inject
    PaymentService paymentService;

    @Inject
    AccountService accountService;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response payment(PaymentRequest paymentRequest) {
        if (!accountService.isValidBankAccount(paymentRequest.getCustomerId())) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("customer id is unknown")
                    .build();
        }

        if (!accountService.isValidBankAccount(paymentRequest.getMerchantId())) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("merchant id is unknown")
                    .build();
        }

        paymentService.processPayment(paymentRequest);
        return Response.ok().build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response list() {
        return Response.ok(paymentService.getAllPayments()).build();
    }

    //TODO handle specific errors instead of relying on catch-all.
    @ServerExceptionMapper
    public RestResponse<String> mapException(MoneyTransferException x) {
        return RestResponse.status(Response.Status.INTERNAL_SERVER_ERROR, "Failed to transfer money with error " + x.status + " and issue " + x.message);
    }
}