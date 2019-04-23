package com.revolut.banking.dao.impl;

import java.math.BigDecimal;
import java.util.List;

import org.sql2o.Connection;
import org.sql2o.Sql2o;

import com.revolut.banking.config.DatabaseConfig;
import com.revolut.banking.dao.AccountDao;
import com.revolut.banking.model.Account;
import com.revolut.banking.model.AccountConstants;
import com.revolut.banking.model.Transfer;
import com.revolut.banking.utility.UniqueAccountNumberGenerator;

public class AccountDaoImpl implements AccountDao {

	private Sql2o databaseConnection;

	private static final String SELECT_ACCOUNT_QUERY = "SELECT * FROM ACCOUNT WHERE accNumber=:accNumber";
	private static final String CREATE_ACCOUNT_QUERY = "INSERT INTO ACCOUNT(accNumber,balance) VALUES (:accNumber, :balance)";
	private static final String UPDATE_BALANCE_QUERY = "UPDATE ACCOUNT SET balance=:balance where accNumber=:accNumber";

	public AccountDaoImpl() {
		this.databaseConnection = DatabaseConfig.getInstance().getDBConnection();
	}

	@Override
	public Account create(Account account) {
		try (Connection connection = databaseConnection.beginTransaction()) {
			long accountNumber = UniqueAccountNumberGenerator.generateRandomAccNumber();
			account.setAccNumber(accountNumber);
			connection.createQuery(CREATE_ACCOUNT_QUERY).addParameter(AccountConstants.ACC_NUMBER, accountNumber)
					.addParameter(AccountConstants.ACC_BALANCE, account.getBalance()).executeUpdate();
			connection.commit();
		}
		return account;
	}

	@Override
	public Account getBalanceStatement(Long accNumber) {
		List<Account> accounts = null;
		try (Connection con = databaseConnection.open()) {
			accounts = con.createQuery(SELECT_ACCOUNT_QUERY).addParameter(AccountConstants.ACC_NUMBER, accNumber)
					.executeAndFetch(Account.class);
		}
		return accounts == null || accounts.isEmpty() ? null : accounts.get(0);
	}

	@Override
	public boolean isAccNumberValid(Long accNumber) {

		try (Connection con = databaseConnection.open()) {
			List<Account> accounts = con.createQuery(SELECT_ACCOUNT_QUERY)
					.addParameter(AccountConstants.ACC_NUMBER, accNumber).executeAndFetch(Account.class);
			if (accounts == null || accounts.isEmpty()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean checkAccountBalance(Transfer transfer) {

		Account account = getBalanceStatement(transfer.getFromAccount());
		if (account == null || account.getBalance().compareTo(transfer.getAmount()) < 0) {
			return false;
		}
		return true;

	}

	@Override
	public Account transferMoney(Transfer transfer) {

		Account account = null;
		try (Connection connection = databaseConnection.beginTransaction()) {

			Account fromAccount = connection.createQuery(SELECT_ACCOUNT_QUERY)
					.addParameter(AccountConstants.ACC_NUMBER, transfer.getFromAccount()).executeAndFetch(Account.class)
					.get(0);

			Account toAccount = connection.createQuery(SELECT_ACCOUNT_QUERY)
					.addParameter(AccountConstants.ACC_NUMBER, transfer.getToAccount()).executeAndFetch(Account.class)
					.get(0);

			BigDecimal fromAccBalance = fromAccount.getBalance().subtract(transfer.getAmount());

			connection.createQuery(UPDATE_BALANCE_QUERY)
					.addParameter(AccountConstants.ACC_NUMBER, transfer.getFromAccount())
					.addParameter(AccountConstants.ACC_BALANCE, fromAccBalance).executeUpdate();

			BigDecimal toAccBalance = toAccount.getBalance().add(transfer.getAmount());

			connection.createQuery(UPDATE_BALANCE_QUERY)
					.addParameter(AccountConstants.ACC_NUMBER, transfer.getToAccount())
					.addParameter(AccountConstants.ACC_BALANCE, toAccBalance).executeUpdate();

			fromAccount.setBalance(fromAccBalance);
			account = fromAccount;
			connection.commit();
		}

		return account;
	}
}
