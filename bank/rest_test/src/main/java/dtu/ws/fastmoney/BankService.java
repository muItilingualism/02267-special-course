package dtu.ws.fastmoney;

import java.io.IOException;
import java.math.BigDecimal;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.ObjectMapper;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public class BankService {

	String endpoint;

	{
		Unirest.setObjectMapper(new ObjectMapper() {
			private com.fasterxml.jackson.databind.ObjectMapper jacksonObjectMapper = new com.fasterxml.jackson.databind.ObjectMapper();

			public <T> T readValue(String value, Class<T> valueType) {
				try {
					return jacksonObjectMapper.readValue(value, valueType);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}

			public String writeValue(Object value) {
				try {
					return jacksonObjectMapper.writeValueAsString(value);
				} catch (JsonProcessingException e) {
					throw new RuntimeException(e);
				}
			}
		});
	}

	public BankService(String endpoint) {
		this.endpoint = endpoint;
	}

	public String createAccountWithBalance(User user, BigDecimal initialBalance) throws BankServiceException {
		CreateAccountData ca = new CreateAccountData();
		ca.user = user;
		ca.balance = initialBalance;
		try {
			HttpResponse<String> r = Unirest.post(endpoint + "/accounts").header("Content-Type", "application/json")
					.body(ca).asString();
			String result = r.getBody();
			if (r.getStatus() != 200) {
				throw new BankServiceException(result);
			} else {
				return result;
			}
		} catch (UnirestException e) {
			throw new Error(e);
		}
	}

	public void retireAccount(String accountId) throws BankServiceException {
		 try {
			HttpResponse<String> r = Unirest.delete(endpoint + "/accounts/"+accountId).asString();
			if (r.getStatus() != 204) {
				throw new BankServiceException(r.getBody());
			} 
		} catch (UnirestException e) {
			throw new Error(e);
		}
	}

	public void transferMoneyFromTo(String accountIdFrom, String accountIdTo, BigDecimal amount, String description)			
			throws BankServiceException {
		 try {
			 PaymentData pd = new PaymentData();
			 pd.amount = amount;
			 pd.debtor = accountIdFrom;
			 pd.creditor = accountIdTo;
			 pd.description = description;
			HttpResponse<String> r = Unirest.post(endpoint + "/payments").header("Content-Type", "application/json").body(pd).asString();
			if (r.getStatus() != 204) {
				throw new BankServiceException(r.getBody());
			} 
		} catch (UnirestException e) {
			throw new Error(e);
		}

	}

	public Account getAccount(String accountId) throws BankServiceException {
		try {
			HttpResponse<String> r =  Unirest.get(endpoint + "/accounts/"+accountId).header("accept", "application/json").asString();
			if(r.getStatus() != 200) {
				ErrorType error = new com.fasterxml.jackson.databind.ObjectMapper().readValue(r.getBody(),ErrorType.class);
				throw new BankServiceException(error.errorMessage);
			}
			return new com.fasterxml.jackson.databind.ObjectMapper().readValue(r.getBody(),Account.class);
		} catch (IOException|UnirestException e) {
			throw new Error(e);
		}

	}
	
	public AccountInfo[] getAccounts() {
		try {
			return Unirest.get(endpoint + "/accounts/hidden").header("accept", "application/json").asObject(AccountInfo[].class).getBody();
		} catch (UnirestException e) {
			throw new Error(e);
		}
	}

	public Account getAccountByCprNumber(String cprNumber) throws BankServiceException {
		try {
			HttpResponse<String> r =  Unirest.get(endpoint + "/accounts/cpr/"+cprNumber).header("accept", "application/json").asString();
			if(r.getStatus() != 200) {
				ErrorType error = new com.fasterxml.jackson.databind.ObjectMapper().readValue(r.getBody(),ErrorType.class);
				throw new BankServiceException(error.errorMessage);
			}
			return new com.fasterxml.jackson.databind.ObjectMapper().readValue(r.getBody(),Account.class);
		} catch (IOException|UnirestException e) {
			throw new Error(e);
		}
	}

}
