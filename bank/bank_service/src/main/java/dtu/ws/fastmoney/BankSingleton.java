package dtu.ws.fastmoney;

public class BankSingleton {
	
	private final static Bank instance = new Bank();
	
	public Bank getInstance() {
		return instance;
	}
}
