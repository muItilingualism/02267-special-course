package dtu.ws.bank.rest;

public class AccountInfo {
	private UserRest user;
	private String accountId;
	
	public UserRest getUser() {
		return user;
	}
	public void setUser(UserRest user) {
		this.user = user;
	}
	public String getAccountId() {
		return accountId;
	}
	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}
}
