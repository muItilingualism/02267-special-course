package cucumber.utilities;

import dtu.ws.fastmoney.BankService;
import dtu.ws.fastmoney.BankServiceService;

public class BankFactory {
	public BankService createBank() {
			BankService service = new BankServiceService().getBankServicePort();
			return service;
	}
	
	String getEndpoint() {
		String host = System.getProperty("service.host");
		if (host == null || host.isEmpty()) {
			host = "localhost";
		}
		String port = System.getProperty("service.port");
		if (port == null || port.isEmpty()) {
			port = "8080";
		}
		String endpoint =  String.format("http://%s:%s/BankService",host,port);
		return endpoint;
	}
}
