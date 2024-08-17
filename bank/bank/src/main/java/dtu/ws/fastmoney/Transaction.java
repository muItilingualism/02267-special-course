package dtu.ws.fastmoney;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Represents a transfer of money from the debitor to the creditor for a specific reason.
 * 
 * It contains information about the money being transferred, the resulting balance, 
 * the date of the transaction, a description of the transfer, and debitor and creditor of the 
 * transaction.
 * 
 * 
 */
@Entity
@Table(name="\"TRANSACT\"") // Appears, TRANSACTION is not a valid table name in neither Derbry nor Sqlite
public class Transaction {
	@Id
	@GeneratedValue
	private int id;
	
	private String debtor;
	private String creditor;
	private BigDecimal amount;
	private BigDecimal balance;
	private String description;
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ss.SSSZ", timezone="UTC")
	@Temporal(TemporalType.TIMESTAMP)
	private Date time;
	
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

	public Date getTime() {
		return time;
	}

	public void setTime(Date dateAndTime) {
		this.time = dateAndTime;
	}

}
