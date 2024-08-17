package cucumber.utilities;

import dtu.ws.fastmoney.Bank;
import dtu.ws.fastmoney.persistency.InMemoryRepository;

public class BankFactory {
	public Bank createBank() {
		Bank bank = new Bank();
		bank.setRepository(new InMemoryRepository());
		return bank;
	}
}
