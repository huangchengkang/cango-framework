package com.cangoframework.dbpool;

import java.util.Properties;

public class PoolConfig {
	private Properties dataSourceProperties;
	
	/**连接池名称*/
	private String poolName;
	
	private String driverClass;
	private String jdbcUrl;
	private String userName;
	private String password;
	
	/**获取连接时最大等待时间，单位毫秒*/
	private int maxWait = 1000;
	/**最大的活动连接（连接池的最大数）*/
	private int maxActive = 10;
	/**最大空闲连接*/
	private int maxIdle = 10;
	/**最小空闲连接*/
	private int minIdle = 1;
	/**连接池初始化大小*/
	private int initialSize = 1;
	
	public Properties getDataSourceProperties() {
		return dataSourceProperties;
	}
	public void setDataSourceProperties(Properties dataSourceProperties) {
		this.dataSourceProperties = dataSourceProperties;
		parseDataSourceProperties(this.dataSourceProperties);
	}
	
	private void parseDataSourceProperties(Properties p) {
		setDriverClass(p.getProperty("driver", "com.mysql.jdbc.Driver"));
		setJdbcUrl(p.getProperty("url", "jdbc:mysql://localhost:3306/mysql"));
		setUserName(p.getProperty("user", "root"));
		setPassword(p.getProperty("password", "root"));
		
		setMaxActive(Integer.parseInt(p.getProperty("maxActive", String.valueOf(getMaxActive()))));
		setMaxWait(Integer.parseInt(p.getProperty("maxWait", String.valueOf(getMaxWait()))));
		setMaxIdle(Integer.parseInt(p.getProperty("maxIdle", String.valueOf(getMaxIdle()))));
		setMinIdle(Integer.parseInt(p.getProperty("minIdle", String.valueOf(getMinIdle()))));
		setInitialSize(Integer.parseInt(p.getProperty("initialSize", String.valueOf(getInitialSize()))));
		
	}
	public String getPoolName() {
		return poolName;
	}
	public void setPoolName(String poolName) {
		this.poolName = poolName;
	}
	public String getDriverClass() {
		return driverClass;
	}
	public void setDriverClass(String driverClass) {
		this.driverClass = driverClass;
	}
	public String getJdbcUrl() {
		return jdbcUrl;
	}
	public void setJdbcUrl(String jdbcUrl) {
		this.jdbcUrl = jdbcUrl;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public int getMaxWait() {
		return maxWait;
	}
	public void setMaxWait(int maxWait) {
		this.maxWait = maxWait;
	}
	public int getMaxActive() {
		return maxActive;
	}
	public void setMaxActive(int maxActive) {
		this.maxActive = maxActive;
	}
	public int getMaxIdle() {
		return maxIdle;
	}
	public void setMaxIdle(int maxIdle) {
		this.maxIdle = maxIdle;
	}
	public int getMinIdle() {
		return minIdle;
	}
	public void setMinIdle(int minIdle) {
		this.minIdle = minIdle;
	}
	public int getInitialSize() {
		return initialSize;
	}
	public void setInitialSize(int initialSize) {
		this.initialSize = initialSize;
	}
	
}
