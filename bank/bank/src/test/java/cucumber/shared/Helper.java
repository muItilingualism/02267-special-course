package cucumber.shared;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import cucumber.api.java.After;
import cucumber.utilities.BankFactory;
import dtu.ws.fastmoney.Bank;
import dtu.ws.fastmoney.User;

public class Helper {
	public Set<String> usedAccounts = new HashSet<>();
	public String errorMessage;	
    private Bank bank;
	public boolean errorHasOccured = false;
    
    public Helper(BankFactory factory) {
    		this.bank = factory.createBank();
    }
    
	Bank getBank() {
		return bank;
	}

	User createUser(String arg1, String arg2, String arg3) {
		User user = new User();
		user.setFirstName(arg1);
		user.setLastName(arg2);
		user.setCprNumber(arg3);
		return user;
	}
	
	String createAccount(String arg1, String arg2, String arg3, int balance) throws Exception {
		User user = createUser(arg1, arg2, arg3);
		return createAccount(user, balance);
	}

	public String createAccount(User user, int balance) throws Exception {
		String accountId = getBank().createAccountWithBalance(user, new BigDecimal(balance));
		usedAccounts.add(accountId);
		return accountId;
	}

	@After
	public void retireUsedAccounts() {
		for (String id : usedAccounts) {
			try {
				bank.retireAccount(id);
			} catch (Exception e) {
				// ignore: account does not exist anymore
			}
		}
		usedAccounts = new HashSet<String>();
	}

}
