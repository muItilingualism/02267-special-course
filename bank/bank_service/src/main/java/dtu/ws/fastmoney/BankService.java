package dtu.ws.fastmoney;

import java.math.BigDecimal;

import javax.jws.WebParam;
import javax.jws.WebService;

import dtu.ws.fastmoney.persistency.InMemoryRepository;

@WebService
public class BankService {

	Bank bank = new BankSingleton().getInstance();
	
	public BankService() {
		bank = new BankSingleton().getInstance();
		bank.setRepository(InMemoryRepository.getInstance());
	}
	
	public String createAccountWithBalance(@WebParam(name = "user") User user
			, @WebParam(name="balance") BigDecimal initialBalance) throws BankServiceException {
		return bank.createAccountWithBalance(user, initialBalance);
	}

	public void retireAccount(@WebParam(name="account_id") String accountId) throws BankServiceException {
		bank.retireAccount(accountId);
	}

	public void transferMoneyFromTo(@WebParam(name = "debtor") String accountIdFrom
			, @WebParam(name = "creditor") String accountIdTo
			, @WebParam(name = "amount") BigDecimal amount
			, @WebParam(name = "description") String description)
			throws BankServiceException {
		bank.transferMoneyFromTo(accountIdFrom, accountIdTo, amount, description);
	}

	public Account getAccount(@WebParam(name = "account_id") String accountId) throws BankServiceException {
		return bank.getAccount(accountId);
	}
	
	public Account getAccountByCprNumber(@WebParam(name = "cpr") String cprNumber) throws BankServiceException {
		return bank.getAccountByCprNumber(cprNumber);
	}

//	public AccountInfo[] getAccounts() {
//		return bank.getAccounts();
//	}
}
