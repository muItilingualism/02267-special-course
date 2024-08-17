package junit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Test;

import cucumber.utilities.BankFactory;
import dtu.ws.fastmoney.BankService;
import dtu.ws.fastmoney.BankServiceException_Exception;
import dtu.ws.fastmoney.User;

public class SmokeTest {

	BankService bank = new BankFactory().createBank();
	List<String> accounts = new ArrayList<>();

//	@Test
//	public void getAccounts() throws RemoteException {
//		System.out.println("Before getAccounts");
//		List<AccountInfo> as = bank.getAccounts();
//		System.out.println("bank.getAccounts() = " + as);
//		assertNotNull(as);
//		System.out.println("Finished getAccounts");
//	}

	@Test
	public void createAccount() throws RemoteException {
		User user = new User();
		user.setCprNumber("191052-3356");
		user.setFirstName("Hubert");
		user.setLastName("Baumeister");
		try {
			String id = bank.createAccountWithBalance(user, new BigDecimal(10));
			assertNotNull(id);
			bank.retireAccount(id); // Clean up
		} catch (BankServiceException_Exception e) {
			assertEquals("Account already exists", e.getFaultInfo().getMessage());
		}
	}

//	@Test
	public void createALotOfAccounts() throws BankServiceException_Exception, RemoteException, InterruptedException {
		for (int i = 0; i < 100; i++) {
			final int ii = i;
			User user = new User();
			user.setCprNumber("12345666" + 2 * ii);
			user.setFirstName("Hubert");
			user.setLastName("Baumeister");
			String a1;
			a1 = bank.createAccountWithBalance(user, new BigDecimal(100));
			accounts.add(a1);
			user = new User();
			user.setCprNumber("7896543" + 2 * ii);
			user.setFirstName("Hubert");
			user.setLastName("Baumeister");
			String a2 = bank.createAccountWithBalance(user, new BigDecimal(100));
			accounts.add(a2);
			bank.transferMoneyFromTo(a1, a2, new BigDecimal(10), "test");
		}
	}

	@After
	public void cleanup() throws BankServiceException_Exception, RemoteException {
		System.out.println("start cleanup");
		for (String a : accounts) {
			bank.retireAccount(a);
		}
		System.out.println("end cleanup");
	}
}
