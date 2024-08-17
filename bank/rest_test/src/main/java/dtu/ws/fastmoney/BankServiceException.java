package dtu.ws.fastmoney;

public class BankServiceException extends Exception {

	private static final long serialVersionUID = 1L;

	public BankServiceException(String string) {
		super(string);
	}

	public String getErrorMessage() {
		return getMessage();
	}
}
