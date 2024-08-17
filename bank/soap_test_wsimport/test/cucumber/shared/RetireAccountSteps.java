package cucumber.shared;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import dtu.ws.fastmoney.BankService;
import dtu.ws.fastmoney.BankServiceException;
import dtu.ws.fastmoney.BankServiceException_Exception;
import dtu.ws.fastmoney.User;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class RetireAccountSteps {

	Helper helper;
	
	BankService bank;
	String accountId;
	User user;
	
	public RetireAccountSteps(Helper helper) {
		this.helper = helper;
		this.bank = helper.getBank();
	}

	@Given("^an existent account \"([^\"]*)\", \"([^\"]*)\", \"([^\"]*)\"$")
	public void anExistentAccount(String arg1, String arg2, String arg3) throws Throwable {
		user = helper.createUser(arg1, arg2, arg3);
		accountId = helper.createAccount(arg1,arg2,arg3,0);
	}

	@When("^I retire that account$")
	public void iRetireThatAccount() throws Throwable {
		helper.errorHasOccured = false;
		try {
			bank.retireAccount(accountId);
		} catch (BankServiceException_Exception e) {
			helper.errorHasOccured = true;
			helper.errorMessage = e.getFaultInfo().getErrorMessage();
		}
	}

	@Then("^I can create a new account with the same information$")
	public void iCanCreateANewAccountWithTheSameInformation() throws Throwable {
		try {
			accountId = helper.createAccount(user,0);
			assertNotNull(accountId);
			assertFalse(accountId.isEmpty());
		} catch (BankServiceException_Exception e) {
			fail("Should not throw an exception");
		}
	}
	@Given("^that account Id \"([^\"]*)\" does not exist$")
	public void thatAccountIdDoesNotExist(String arg1) throws Throwable {
		/* Nothing to do at the momement, we cannot yet check if an account id exists or not.
		 * However, the choice of account id makes it clear, that it won't exist"
		 */
	}
	
	@Then("^I don't get an error messsage$")
	public void iDonTGetAnErrorMesssage() throws Throwable {
		assertNull(helper.errorMessage);
		assertFalse(helper.errorHasOccured);
	}


}
