package org.acme;

import org.acme.model.Payment;

import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

@Path("/payment")
public class PaymentResource {

    String customerId = "cid1";
    String merchantId = "mid1";
    
    @POST
    public Response payment(Payment payment) {
        return Response.ok().build();
    }
}
