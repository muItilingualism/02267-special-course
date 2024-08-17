package dtu.ws.fastmoney;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import dtu.ws.fastmoney.persistency.InMemoryRepository;
import dtu.ws.fastmoney.persistency.Repository;

/**
 * Class Bank represents bank services, with accounts and the transfer of money
 * between accounts. A bank is created with <code>new Bank()</code>. The default
 * persistence layer uses an in-memory database. An alternative persistence
 * layer can be set using <code>setRepository</code>.
 * 
 * All accesses to the bank, that go through the same object, use the same
 * database.
 */
public class Bank {

	/*
	 * Default database. Can/Should be set to the production database.
	 */
	Repository pl = new InMemoryRepository();

	/**
	 * Create a new account with a given balance for a given user. The method
	 * returns a unique account number.
	 * 
	 * @param user           for which the account should be created
	 * @param initialBalance the initial balance
	 * @return the account number
	 * @throws BankServiceException when the initial balance is negative or the user
	 *                              has null or empty fields for CPR number, first
	 *                              name, or last name
	 */
	public String createAccountWithBalance(User user, BigDecimal initialBalance) throws BankServiceException {
		if (user == null) {
			throw new BankServiceException("Missing user information");
		}
		if (initialBalance == null) {
			throw new BankServiceException("Missing balance information");
		}
		if (initialBalance.compareTo(new BigDecimal(0)) == -1) {
			throw new BankServiceException("Initial balance must be 0 or positive");
		}
		System.out.format("createAccount(%s,%s)\n", user.toString(), initialBalance.toString());
		if (user.getCprNumber() == null || user.getCprNumber().isEmpty()) {
			throw new BankServiceException("Missing CPR number");
		}
		if (user.getFirstName() == null || user.getFirstName().isEmpty()) {
			throw new BankServiceException("Missing first name");
		}
		if (user.getLastName() == null || user.getLastName().isEmpty()) {
			throw new BankServiceException("Missing last name");
		}

		synchronized (this) {
			// Trying to avoid a concurrent modification exception
			if (findAccountByCprNumber(user.getCprNumber()).isPresent()) {
				throw new BankServiceException("Account already exists");
			}
			Account account = new Account();
			account.setUser(user);
			account.setId(UUID.randomUUID().toString());
			account.setBalance(initialBalance);
			pl.executeInTransaction(() -> {
				pl.createAccount(account);
			});
			return account.getId();
		}
	}

	private Optional<Account> findAccountByCprNumber(String cprNumber) {
		Account a = pl.readAccountByCpr(cprNumber);
		if (a == null) {
			return Optional.empty();
		} else {
			return Optional.of(a);
		}
	}

	/**
	 * Delete an existing account.
	 * 
	 * @param account is the account number to be deleted
	 * @throws BankServiceException when the account doe snot exist
	 */
	synchronized public void retireAccount(String account) throws BankServiceException {
		System.out.format("retireAccount(%s)\n", account);
		if (pl.readAccount(account) != null) {
			pl.executeInTransaction(() -> {
				pl.deleteAccount(account);
			});
		} else {
			throw new BankServiceException("Account does not exist");
		}
	}

	/**
	 * Transfer money from one account to another
	 * 
	 * @param accountFrom the debitor
	 * @param accountTo   the creditor
	 * @param amount      the amount to transfer
	 * @param description the reason for the transaction
	 * @throws BankServiceException when amount is negative, or the debitor and
	 *                              creditor do not exists or the description is
	 *                              missing
	 */
	public void transferMoneyFromTo(String accountFrom, String accountTo, BigDecimal amount, String description)
			throws BankServiceException {
		System.out.format("transferMoney(%s,%s,%s,%s)\n", accountFrom, accountTo, amount, description);

		ExceptionStatus exceptionStatus = new ExceptionStatus();
		if (accountFrom == null || !hasAccount(accountFrom)) {
			throw new BankServiceException("Debtor account does not exist");
		}
		if (accountTo == null || !hasAccount(accountTo)) {
			throw new BankServiceException("Creditor account does not exist");
		}
		if (amount == null || amount.compareTo(new BigDecimal(0)) == -1) {
			throw new BankServiceException("Amount must be positive");
		}
		if (description == null || description.isEmpty()) {
			throw new BankServiceException("Description missing");
		}
		synchronized (this) {
			pl.executeInTransaction(() -> {
				Account f = pl.readAccount(accountFrom);
				Account t = pl.readAccount(accountTo);
				if (f.getBalance().subtract(amount).compareTo(new BigDecimal(0)) == -1) {
					exceptionStatus.throwException = true;
				} else {
					exceptionStatus.throwException = false;
					Date time = new Date();
					f.sendMoney(pl, amount, description, t, time);
				}
			});
		}
		if (exceptionStatus.throwException) {
			throw new BankServiceException("Debtor balance will be negative");
		}

	}

	class ExceptionStatus {
		boolean throwException = false;
	}

	private boolean hasAccount(String accountId) {
		return pl.readAccount(accountId) != null;
	}

	/**
	 * Return the account data for a givcen account number.
	 * 
	 * @param account the account number
	 * @return the account data
	 * @throws BankServiceException when the account number does not exist
	 */
	public Account getAccount(String account) throws BankServiceException {
		System.out.format("getAccount(%s)\n", account);

		if (account == null || !hasAccount(account)) {
			throw new BankServiceException("Account does not exist");
		}
		return pl.readAccount(account);
	}

	/**
	 * This function is disabled because it would not exist on a public interface of
	 * a bank.
	 */

	/**
	 * Return an account data for a user with a given CpR number
	 * 
	 * @param cpr CPR number
	 * @return the account data
	 * @throws BankServiceException when the bank does not have a user with the
	 *                              given CPR number
	 */
	public Account getAccountByCprNumber(String cpr) throws BankServiceException {
		System.out.format("getAccountByCpr(%s)\n", cpr);

		if (cpr == null) {
			throw new BankServiceException("CPR can't be null");
		}

		Optional<Account> a = findAccountByCprNumber(cpr);
		if (a.isPresent()) {
			return a.get();
		} else {
			throw new BankServiceException("Account does not exist");
		}
	}

	/**
	 * @return the list of accounts of the bank.
	 */
	public AccountInfo[] getAccounts() {
		System.out.format("getAccounts\n");

		List<AccountInfo> as = new ArrayList<>();
		for (Account a : pl.readAccounts()) {
			AccountInfo ai = new AccountInfo();
			ai.setUser(a.getUser());
			ai.setAccountId(a.getId());
			as.add(ai);
		}
		return as.toArray(new AccountInfo[0]);
	}

	/**
	 * Set the database used by the bank. The default database is an in-memory
	 * database.
	 * 
	 * @param repo the database the bank should be using.
	 */
	public void setRepository(Repository repo) {
		pl = repo;
	}
}
