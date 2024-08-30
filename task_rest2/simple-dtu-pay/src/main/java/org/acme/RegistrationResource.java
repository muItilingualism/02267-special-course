package org.acme;

import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/register")
public class RegistrationResource {

    @Inject
    PaymentService paymentService;

    @POST
    @Path("/{bankAccountId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response registerAccount(String bankAccountId) {
        if (!paymentService.isValidBankAccount(bankAccountId)) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("could not register unknown bank account")
                    .build();
        }

        paymentService.processAccountRegistration(bankAccountId);
        return Response.ok().build();
    }
}