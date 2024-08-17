package dtu.ws.fastmoney.persistency;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import dtu.ws.fastmoney.Account;
import dtu.ws.fastmoney.BankServiceException;
import dtu.ws.fastmoney.Transaction;

/**
 * Stores the bank data in a hash map of account numbers to accounts.
 */
public class InMemoryRepository implements Repository {

	private static Repository instance = new InMemoryRepository();

	public static Repository getInstance() {
		return instance;
	}

	Map<String, Account> accounts = new ConcurrentHashMap<>();

	@Override
	public void createAccount(Account a) {
		accounts.put(a.getId(), a);

	}

	@Override
	public Account readAccount(String id) {
		if (id == null) {
			return null;
		}
		return accounts.get(id);
	}

	@Override
	public void deleteAccount(String id) {
		accounts.remove(id);
	}

	@Override
	public void updateAccount(Account a) {
		accounts.put(a.getId(), a);
	}

	@Override
	public void createAccountTransaction(Transaction t) {
		// Don't need to do anything for the InMemoryDatabase.
	}

	@Override
	public Account readAccountByCpr(String cprNumber) {
		Optional<Account> acc = accounts.values().stream().filter(a -> a.getUser().getCprNumber().equals(cprNumber))
				.findFirst();
		if (acc.isPresent()) {
			return acc.get();
		} else {
			return null;
		}
	}

	@Override
	public List<Account> readAccounts() {
		return new ArrayList<Account>(accounts.values());
	}

	@Override
	public void executeInTransaction(UnitFunction f) throws BankServiceException {
		f.unitFunction();

	}

}
