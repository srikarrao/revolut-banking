package com.revolut.banking.utility;

import org.apache.commons.math3.random.RandomDataGenerator;

/**
 * This generates an account number for a new account, limited to this
 * application. Reduced the probability of account numbers collision by having a
 * bigger UP_LIMIT. Ideally it can be either auto_incremented in the database or
 * delegate it to another service that only does unique id generation.
 *
 * @author srikarrao
 *
 */
public class UniqueAccountNumberGenerator {

	private static final long ACC_NUMBER_UP_LIMIT = 1001L;
	private static final long ACC_NUMBER_DOWN_LIMIT = 999999L;

	public static long generateRandomAccNumber() {
		return new RandomDataGenerator().nextLong(ACC_NUMBER_UP_LIMIT, ACC_NUMBER_DOWN_LIMIT);
	}
}
