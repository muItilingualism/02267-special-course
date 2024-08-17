package cucumber.utilities;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import dtu.ws.fastmoney.BankService;

public class BankFactory {
	public BankService createBank() {
		return new BankService(getEndpoint());
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
		String endpoint = String.format("http://%s:%s/rest", host,port);
		return endpoint;
	}

}
