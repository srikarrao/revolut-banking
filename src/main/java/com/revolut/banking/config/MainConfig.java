package com.revolut.banking.config;

import static spark.SparkBase.port;
import static spark.SparkBase.threadPool;

import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.revolut.banking.utility.PropertyFileUtility;
import com.revolut.banking.utility.ShutdownHook;

import spark.Spark;

public class MainConfig {

	private static final Logger logger = LoggerFactory.getLogger(MainConfig.class);

	private final int DEFAULT_SERVICE_PORT = 8080;
	private final String APPLICATION_PROPERTIES_FILE = "application.properties";
	private final int DEFAULT_THREAD_COUNT = Runtime.getRuntime().availableProcessors();

	private static final MainConfig INSTANCE = new MainConfig();

	private final Map<Object, Object> APPLICATION_PROPERTIES;

	private MainConfig() {
		APPLICATION_PROPERTIES = PropertyFileUtility.getApplicationPropertiesAsMap(APPLICATION_PROPERTIES_FILE);
	}

	public static synchronized MainConfig getInstance() {
		return INSTANCE;
	}

	public void config() {
		logger.info("Configuring the Revolut Banking Application..");
		configServer();
	}

	private void configServer() {
		Optional.ofNullable(APPLICATION_PROPERTIES.get("server.threads")).ifPresentOrElse(
				threadCount -> threadPool(Integer.parseInt((String) threadCount)), () -> port(DEFAULT_THREAD_COUNT));

		Optional.ofNullable(APPLICATION_PROPERTIES.get("server.port")).ifPresentOrElse(
				serverPort -> port(Integer.parseInt((String) serverPort)), () -> port(DEFAULT_SERVICE_PORT));

		ShutdownHook.addShutDownHook(new Thread(Spark::stop));
	}

	public Map<Object, Object> getApplicationProperties() {
		return APPLICATION_PROPERTIES;
	}
}