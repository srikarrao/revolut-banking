package com.revolut.banking.config;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;

public class DatabaseConfigTest {

	private DatabaseConfig dbConfig;

	@Before
	public void setUp() throws Exception {
		dbConfig = DatabaseConfig.getInstance();
	}

	@Test
	public void testDBProperties() {
		Map<Object, Object> databaseProperties = dbConfig.getDatabaseProperties();
		assertNotNull("Database Properties in not null", databaseProperties);
		assertNotEquals(0, databaseProperties.size());
	}

	@Test
	public void testDBConnection() {
		assertNotNull("Database Connection in not null", dbConfig.getDBConnection());
	}

}
