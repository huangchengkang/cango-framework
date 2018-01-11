package com.cangoframework.dbpool.dbcp;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;

import com.cangoframework.dbpool.DataSourceProvider;
import com.cangoframework.dbpool.PoolConfig;

public class DBCPDataSourceProvider extends DataSourceProvider {

	@Override
	public DataSource getDataSource(PoolConfig poolConfig) {
		
		BasicDataSource dataSource = new BasicDataSource();
		
		dataSource.setDriverClassName(poolConfig.getDriverClass());
		dataSource.setUrl(poolConfig.getJdbcUrl());
		dataSource.setUsername(poolConfig.getUserName());
		dataSource.setPassword(poolConfig.getPassword());
		
		dataSource.setMaxActive(poolConfig.getMaxActive());
		dataSource.setMaxIdle(poolConfig.getMaxIdle());
		dataSource.setMinIdle(poolConfig.getMinIdle());
		dataSource.setInitialSize(poolConfig.getInitialSize());
		dataSource.setMaxWait(poolConfig.getMaxWait());
		return dataSource;
	}

}
