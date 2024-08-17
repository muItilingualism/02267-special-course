package dtu.ws.fastmoney;

/**
 * This class represents an abbreviated version of an account and is used 
 * to be returned in the list of accounts
 * instead of a complete account.
 * 
 *  An account information contains the user of an account and the account number,
 *  but does not contain the list of transactions belonging to an account. */

public class AccountInfo {
	private User user;
	private String accountId;
	
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public String getAccountId() {
		return accountId;
	}
	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}
}
