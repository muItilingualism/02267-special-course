package dtu.ws.fastmoney.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import dtu.ws.fastmoney.BankServiceException;
import dtu.ws.fastmoney.rest.data.PaymentData;

@Path("/payments")

public class PaymentResource {

	@POST
	@Produces({ "text/plain" })
	@Consumes({ "application/json" })
	public Response transferMoney(PaymentData pd) {
		try {
			AccountResource.bank.transferMoneyFromTo(pd.debtor, pd.creditor, pd.amount, pd.description);
			return Response.noContent().build();
		} catch (BankServiceException e) {
			return Response.status(Response.Status.BAD_REQUEST).type(MediaType.TEXT_PLAIN)
					.entity(e.getErrorMessage()).build();
		}
	}

}
