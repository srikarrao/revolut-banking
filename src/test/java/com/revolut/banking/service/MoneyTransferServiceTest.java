package com.revolut.banking.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.gson.Gson;
import com.revolut.banking.controller.GenericResponse;
import com.revolut.banking.model.Account;
import com.revolut.banking.model.AccountConstants;

import spark.Request;
import spark.Response;

public class MoneyTransferServiceTest {

	private MoneyTransferService moneyTransferService = new MoneyTransferService();
	private Request request;
	private Response response;
	private final String ACCOUNT_JSON_1 = "{\"balance\": 237.8}";
	private final String ACCOUNT_JSON_2 = "{\"balance\": -1.0}";

	private final String TRANSFER_JSON_1 = "{\"amount\": 0.0, \"fromAccount\": 1221, \"toAccount\": 1890}";

	@Before
	public void setUp() {
		request = Mockito.mock(Request.class);
		response = Mockito.mock(Response.class);
	}

	@Test
	public void testCreateAccount_200() {
		Mockito.when(request.body()).thenReturn(ACCOUNT_JSON_1);
		GenericResponse res = moneyTransferService.createAccount(request, response);
		assertNotNull(res);
		assertEquals(200, res.getStatus());
		assertNotNull(res.getData());
		Account acc = new Gson().fromJson(res.getData().toString(), Account.class);
		assertTrue(Math.abs(acc.getBalance().doubleValue()) >= 237.0);
		assertTrue(acc.getAccNumber() > 1000L);
	}

	@Test
	public void testCreateAccount_400() {
		Mockito.when(request.body()).thenReturn(ACCOUNT_JSON_2);
		GenericResponse res = moneyTransferService.createAccount(request, response);
		assertNotNull(res);
		assertEquals(400, res.getStatus());
		assertNotNull(res.getData());
		assertEquals("Unable to create account", res.getData().toString().replace("\"", ""));
	}

	@Test
	public void testGetAccountBalance_200() {
		Mockito.when(request.body()).thenReturn(ACCOUNT_JSON_1);

		GenericResponse accountCreationResponse = moneyTransferService.createAccount(request, response);
		Account account = new Gson().fromJson(accountCreationResponse.getData().toString(), Account.class);
		Mockito.when(request.params(AccountConstants.ACC_NUMBER)).thenReturn(account.getAccNumber() + "");

		GenericResponse res = moneyTransferService.getAccountBalance(request, response);
		assertNotNull(res);
		assertEquals(200, res.getStatus());
		assertNotNull(res.getData());

		Account accountBalance = new Gson().fromJson(res.getData().toString(), Account.class);
		assertTrue(Math.abs(accountBalance.getBalance().doubleValue()) == 238.0);
		assertTrue(accountBalance.getAccNumber() > 1000L);
	}

	@Test
	public void testGetAccountBalance_404() {
		Mockito.when(request.params(AccountConstants.ACC_NUMBER)).thenReturn("11233");
		GenericResponse res = moneyTransferService.getAccountBalance(request, response);
		assertNotNull(res);
		assertEquals(404, res.getStatus());
		assertNotNull(res.getData());
		assertEquals("Unable to find account", res.getData().toString().replace("\"", ""));
	}

