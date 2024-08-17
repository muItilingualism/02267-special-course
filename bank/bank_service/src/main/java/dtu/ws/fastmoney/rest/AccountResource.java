package dtu.ws.fastmoney.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import dtu.ws.fastmoney.AccountInfo;
import dtu.ws.fastmoney.Bank;
import dtu.ws.fastmoney.BankServiceException;
import dtu.ws.fastmoney.BankSingleton;
import dtu.ws.fastmoney.persistency.InMemoryRepository;
import dtu.ws.fastmoney.rest.data.CreateAccountData;
import dtu.ws.fastmoney.rest.data.ErrorType;

@Path("/accounts")

@Produces({ "text/plain", "application/json" })
public class AccountResource {

	static Bank bank = new BankSingleton().getInstance();
	
	public AccountResource( ) {
		bank.setRepository(InMemoryRepository.getInstance());
	}

	@GET
	@Path("/hidden")
	@Produces({"application/json"})
	public AccountInfo[] getAccounts() {
		return bank.getAccounts();
	}
	
	@GET
	@Path("/{id}")
	@Produces({ "application/json"})
	public Response getAccount(@PathParam("id") String accountId) throws BankServiceException {
		try {
			return Response.ok(bank.getAccount(accountId)).build();
		} catch (BankServiceException e) {
			return Response.status(Response.Status.NOT_FOUND).type(MediaType.APPLICATION_JSON).entity(new ErrorType(e.getErrorMessage()))
					.build();
		}
	}
	
	@GET
	@Path("/cpr/{cpr}")
	@Produces({ "application/json"})
	public Response getAccountByCprNumber(@PathParam("cpr") String cpr) throws BankServiceException {
		try {
			return Response.ok(bank.getAccountByCprNumber(cpr)).build();
		} catch (BankServiceException e) {
			return Response.status(Response.Status.NOT_FOUND).type(MediaType.APPLICATION_JSON).entity(new ErrorType(e.getErrorMessage()))
					.build();
		}
	}

	@POST
	@Produces({ "text/plain" })
	@Consumes({ "application/json" })
	public Response createAccount(CreateAccountData data) {
		try {
			String id = bank.createAccountWithBalance(data.user, data.balance);
			return Response.ok(id).build();
		} catch (BankServiceException e) {
			return Response.status(Response.Status.BAD_REQUEST).type(MediaType.TEXT_PLAIN).entity(e.getErrorMessage())
					.build();
		}
	}

	@Path("/{id}")
	@DELETE
	@Produces({ "text/plain" })
	public Response retireAccount(@PathParam("id") String accountId) {
		try {
			bank.retireAccount(accountId);
			return Response.status(Response.Status.NO_CONTENT).build();
		} catch (BankServiceException e) {
			return Response.status(Response.Status.NOT_FOUND).type(MediaType.TEXT_PLAIN)
					.entity(e.getErrorMessage()).build();
		}
	}

}
