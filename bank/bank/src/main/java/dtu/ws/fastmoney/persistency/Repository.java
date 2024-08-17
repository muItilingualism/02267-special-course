package dtu.ws.fastmoney.persistency;

import java.util.List;

import dtu.ws.fastmoney.Account;
import dtu.ws.fastmoney.BankServiceException;
import dtu.ws.fastmoney.Transaction;

/**
 * Represents an abstraction to access a database. It provides operations to 
 * create, read, delete, and update accounts and to create transactions used with accounts.
 * 
 * Databases for the bank implement the Repository interface.
 * 
 */
public interface Repository {

	/**
	 * Create a new account in the database.
	 * @param a the account to be created
	 */
	void createAccount(Account a);

	/**
	 * 
	 * @param id the account number
	 * @return the account for the given account number
	 */
	Account readAccount(String id);

	/**
	 * Delete the account with the given account number.
	 * @param id the account number
	 */
	void deleteAccount(String id);

	/**
	 * Update the records for a given account in the database.
	 * @param a the account with the new information
	 */
	void updateAccount(Account a);

	/**
	 * Create a transaction record in the database.
	 * @param t the transaction
	 */
	void createAccountTransaction(Transaction t);

	/**
	 * 
	 * @param cpr the CPR number of the user owning the account
	 * @return the account object for the user with a given CPR number
	 */
	Account readAccountByCpr(String cpr);

	/**
	 * 
	 * @return a list of all accounts in the database
	 */
	List<Account> readAccounts();

	/**
	 * Executes a set of commands in the context of a database transaction. This can be used 
	 * as follows <code>repo.executeInTransaction(() -&gt; {...;});</code>
	 * @param f is an instance of class UnitFunction representing a method with no arguments and no return value
	 * @throws BankServiceException when the transaction fails after the transaction was rolled back
	 */
	void executeInTransaction(UnitFunction f) throws BankServiceException;

}