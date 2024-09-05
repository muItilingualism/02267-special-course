package org.acme.service.payment;

import org.acme.facade.SimpleDTUPayFacade;
import org.acme.model.PaymentRequest;
import org.acme.model.exception.MoneyTransferException;
import org.acme.model.exception.UnknownCustomerException;
import org.acme.model.exception.UnknownMerchantException;
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
    SimpleDTUPayFacade dtuPayFacade;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response payment(PaymentRequest paymentRequest)
            throws UnknownCustomerException, UnknownMerchantException, MoneyTransferException {
        dtuPayFacade.processPayment(paymentRequest);
        return Response.ok().build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response list() {
        return Response.ok(dtuPayFacade.getAllPayments()).build();
    }

    @ServerExceptionMapper
    public RestResponse<String> mapException(MoneyTransferException x) {
        return RestResponse.status(Response.Status.INTERNAL_SERVER_ERROR,
                "Failed to transfer money with error " + x.status + " and issue " + x.message);
    }

    @ServerExceptionMapper
    public RestResponse<String> mapException(UnknownCustomerException x) {
        return RestResponse.status(Response.Status.BAD_REQUEST, "customer id is unknown");
    }

    @ServerExceptionMapper
    public RestResponse<String> mapException(UnknownMerchantException x) {
        return RestResponse.status(Response.Status.BAD_REQUEST, "merchant id is unknown");
    }
}