	@Test
	public void testTransferMoney_200() {
		Mockito.when(request.body()).thenReturn(ACCOUNT_JSON_1);
		GenericResponse fromAccResponse = moneyTransferService.createAccount(request, response);
		GenericResponse toAccResponse = moneyTransferService.createAccount(request, response);

		Account fromAccount = new Gson().fromJson(fromAccResponse.getData().toString(), Account.class);
		Account toAccount = new Gson().fromJson(toAccResponse.getData().toString(), Account.class);

		Mockito.when(request.body()).thenReturn(ACCOUNT_JSON_1);

		String transferRequestTemplate = "{\"amount\": 10.0, \"fromAccount\":" + fromAccount.getAccNumber()
				+ ", \"toAccount\":" + toAccount.getAccNumber() + "}";

		Mockito.when(request.body()).thenReturn(transferRequestTemplate);
		GenericResponse result = moneyTransferService.transferMoney(request, response);
		assertNotNull(result);
		assertEquals(200, result.getStatus());
		assertNotNull(result.getData());

		Account fromAccount2 = new Gson().fromJson(result.getData().toString(), Account.class);
		assertTrue(Math.abs(fromAccount2.getBalance().doubleValue()) < 238.0);

		Mockito.when(request.params(AccountConstants.ACC_NUMBER)).thenReturn(toAccount.getAccNumber() + "");
		GenericResponse toAcc = moneyTransferService.getAccountBalance(request, response);
		Account accountBalance = new Gson().fromJson(toAcc.getData().toString(), Account.class);
		assertTrue(Math.abs(accountBalance.getBalance().doubleValue()) > 238.0);
	}

	@Test
	public void testTransferMoney_400_InvalidAmount() {

		Mockito.when(request.body()).thenReturn(TRANSFER_JSON_1);

		GenericResponse result = moneyTransferService.transferMoney(request, response);
		assertNotNull(result);
		assertEquals(400, result.getStatus());
		assertNotNull(result.getData());
		assertEquals("Invalid Amount", result.getData().toString().replace("\"", ""));
	}

	@Test
	public void testTransferMoney_400_InsufficientAmount() {
		Mockito.when(request.body()).thenReturn(ACCOUNT_JSON_1);
		GenericResponse fromAccResponse = moneyTransferService.createAccount(request, response);
		GenericResponse toAccResponse = moneyTransferService.createAccount(request, response);

		Account fromAccount = new Gson().fromJson(fromAccResponse.getData().toString(), Account.class);
		Account toAccount = new Gson().fromJson(toAccResponse.getData().toString(), Account.class);

		Mockito.when(request.body()).thenReturn(ACCOUNT_JSON_1);

		String transferRequestTemplate = "{\"amount\": 1000.0, \"fromAccount\":" + fromAccount.getAccNumber()
				+ ", \"toAccount\":" + toAccount.getAccNumber() + "}";

		Mockito.when(request.body()).thenReturn(transferRequestTemplate);
		GenericResponse result = moneyTransferService.transferMoney(request, response);
		assertNotNull(result);
		assertEquals(400, result.getStatus());
		assertNotNull(result.getData());
		assertEquals("Insufficient Amount", result.getData().toString().replace("\"", ""));
	}

	@Test
	public void testTransferMoney_404_InvalidFromAccount() {
		String transferRequestTemplate = "{\"amount\": 100.0, \"fromAccount\":211,toAccount\":984}";

		Mockito.when(request.body()).thenReturn(transferRequestTemplate);
		GenericResponse result = moneyTransferService.transferMoney(request, response);
		assertNotNull(result);
		assertEquals(404, result.getStatus());
		assertNotNull(result.getData());
		assertEquals("FromAccount Not Found", result.getData().toString().replace("\"", ""));
	}

	@Test
	public void testTransferMoney_400_InvalidToAccount() {
		Mockito.when(request.body()).thenReturn(ACCOUNT_JSON_1);
		GenericResponse fromAccResponse = moneyTransferService.createAccount(request, response);

		Account fromAccount = new Gson().fromJson(fromAccResponse.getData().toString(), Account.class);
		Mockito.when(request.body()).thenReturn(ACCOUNT_JSON_1);

		String transferRequestTemplate = "{\"amount\": 10.0, \"fromAccount\":" + fromAccount.getAccNumber()
				+ ", toAccount:988}";

		Mockito.when(request.body()).thenReturn(transferRequestTemplate);
		GenericResponse result = moneyTransferService.transferMoney(request, response);
		assertNotNull(result);
		assertEquals(404, result.getStatus());
		assertNotNull(result.getData());
		assertEquals("ToAccount Not Found", result.getData().toString().replace("\"", ""));
	}
}