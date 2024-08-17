package junit;

import static org.junit.Assert.assertNull;

import java.math.BigDecimal;

import org.junit.Test;

import dtu.ws.fastmoney.Bank;
import dtu.ws.fastmoney.BankServiceException;
import dtu.ws.fastmoney.User;

public class TestForNull {

	@Test(expected = BankServiceException.class)
	public void testForNullGetAccount() throws BankServiceException {
		Bank bank = new Bank();
		bank.getAccount(null);
	}

	@Test(expected = BankServiceException.class)
	public void testForNullGetAccountCpr() throws BankServiceException {
		Bank bank = new Bank();
		bank.getAccountByCprNumber(null);
	}

	@Test(expected = BankServiceException.class)
	public void testForNullUserGetCreateAccount1() throws BankServiceException {
		Bank bank = new Bank();
		bank.createAccountWithBalance(null, new BigDecimal(10));
	}

	@Test(expected = BankServiceException.class)
	public void testForNullBalanceGetCreateAccount1() throws BankServiceException {
		Bank bank = new Bank();
		User user = new User();
		user.setCprNumber(null);
		user.setFirstName("first");
		user.setLastName("last");
		bank.createAccountWithBalance(user, null);
	}

	@Test(expected = BankServiceException.class)
	public void testForNullGetCreateAccount2() throws BankServiceException {
		Bank bank = new Bank();
		User user = new User();
		user.setCprNumber(null);
		user.setFirstName("first");
		user.setLastName("last");
		bank.createAccountWithBalance(user, new BigDecimal(10));
	}

	@Test(expected = BankServiceException.class)
	public void testForNullGetCreateAccount3() throws BankServiceException {
		Bank bank = new Bank();
		User user = new User();
		user.setCprNumber("cpr-some-wired");
		user.setFirstName(null);
		user.setLastName("last");
		bank.createAccountWithBalance(user, new BigDecimal(10));
	}

	@Test(expected = BankServiceException.class)
	public void testForNullGetCreateAccount4() throws BankServiceException {
		Bank bank = new Bank();
		User user = new User();
		user.setCprNumber("cpr-some-wired");
		user.setFirstName("first");
		user.setLastName(null);
		bank.createAccountWithBalance(user, new BigDecimal(10));
	}

	@Test(expected = BankServiceException.class)
	public void testForNulltransferMoney1() throws BankServiceException {
		Bank bank = new Bank();
		bank.transferMoneyFromTo(null, "-account---2", new BigDecimal(1000), "description");
		;
	}

	@Test(expected = BankServiceException.class)
	public void testForNulltransferMoney2() throws BankServiceException {
		Bank bank = new Bank();
		bank.transferMoneyFromTo("-account---1", null, new BigDecimal(1000), "description");
		;
	}

	@Test(expected = BankServiceException.class)
	public void testForNulltransferMoney3() throws BankServiceException {
		Bank bank = new Bank();
		bank.transferMoneyFromTo("-account---1", "-account---2", null, "description");
		;
	}

	@Test(expected = BankServiceException.class)
	public void testForNulltransferMoney4() throws BankServiceException {
		Bank bank = new Bank();
		bank.transferMoneyFromTo("-account---1", "-account---2", new BigDecimal(1000), null);
		;
	}

	@Test
	public void raceConditionOnCreation() throws InterruptedException {
		class ValueHolder {
			Object value = 0;
		}
		Bank bank = new Bank();
		ValueHolder exception = new ValueHolder();
		exception.value = null;
		for (final ValueHolder vh = new ValueHolder(); (int) vh.value <= 300; vh.value = (int) vh.value + 1) {
			final int cpr = (int)vh.value;
			final int balance = (int)vh.value;
			new Thread(() -> {
				User user = new User();
				user.setCprNumber("" + cpr);
				user.setFirstName("First");
				user.setLastName("Last");
				try {
					bank.createAccountWithBalance(user, new BigDecimal(balance));
				} catch (Throwable e) {
					e.printStackTrace();
					exception.value = e;
				}
			}).start();
		}
		Thread.sleep(1000);
		assertNull(exception.value);

	}
}
