package junit;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Test;

import dtu.ws.bank.rest.BankServiceRest;
import dtu.ws.bank.rest.UserRest;
import dtu.ws.fastmoney.BankService;
import dtu.ws.fastmoney.BankServiceException;
import dtu.ws.fastmoney.User;

public class CreateAccountTest {

	List<String> accounts = new ArrayList<>();
	
	@Test
	public void createAccountSoapAccessRest() throws Exception {
		BankService soapBank = new BankFactory().createBank();
		BankServiceRest restBank = new BankFactory().createBankRest();
		User user = new User();
		user.setCprNumber("---cpr1234---");
		user.setFirstName("user 1");
		user.setLastName("user 1 last name");
		String account = soapBank.createAccountWithBalance(user, new BigDecimal(1000));
		accounts.add(account);
		assertEquals(user.getCprNumber(),restBank.getAccount(account).getUser().getCprNumber());
	}

	@Test
	public void createAccountRestAccessSoap() throws Exception {
		BankService soapBank = new BankFactory().createBank();
		BankServiceRest restBank = new BankFactory().createBankRest();
		UserRest user = new UserRest();
		user.setCprNumber("---cpr5678---");
		user.setFirstName("user 2");
		user.setLastName("user 2 last name");
		String account = restBank.createAccountWithBalance(user, new BigDecimal(1000));
		accounts.add(account);
		assertEquals(user.getCprNumber(),soapBank.getAccount(account).getUser().getCprNumber());
	}

	@After
	public void cleanUp() throws Exception {
		BankService bank = new BankFactory().createBank();
		for (String a : accounts ) {
			bank.retireAccount(a);
		}
	}
}
