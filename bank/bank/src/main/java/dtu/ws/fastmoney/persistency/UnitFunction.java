package dtu.ws.fastmoney.persistency;

import dtu.ws.fastmoney.BankServiceException;
/**
 * Represents a function with no argument and no return value. 
 * Instances of this class can be represented using lambda functions as
 * <code>() -&gt; {...;}</code>
 */
public interface UnitFunction {
	void unitFunction() throws BankServiceException;
}

