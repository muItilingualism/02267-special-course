package dtu.ws.fastmoney;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import dtu.ws.fastmoney.persistency.Repository;

/**
 * An account represents the account of a user. An account has a user, an account number,
 * a balance, and a list of transactions.
 */
@Entity
public class Account {

	@Embedded
	private User user;
	
	@Id
	private String id;
	private BigDecimal balance;
	
	/* The trick with ptranscations and transactions is used
	 * to return externally an array of transactions but internally keep
	 * a list of transactions because it is easier to add transactions to 
	 * a list than an array.
	 * 
	 * However, a list is not good as the type of the transactions field,
	 * because the used AXIS XML deserializer creates an array instead of 
	 * a list for the given XML.
	 */
	@OneToMany(cascade=CascadeType.ALL)
	private List<Transaction> pTransactions = new ArrayList<>();

	@Transient
	private Transaction[] transactions;
	
	/**
	 * 
	 * @return the user for the account.
	 */
	public User getUser() {
		return user;
	}
	
	public void setUser(User user) {
		this.user = user;
	}
	
	/**
	 * 
	 * @return the account number of the account.
	 */
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
	
	/**
	 * 
	 * @return the list of transactions recorded for that account.
	 */
	public Transaction[] getTransactions() {
		return pTransactions.toArray(new Transaction[0]);
	}
	
    void sendMoney(Repository pl, BigDecimal amount, String description, Account creditor, Date time) {
		Transaction t = new Transaction();
		t.setDebtor("this account");
		t.setAmount(amount);
		t.setCreditor(creditor.getId());
		t.setDescription(description);
		t.setTime(time);
		pl.createAccountTransaction(t);
		creditor.receiveMoney(pl,amount, description, this, time);
		balance = balance.subtract(amount);
		t.setBalance(balance);
		pTransactions.add(t);
		pl.updateAccount(this);
	}
	
	private void receiveMoney(Repository pl, BigDecimal amount, String description, Account debtor, Date time) {
		Transaction t = new Transaction();
		t.setDebtor(debtor.getId());
		t.setAmount(amount);
		t.setCreditor("this account");
		t.setDescription(description);
		t.setTime(time);
		balance = balance.add(amount);
		t.setBalance(balance);
		pl.createAccountTransaction(t);
		pTransactions.add(t);
		pl.updateAccount(this);
	}
	
	public void setTransactions(Transaction[] transactions) {
		this.pTransactions = Arrays.asList(transactions);
	}

}
