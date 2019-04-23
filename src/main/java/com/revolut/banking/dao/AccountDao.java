package com.revolut.banking.dao;

import com.revolut.banking.model.Account;
import com.revolut.banking.model.Transfer;

public interface AccountDao {

	Account create(Account account);

	Account getBalanceStatement(Long accNumber);

	Account transferMoney(Transfer transfer);

	boolean isAccNumberValid(Long accNumber);
	boolean checkAccountBalance(Transfer amount);

}