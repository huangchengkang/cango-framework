package com.cangoframework.dbpool.druid;

import javax.sql.DataSource;

import com.alibaba.druid.pool.DruidDataSource;
import com.cangoframework.dbpool.DataSourceProvider;
import com.cangoframework.dbpool.PoolConfig;

public class DruidDataSourceProvider extends DataSourceProvider{

	@Override
	public DataSource getDataSource(PoolConfig poolConfig) {
		DruidDataSource dataSource = new DruidDataSource();
		dataSource.setName(poolConfig.getPoolName());
		dataSource.setDriverClassName(poolConfig.getDriverClass());
		dataSource.setUrl(poolConfig.getJdbcUrl());
		dataSource.setUsername(poolConfig.getUserName());
		dataSource.setPassword(poolConfig.getPassword());
		
		dataSource.setMaxWait(poolConfig.getMaxWait());
		dataSource.setMaxActive(poolConfig.getMaxActive());
		//dataSource.setMaxIdle(poolConfig.getMaxIdle());
		dataSource.setMinIdle(poolConfig.getMinIdle());
		dataSource.setInitialSize(poolConfig.getInitialSize());
		
		return dataSource;
	}

}