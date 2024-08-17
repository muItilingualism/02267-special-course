package dtu.ws.bank.rest;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AccountRest {

	private UserRest user;
	private String id;
	private BigDecimal balance;
	private TransactionRest[] transactions;
	
	public UserRest getUser() {
		return user;
	}
	public void setUser(UserRest user) {
		this.user = user;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public void setBalance(BigDecimal initialBalance) {
		balance = initialBalance;
		
	}
	public BigDecimal getBalance() {
		return balance;
	}
	
	public TransactionRest[] getTransactions() {
		return transactions;
	}
	
	public void setTransactions(TransactionRest[] transactions) {
		this.transactions = transactions;
	}


}
