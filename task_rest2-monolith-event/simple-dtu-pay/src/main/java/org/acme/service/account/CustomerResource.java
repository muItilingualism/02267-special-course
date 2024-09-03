package org.acme.service.account;

import org.acme.model.AccountRegistrationRequest;
import org.acme.model.exception.UnknownBankAccountIdException;
import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/customers")
public class CustomerResource {

    @Inject
    AccountService accountService;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response registerCustomerAccount(AccountRegistrationRequest account) {
        String id = accountService.processCustomerAccountRegistration(account);
        return Response.ok(id).build();
    }

    @ServerExceptionMapper
    public RestResponse<String> mapException(UnknownBankAccountIdException x) {
        System.out.println("Unknown bank account id: " + x.id);
        return RestResponse.status(Response.Status.NOT_FOUND, "Unknown bank account id: " + x.id);
    }
}