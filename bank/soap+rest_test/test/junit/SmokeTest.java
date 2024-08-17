package junit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import dtu.ws.bank.rest.BankServiceRest;
import dtu.ws.fastmoney.BankService;
import dtu.ws.fastmoney.BankServiceException;
import dtu.ws.fastmoney.User;

public class SmokeTest {

	List<String> accounts = new ArrayList<>();
	
	@Before
	public void createAccountsSoap() throws Exception {
		BankService bank = new BankFactory().createBank();
		User user1 = new User();
		user1.setCprNumber("---cpr1234---");
		user1.setFirstName("user 1");
		user1.setLastName("user 1 last name");
		accounts.add(bank.createAccountWithBalance(user1, new BigDecimal(100)));
		User user2 = new User();
		user2.setCprNumber("---cpr5678---");
		user2.setFirstName("user 2");
		user2.setLastName("user 2 last name");
		accounts.add(bank.createAccountWithBalance(user2, new BigDecimal(1000)));
		assertTrue(true);
	}
	
	@After
	public void cleanUp() throws Exception {
		BankService bank = new BankFactory().createBank();
		for (String a : accounts ) {
			bank.retireAccount(a);
		}
	}

	@Test
	public void createAccount() throws Exception {
		BankService bank = new BankFactory().createBank();
		String a1 = accounts.get(0);
		String a2 = accounts.get(1);
		bank.transferMoneyFromTo(a1, a2, new BigDecimal(10), "from a1 to a2");
		BankServiceRest bankRest = new BankFactory().createBankRest();
		bankRest.transferMoneyFromTo(a1, a2, new BigDecimal(10), "from a1 to a2");
		assertEquals(2,bankRest.getAccount(a1).getTransactions().length);
		assertEquals(bankRest.getAccount(a1).getBalance(),bank.getAccount(a1).getBalance());
		assertEquals(2,bank.getAccount(a1).getTransactions().size());
		
	}
}
