package com.cangoframework.dbpool.hikaricp;

import javax.sql.DataSource;

import com.cangoframework.dbpool.DataSourceProvider;
import com.cangoframework.dbpool.PoolConfig;
import com.zaxxer.hikari.HikariDataSource;

public class HikariCPDataSourceProvider extends DataSourceProvider{

	@Override
	public DataSource getDataSource(PoolConfig poolConfig) {
		HikariDataSource dataSource = new HikariDataSource();
		dataSource.setPoolName(poolConfig.getPoolName());
		dataSource.setDriverClassName(poolConfig.getDriverClass());
		dataSource.setJdbcUrl(poolConfig.getJdbcUrl());
		dataSource.setUsername(poolConfig.getUserName());
		dataSource.setPassword(poolConfig.getPassword());
		//���������
		dataSource.setMaximumPoolSize(poolConfig.getMaxActive()+5);
		dataSource.setMinimumIdle(poolConfig.getMinIdle());
		return dataSource;
	}

}
