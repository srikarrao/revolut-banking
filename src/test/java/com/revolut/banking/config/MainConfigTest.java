package com.revolut.banking.config;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;

public class MainConfigTest {

	private MainConfig mainConfig;

	@Before
	public void setUp() throws Exception {
		mainConfig = MainConfig.getInstance();
	}

	@Test
	public void testConfig() {
		mainConfig.config();

		Map<Object, Object> applicationProperties = mainConfig.getApplicationProperties();
		assertNotNull("Application Properties in not null", applicationProperties);
		assertNotEquals(0, applicationProperties.size());
	}
}