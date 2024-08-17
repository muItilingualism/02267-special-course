package dtu.ws.fastmoney;

/**
 * Exception being thrown if something goes wrong in operations associated with the bank.
 */
public class BankServiceException extends Exception {

	private static final long serialVersionUID = 1L;

	public BankServiceException(String string) {
		super(string);
	}

	public String getErrorMessage() {
		return getMessage();
	}
}
