package com.revolut.banking.service;

import java.math.BigDecimal;

import org.eclipse.jetty.http.HttpStatus;

import com.google.gson.Gson;
import com.revolut.banking.controller.GenericResponse;
import com.revolut.banking.dao.AccountDao;
import com.revolut.banking.dao.impl.AccountDaoImpl;
import com.revolut.banking.model.Account;
import com.revolut.banking.model.AccountConstants;
import com.revolut.banking.model.Transfer;

import spark.Request;
import spark.Response;

public class MoneyTransferService {
	private static final AccountDao accountDao = new AccountDaoImpl();

	public GenericResponse createAccount(final Request request, final Response response) {
		Account account = new Gson().fromJson(request.body(), Account.class);

		if (account.getBalance() == null || account.getBalance().compareTo(new BigDecimal(0)) < 0) {
			return generateResponse(response, HttpStatus.BAD_REQUEST_400, "Unable to create account");
		}

		accountDao.create(account);

		return generateResponse(response, HttpStatus.OK_200, account);
	}

	public GenericResponse getAccountBalance(final Request request, final Response response) {

		Long accNumber = Long.parseLong(request.params(AccountConstants.ACC_NUMBER));
		Account account = accountDao.getBalanceStatement(accNumber);

		if (account == null) {
			return generateResponse(response, HttpStatus.NOT_FOUND_404, "Unable to find account");
		}

		return generateResponse(response, HttpStatus.OK_200, new Gson().toJsonTree(account));
	}

	public GenericResponse transferMoney(final Request request, final Response response) {

		Transfer transfer = new Gson().fromJson(request.body(), Transfer.class);

		if (transfer.getAmount().compareTo(new BigDecimal(0)) <= 0) {
			return generateResponse(response, HttpStatus.BAD_REQUEST_400, "Invalid Amount");
		}

		if (!accountDao.isAccNumberValid(transfer.getFromAccount())) {
			return generateResponse(response, HttpStatus.NOT_FOUND_404, "FromAccount Not Found");
		}

		if (!accountDao.checkAccountBalance(transfer)) {
			return generateResponse(response, HttpStatus.BAD_REQUEST_400, "Insufficient Amount");
		}

		if (!accountDao.isAccNumberValid(transfer.getToAccount())) {
			return generateResponse(response, HttpStatus.NOT_FOUND_404, "ToAccount Not Found");
		}

		Account account = accountDao.transferMoney(transfer);

		if (account == null) {
			return generateResponse(response, HttpStatus.BAD_REQUEST_400, "Money Transfer Failed");
		}

		return generateResponse(response, HttpStatus.OK_200, account);
	}

	private GenericResponse generateResponse(Response response, int status, Object data) {
		response.status(status);
		return new GenericResponse(status, new Gson().toJsonTree(data));
	}
}
