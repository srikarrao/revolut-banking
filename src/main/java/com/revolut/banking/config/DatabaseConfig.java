package com.revolut.banking.config;

import static com.wix.mysql.EmbeddedMysql.anEmbeddedMysql;
import static com.wix.mysql.ScriptResolver.classPathScript;
import static com.wix.mysql.config.Charset.UTF8;
import static com.wix.mysql.config.MysqldConfig.aMysqldConfig;
import static com.wix.mysql.distribution.Version.v5_7_latest;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sql2o.Sql2o;

import com.revolut.banking.utility.PropertyFileUtility;
import com.revolut.banking.utility.ShutdownHook;
import com.wix.mysql.EmbeddedMysql;
import com.wix.mysql.EmbeddedMysql.Builder;
import com.wix.mysql.config.MysqldConfig;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class DatabaseConfig {

	private static final Logger logger = LoggerFactory.getLogger(DatabaseConfig.class);

	private static final String DB_PROPERTIES_FILE = "db.properties";
	private final Map<Object, Object> DB_PROPERTIES;

	private static final String DATABASE_NAME = "revolut_banking";
	private static final String DB_INIT_SCRIPT = "DB_INIT_SCRIPT.sql";

	private static final DatabaseConfig INSTANCE = new DatabaseConfig();
	private final Sql2o sql2o;

	private DatabaseConfig() {
		Properties dbProperties = PropertyFileUtility.getApplicationProperties(DB_PROPERTIES_FILE);
		DB_PROPERTIES = PropertyFileUtility.convertPropertiesToMap(dbProperties);
		setUpInMemoryDatabase();
		sql2o = new Sql2o(createDataSource(dbProperties));
	}

	public static synchronized DatabaseConfig getInstance() {
		return INSTANCE;
	}

	private void setUpInMemoryDatabase() {
		try {
			String username = DB_PROPERTIES.get("username").toString();
			String password = DB_PROPERTIES.get("password").toString();

			MysqldConfig config = aMysqldConfig(v5_7_latest).withCharset(UTF8).withPort(3306)
					.withUser(username, password).withTimeout(2, TimeUnit.MINUTES).build();

			Builder builder = anEmbeddedMysql(config).addSchema(DATABASE_NAME, classPathScript(DB_INIT_SCRIPT));
			EmbeddedMysql embeddedMysqlD = builder.start();
			ShutdownHook.addShutDownHook(new Thread(() -> embeddedMysqlD.stop()));
		} catch (Exception e) {
			logger.info("Exception occurred while setting up In-Memory Dabatase. ", e);
		}
	}

	private HikariDataSource createDataSource(Properties properties) {
		return new HikariDataSource(new HikariConfig(properties));
	}

	public Sql2o getDBConnection() {
		return sql2o;
	}

	public Map<Object, Object> getDatabaseProperties() {
		return DB_PROPERTIES;
	}
}