package cucumber.utilities;

import java.net.MalformedURLException;
import java.net.URL;

import dtu.ws.fastmoney.BankService;
import dtu.ws.fastmoney.BankServiceService;

public class BankFactory {
	public BankService createBank() {
//		try {
//			BankService service = new BankServiceService(new URL(getEndpoint())).getBankServicePort();
			BankService service = new BankServiceService().getBankServicePort();
			return service;
//		} catch (MalformedURLException e) {
//			throw new Error(e);
//		}
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
