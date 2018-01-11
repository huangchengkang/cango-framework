package com.cangoframework.dbpool.bonecp;

import java.util.Properties;

import javax.sql.DataSource;

import com.cangoframework.dbpool.DataSourceProvider;
import com.cangoframework.dbpool.PoolConfig;
import com.jolbox.bonecp.BoneCPDataSource;

public class BoneCPDataSourceProvider extends DataSourceProvider{

	@Override
	public DataSource getDataSource(PoolConfig poolConfig) {
		BoneCPDataSource dataSource = new BoneCPDataSource();
		dataSource.setPoolName(poolConfig.getPoolName());
		dataSource.setDriverClass(poolConfig.getDriverClass());
		dataSource.setJdbcUrl(poolConfig.getJdbcUrl());
		dataSource.setUsername(poolConfig.getUserName());
		dataSource.setPassword(poolConfig.getPassword());
		try {
			Properties dataSourceProperties = new Properties();
			dataSourceProperties.putAll(poolConfig.getDataSourceProperties());
			dataSourceProperties.put("poolName", poolConfig.getPoolName());
			dataSourceProperties.put("driverClass", poolConfig.getDriverClass());
			dataSourceProperties.put("jdbcUrl", poolConfig.getJdbcUrl());
			dataSourceProperties.put("username", poolConfig.getUserName());
			dataSourceProperties.put("password", poolConfig.getPassword());
			dataSource.setProperties(dataSourceProperties);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dataSource;
	}

}
