package com.cangoframework.dbpool.c3p0;

import javax.sql.DataSource;

import com.cangoframework.dbpool.DataSourceProvider;
import com.cangoframework.dbpool.PoolConfig;
import com.mchange.v2.c3p0.ComboPooledDataSource;

public class C3p0DataSourceProvider extends DataSourceProvider{

	@Override
	public DataSource getDataSource(PoolConfig poolConfig) {
		ComboPooledDataSource dataSource = null;
		try {
			dataSource = new ComboPooledDataSource();
			dataSource.setDataSourceName(poolConfig.getPoolName());
			dataSource.setDriverClass(poolConfig.getDriverClass());
			dataSource.setJdbcUrl(poolConfig.getJdbcUrl());
			dataSource.setUser(poolConfig.getUserName());
			dataSource.setPassword(poolConfig.getPassword());
			dataSource.setInitialPoolSize(poolConfig.getInitialSize());
			dataSource.setMaxPoolSize(poolConfig.getMaxActive()+5);
			dataSource.setMinPoolSize(poolConfig.getMinIdle());
			dataSource.getConnection().close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dataSource;
	}

}
