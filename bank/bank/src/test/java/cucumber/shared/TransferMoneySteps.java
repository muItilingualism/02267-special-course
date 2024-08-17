package cucumber.shared;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import dtu.ws.fastmoney.Account;
import dtu.ws.fastmoney.Bank;
import dtu.ws.fastmoney.BankServiceException;
import dtu.ws.fastmoney.Transaction;
import dtu.ws.fastmoney.User;

public class TransferMoneySteps {

	Helper helper;

	List<String> accounts = new ArrayList<>();
	User user1;
	User user2;
	Bank bank;

	public TransferMoneySteps(Helper helper) {
		this.helper = helper;
		this.bank = helper.getBank();
	}

	@Given("^account \"([^\"]*)\", \"([^\"]*)\", \"([^\"]*)\" with start balance (\\d+)$")
	public void accountWithStartBalance(String arg1, String arg2, String arg3, int arg4) throws Throwable {
		accounts.add(helper.createAccount(arg1, arg2, arg3, arg4));
	}

	@When("^I transfer \\+?(-?\\d+) from the first account to the second account with description \"([^\"]*)\"$")
	public void iTransferFromTheFirstAccountToTheSecondAccount(int arg1, String arg2) throws Throwable {
		try {
			bank.transferMoneyFromTo(accounts.get(0), accounts.get(1), new BigDecimal(arg1), arg2);
		} catch (BankServiceException e) {
			helper.errorMessage = e.getErrorMessage();
		}
	}

	@Then("^the balance of the first account is (\\d+)$")
	public void theBalanceOfTheFirstAccountIs(int arg1) throws Throwable {
		Account account = bank.getAccount(accounts.get(0));
		assertEquals(new BigDecimal(arg1), account.getBalance());
	}

	@Then("^the balance of the second account is (\\d+)$")
	public void theBalanceOfTheSecondAccountIs(int arg1) throws Throwable {
		Account account = bank.getAccount(accounts.get(1));
		assertEquals(new BigDecimal(arg1), account.getBalance());
	}

	@Given("^a non existent account id \"([^\"]*)\"$")
	public void aNonExistentAccountId(String arg1) throws Throwable {
		accounts.add(arg1);
	}
	
	@Then("^account (\\d+) has transaction (\\d+) of amount (\\d+) with debtor account (\\d+) and creditor account (\\d+) and balance (\\d+) and description \"([^\"]*)\" and current Date/Time$")
	public void accountHasTransactionOfAmountWithDebtorAccountAndCreditorAccountAndBalance(int arg1, int arg2, int arg3, int arg4, int arg5, int arg6, String arg7) throws Throwable {
		Account account = bank.getAccount(accounts.get(arg1 - 1));
		Transaction[] transactions = account.getTransactions();
		Transaction t = transactions[arg2-1];
		assertEquals(new BigDecimal(arg3),t.getAmount());
		assertEquals(accounts.get(arg4-1),t.getDebtor());
		assertEquals(accounts.get(arg5-1),t.getCreditor());
		assertEquals(new BigDecimal(arg6),t.getBalance());
		assertEquals(arg7,t.getDescription());
		Date time = t.getTime();
		assertNotNull(time);
		Date now = new Date();
		assertEquals(now.getTime()/10000,time.getTime()/10000); // Equal by the 10 second
	}

	@Then("^account (\\d+) has transaction (\\d+) of amount (\\d+) with creditor account (\\d+) and balance (\\d+) and description \"([^\"]*)\" and current Date/Time$")
	public void accountHasTransactionOfAmountWithCreditorAccountAndBalanceAndDescriptionAndCurrentDateTime(int arg1, int arg2, int arg3, int arg4, int arg5, String arg6) throws Throwable {
		Account account = bank.getAccount(accounts.get(arg1 - 1));
		Transaction[] transactions = account.getTransactions();
		Transaction t = transactions[arg2-1];
		assertEquals(new BigDecimal(arg3),t.getAmount());
		assertEquals(accounts.get(arg4-1),t.getCreditor());
		assertEquals("this account",t.getDebtor());
		assertEquals(new BigDecimal(arg5),t.getBalance());
		assertEquals(arg6,t.getDescription());
		Date time = t.getTime();
		assertNotNull(time);
		Date now = new Date();
		assertEquals(now.getTime()/10000,time.getTime()/10000); // Equal by the 10 second
	}

	@Then("^account (\\d+) has transaction (\\d+) of amount (\\d+) with debtor account (\\d+) and balance (\\d+) and description \"([^\"]*)\" and current Date/Time$")
	public void accountHasTransactionOfAmountWithDebtorAccountAndBalanceAndDescriptionAndCurrentDateTime(int arg1, int arg2, int arg3, int arg4, int arg5, String arg6) throws Throwable {
		Account account = bank.getAccount(accounts.get(arg1 - 1));
		Transaction[] transactions = account.getTransactions();
		Transaction t = transactions[arg2-1];
		assertEquals(new BigDecimal(arg3),t.getAmount());
		assertEquals(accounts.get(arg4-1),t.getDebtor());
		assertEquals("this account",t.getCreditor());
		assertEquals(new BigDecimal(arg5),t.getBalance());
		assertEquals(arg6,t.getDescription());
		Date time = t.getTime();
		assertNotNull(time);
		Date now = new Date();
		assertEquals(now.getTime()/10000,time.getTime()/10000); // Equal by the 10 second
	}

}
