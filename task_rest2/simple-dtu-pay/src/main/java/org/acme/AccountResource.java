package org.acme;

import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/accounts")
public class AccountResource {

    @Inject
    AccountService accountService;

    @POST
    @Path("/{bankAccountId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response registerAccount(String bankAccountId) {
        if (!accountService.isValidBankAccount(bankAccountId)) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("could not register unknown bank account")
                    .build();
        }

        accountService.processAccountRegistration(bankAccountId);
        return Response.ok().build();
    }
}