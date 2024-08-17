package cucumber.shared;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import java.math.BigDecimal;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import dtu.ws.fastmoney.Account;
import dtu.ws.fastmoney.BankServiceException;
import dtu.ws.fastmoney.User;

public class CreateAccountSteps {

	Helper helper;

	private User user;
	private String accountId1;
	private String accountId2;
	private Account account1;
	private String cprNumber;

	public CreateAccountSteps(Helper helper) {
		this.helper = helper;
		helper.getBank();
	}

	@Given("^a user with first name \"([^\"]*)\", last name \"([^\"]*)\", and CPR number \"([^\"]*)\"$")
	public void aUserWithFirstNameLastNameAndCPRNumber(String arg1, String arg2, String arg3) throws Throwable {
		user = helper.createUser(arg1, arg2, arg3);
	}

	@When("^creating an account$")
	public void creatingAnAccount() throws Throwable {
		try {
			accountId1 = helper.createAccount(user, 0);
			cprNumber = user.getCprNumber();
		} catch (BankServiceException e) {
			helper.errorMessage = e.getErrorMessage();
		}
	}
	
	@When("^creating an account with balance \\+?(-?\\d+)$")
	public void creatingAnAccountWithBalance(int arg1) throws Throwable {
		try {
			accountId1 = helper.createAccount(user, arg1);
		} catch (BankServiceException e) {
			helper.errorMessage = e.getErrorMessage();
		}
	}


	@Then("^a valid account id is assigned\\.$")
	public void aValidAccountIdIsAssigned() throws Throwable {
		assertNotNull(accountId1);
	}

	@Given("^that I have created an account for user \"([^\"]*)\", \"([^\"]*)\", \"([^\"]*)\"$")
	public void thatIHaveCreatedAnAccountForUser(String arg1, String arg2, String arg3) throws Throwable {
		user = helper.createUser(arg1, arg2, arg3);
		accountId1 = helper.createAccount(user, 0);
	}

	@When("^I create the account a second time$")
	public void iCreateTheAccountASecondTime() throws Throwable {
		try {
			helper.createAccount(user, 0);
		} catch (BankServiceException e) {
			helper.errorMessage = e.getErrorMessage();
		}
	}

	@When("^I create an account for \"([^\"]*)\", \"([^\"]*)\", \"([^\"]*)\"$")
	public void iCreateAnAccountFor(String arg1, String arg2, String arg3) throws Throwable {
		user = helper.createUser(arg1, arg2, arg3);
		accountId2 = helper.createAccount(user, 0);
	}

	@Then("^Then I have two accounts with differennt account id$")
	public void thenIHaveTwoAccountsWithDifferenntAccountId() throws Throwable {
		assertNotEquals(accountId1, accountId2);
	}

	@Then("^I get the error message \"([^\"]*)\"$")
	public void iGetTheErrorMessage(String arg1) throws Throwable {
		assertEquals(arg1, helper.errorMessage);
		helper.errorMessage = null;
	}

	@When("^getting the account$")
	public void gettingTheAccount() throws Throwable {
		account1 = helper.getBank().getAccount(accountId1);
	}

	@Then("^the account has the correct account id$")
	public void theAccountHasTheCorrectAccountId() throws Throwable {
		assertEquals(accountId1, account1.getId());
	}

	@Then("^the first name is \"([^\"]*)\", last name is \"([^\"]*)\", and CPR number is \"([^\"]*)\"$")
	public void theFirstNameIsLastNameIsAndCPRNumberIs(String arg1, String arg2, String arg3) throws Throwable {
		assertEquals(arg1, account1.getUser().getFirstName());
		assertEquals(arg2, account1.getUser().getLastName());
		assertEquals(arg3, account1.getUser().getCprNumber());
		assertEquals(new BigDecimal(0), account1.getBalance());
		if (account1.getTransactions() != null) {
			assertEquals(0, account1.getTransactions().length);
		}
	}

	@When("^getting an account with account Id \"([^\"]*)\"$")
	public void gettingAnAccountWithAccountId(String arg1) throws Throwable {
		try {
			account1 = helper.getBank().getAccount(arg1);
		} catch (BankServiceException e) {
			helper.errorMessage = e.getErrorMessage();
		}
	}
	
	@When("^getting the account by CPR number$")
	public void gettingTheAccountByCPRNumber() throws Throwable {
		try {
			account1 = helper.getBank().getAccountByCprNumber(cprNumber);
			accountId1 = account1.getId();
		} catch (BankServiceException e) {
			helper.errorMessage = e.getErrorMessage();
		}
	}

	@When("^getting an account with CPR number \"([^\"]*)\"$")
	public void gettingAnAccountWithCPRNumber(String arg1) throws Throwable {
		try {
			account1 = helper.getBank().getAccountByCprNumber(arg1);
			accountId1 = account1.getId();
		} catch (BankServiceException e) {
			helper.errorMessage = e.getErrorMessage();
		}
	}
}
