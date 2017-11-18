package com.trendyol.reader;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import com.mysql.jdbc.PreparedStatement;
import com.trendyol.model.Configuration;

public class ConfigurationReader {

	private static final String DEFAULT_USERNAME = "root";
	private static final String DEFAULT_PASSWORD = "root";
	private static final String DEFAULT_JDBC_DRIVER = "com.mysql.jdbc.Driver";
	private static final String SELECT_QUERY = "SELECT id, name, type, value "
			+ "FROM configuration WHERE applicationname = ? AND isactive = ?";
	private static final String INSERT_QUERY = "INSERT INTO configuration VALUES (?, ?, ?, ?, ?, ?)";

	private String applicationName;
	private Connection connection;
	private Timer timer;
	private Map<String, Configuration> configurationMap;

	public ConfigurationReader(String applicationName, String connectionString,
			long refreshTimerIntervalInMs) throws ClassNotFoundException,
			SQLException {
		this(applicationName, connectionString, null, null,
				refreshTimerIntervalInMs);
	}

	public ConfigurationReader(String applicationName, String connectionString,
			String userName, String password, long refreshTimerIntervalInMs)
			throws ClassNotFoundException, SQLException {
		this(applicationName, connectionString, userName, password, null,
				refreshTimerIntervalInMs);
	}

	public ConfigurationReader(String applicationName, String connectionString,
			String userName, String password, String jdbcDriver,
			long refreshTimerIntervalInMs) throws ClassNotFoundException,
			SQLException {

		this.applicationName = applicationName;

		connection = getConnection(connectionString,
				userName != null ? userName : DEFAULT_USERNAME,
				password != null ? password : DEFAULT_PASSWORD,
				jdbcDriver != null ? jdbcDriver : DEFAULT_JDBC_DRIVER);

		configurationMap = readConfigurationsFromDatabase();

		readConfigurations(refreshTimerIntervalInMs);
	}

	public Connection getConnection(String url, String userName,
			String password, String jdbcDriver) throws ClassNotFoundException,
			SQLException {

		Class.forName(jdbcDriver);
		Connection connection = DriverManager.getConnection(url, userName,
				password);
		return connection;

	}

	public Map<String, Configuration> readConfigurationsFromDatabase()
			throws SQLException {

		PreparedStatement stmt = (PreparedStatement) connection
				.prepareStatement(SELECT_QUERY);
		stmt.setString(1, applicationName);
		stmt.setBoolean(2, true);

		ResultSet rs = stmt.executeQuery();

		Map<String, Configuration> confMap = new HashMap<String, Configuration>();
		Configuration conf;
		if (rs.next()) {
			conf = new Configuration();
			conf.setID(rs.getInt("id"));
			conf.setName(rs.getString("name"));
			conf.setType(rs.getString("type"));
			conf.setValue(rs.getString("value"));
			conf.setApplicationName(applicationName);
			conf.setIsActive(true);
			confMap.put(conf.getName(), conf);
		}
		return confMap;

	}

	public void readConfigurations(long refreshTimerIntervalInMs) {

		timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {

				try {
					configurationMap = readConfigurationsFromDatabase();
				} catch (SQLException e) {
					e.printStackTrace();
				}

			}
		}, 0, refreshTimerIntervalInMs);

	}

	/**
	 * Java is not a type safe language so we need to check for each
	 * configuration type for casting.
	 * 
	 * @param key
	 * @param cls
	 * @return
	 * @throws Exception
	 */
	public <T> T getValue(String key, Class<T> cls) throws Exception {

		if (configurationMap == null || configurationMap.isEmpty()) {
			return null;
		}

		T value = null;

		Configuration conf = configurationMap.get(key);

		if (conf == null) {
			return null;
		}

		if ("String".equalsIgnoreCase(conf.getType())) {
			value = (T) conf.getValue();
		} else if ("Integer".equalsIgnoreCase(conf.getType())
				|| "Int".equalsIgnoreCase(conf.getType())) {
			value = (T) Integer.valueOf(conf.getValue());
		} else if ("Double".equalsIgnoreCase(conf.getType())) {
			value = (T) Double.valueOf(conf.getValue());
		} else if ("Boolean".equalsIgnoreCase(conf.getType())) {
			if ("0".equals(conf.getValue())) {
				value = (T) Boolean.FALSE;
			} else {
				value = (T) Boolean.TRUE;
			}
		} else {
			throw new Exception("Unknown type!");
		}

		return value;

	}

	public void addConfiguration(Integer id, String name, String type,
			String value, Boolean isActive) throws SQLException {

		PreparedStatement stmt = (PreparedStatement) connection
				.prepareStatement(INSERT_QUERY);
		stmt.setInt(1, id);
		stmt.setString(2, name);
		stmt.setString(3, type);
		stmt.setString(4, value);
		stmt.setBoolean(5, isActive);
		stmt.setString(6, applicationName);

		stmt.executeUpdate();
	}

	public void stopReading() {
		if (timer != null) {
			timer.cancel();
		}
	}
}
