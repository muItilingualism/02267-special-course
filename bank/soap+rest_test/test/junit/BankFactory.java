package junit;

import dtu.ws.bank.rest.BankServiceRest;
import dtu.ws.fastmoney.BankService;
import dtu.ws.fastmoney.BankServiceService;

public class BankFactory {
	public BankService createBank() {
		return new BankServiceService().getBankServicePort();
	}
	
	public BankServiceRest createBankRest() {
		return new BankServiceRest(getEndpointRest());
	}
	
	String getEndpoint() {
		String host = System.getProperty("service.host");
		if (host == null || host.isEmpty()) {
			host = "localhost";
		}
		String port = System.getProperty("service.port");
		if (port == null || port.isEmpty()) {
			port = "80";
		}
		String endpoint =  String.format("http://%s:%s/BankService",host,port);
		return endpoint;
	}

	String getEndpointRest() {
		String host = System.getProperty("service.host");
		if (host == null || host.isEmpty()) {
			host = "localhost";
		}
		String port = System.getProperty("service.port");
		if (port == null || port.isEmpty()) {
			port = "80";
		}
		String endpoint = String.format("http://%s:%s/rest", host,port);
		return endpoint;
	}


}
