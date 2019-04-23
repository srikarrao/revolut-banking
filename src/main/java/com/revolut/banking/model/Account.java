package com.revolut.banking.model;

import java.math.BigDecimal;

public class Account {

	private Long accNumber;
	private BigDecimal balance;

	public Long getAccNumber() {
		return accNumber;
	}

	public void setAccNumber(Long accNumber) {
		this.accNumber = accNumber;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}
}