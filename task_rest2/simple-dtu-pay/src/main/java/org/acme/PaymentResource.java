package org.acme;

import org.acme.model.PaymentRequest;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/payment")
public class PaymentResource {

    @Inject
    PaymentService paymentService;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response payment(PaymentRequest paymentRequest) {
        if (!paymentService.isValidCustomer(paymentRequest.getCustomerId())) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("customer id is unknown")
                    .build();
        }

        if (!paymentService.isValidMerchant(paymentRequest.getMerchantId())) {
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
}