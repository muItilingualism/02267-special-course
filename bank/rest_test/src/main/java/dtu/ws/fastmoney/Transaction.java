package dtu.ws.fastmoney;

import java.math.BigDecimal;
import java.util.Calendar;

public class Transaction {
	private int id;
	
	private String debtor;
	private String creditor;
	private BigDecimal amount;
	private BigDecimal balance;
	private String description;
	private Calendar time;
	
	public int getId() {
		return id;
	}
	
	public String getDebtor() {
		return debtor;
	}
	public void setDebtor(String debtor) {
		this.debtor = debtor;
	}
	public String getCreditor() {
		return creditor;
	}
	public void setCreditor(String creditor) {
		this.creditor = creditor;
	}
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount2) {
		this.amount = amount2;
	}
	public BigDecimal getBalance() {
		return balance;
	}
	public void setBalance(BigDecimal balance2) {
		this.balance = balance2;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Calendar getTime() {
		return time;
	}

	public void setTime(Calendar dateAndTime) {
		this.time = dateAndTime;
	}

}
