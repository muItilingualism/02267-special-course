package org.acme;

import java.util.ArrayList;
import java.util.List;

import org.acme.model.Payment;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/payment")
public class PaymentResource {

    List<Payment> list = new ArrayList<>();
    String customerId = "cid1";
    String merchantId = "mid1";
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response payment(Payment payment) {
        list.add(payment);
        return Response.ok().build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response list() {
        return Response.ok(this.list).build();
    }
}
