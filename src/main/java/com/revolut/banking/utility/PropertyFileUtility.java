package com.revolut.banking.utility;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertyFileUtility {

	private static final Logger logger = LoggerFactory.getLogger(PropertyFileUtility.class);
	private static final PropertyFileUtility _THIS = new PropertyFileUtility();

	public static Properties getApplicationProperties(String fileName) {
		Objects.requireNonNull(fileName, "fileName");
		Properties properties = new Properties();
		loadPropertiesFile(properties, fileName);
		return properties;
	}

	public static Map<Object, Object> convertPropertiesToMap(Properties properties) {
		Objects.requireNonNull(properties, "properties");
		return properties.entrySet().stream()
				.collect(Collectors.toMap(e -> e.getKey().toString(), e -> e.getValue().toString()));
	}

	public static Map<Object, Object> getApplicationPropertiesAsMap(String fileName) {
		Properties properties = new Properties();
		loadPropertiesFile(properties, fileName);
		return properties.entrySet().stream()
				.collect(Collectors.toMap(e -> e.getKey().toString(), e -> e.getValue().toString()));
	}

	public static void loadPropertiesFile(Properties properties, String fileName) {
		try (InputStream input = _THIS.getClass().getClassLoader().getResourceAsStream(fileName)) {
			properties.load(input);
		} catch (IOException ioException) {
			logger.error("Exception reading properties from file: {}", fileName, ioException);
		}
	}
}
