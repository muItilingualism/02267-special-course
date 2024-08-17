package cucumber.shared;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.List;

import dtu.ws.fastmoney.AccountInfo;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class GetAccountInfoSteps {
	Helper helper;
	List<AccountInfo> accounts;
	
	public GetAccountInfoSteps(Helper helper) {
		this.helper = helper;
	}
	
	@When("^I get all account infos$")
	public void iGetAllAccountInfos() throws Throwable {
		accounts = helper.getBank().getAccounts();
	}
	
	@Then("^account info include \"([^\"]*)\", \"([^\"]*)\", \"([^\"]*)\" and a valid account id$")
	public void accountInfoIncludeAndAValidAccountId(String arg2, String arg3, String arg4) throws Throwable {
		boolean found = false;
		for (AccountInfo a : accounts ) {
			if (a.getUser().getCprNumber().equals(arg4)) {
				assertEquals(arg2,a.getUser().getFirstName());
				assertEquals(arg3,a.getUser().getLastName());
				assertNotNull(a.getAccountId());
				found = true;
			}
		}
	if (!found) {
		fail("Matching Element Expected");
	}
	}
}

