package dtu.ws.fastmoney;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Account {

	private User user;
	private String id;
	private BigDecimal balance;
	private Transaction[] transactions;
	
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
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
	
	public Transaction[] getTransactions() {
		return transactions;
	}
	
	public void setTransactions(Transaction[] transactions) {
		this.transactions = transactions;
	}


}
