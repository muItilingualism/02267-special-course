package org.acme.facade.resource;

import java.util.concurrent.TimeoutException;

import org.acme.facade.SimpleDTUPayFacade;
import org.acme.model.AccountRegistrationRequest;
import org.acme.model.exception.UnknownBankAccountIdException;
import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;

import io.smallrye.mutiny.Uni;
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
    SimpleDTUPayFacade dtuPayFacade;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> registerCustomerAccount(AccountRegistrationRequest account) {
        return dtuPayFacade.processCustomerAccountRegistration(account);
    }

    @ServerExceptionMapper
    public RestResponse<String> mapException(UnknownBankAccountIdException x) {
        return RestResponse.status(Response.Status.NOT_FOUND, "Unknown bank account id: " + x.id);
    }

    @ServerExceptionMapper
    public RestResponse<String> mapException(TimeoutException x) {
        return RestResponse.status(Response.Status.INTERNAL_SERVER_ERROR, "Timeout, the request took too long");
    }
}