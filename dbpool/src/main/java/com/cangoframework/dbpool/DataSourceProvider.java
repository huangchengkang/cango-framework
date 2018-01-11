package com.cangoframework.dbpool;

import java.util.Properties;

import javax.sql.DataSource;

public abstract class DataSourceProvider {
	private String dataSourceName;
	public DataSource getDataSource(String dataSourceName, Properties dataSourceProperties){
		this.dataSourceName = dataSourceName;
		PoolConfig poolConfig = new PoolConfig();
		poolConfig.setPoolName(dataSourceName);
		poolConfig.setDataSourceProperties(dataSourceProperties);
		return getDataSource(poolConfig);
	}
	
	public abstract DataSource getDataSource(PoolConfig poolConfig);

	@Override
	public String toString() {
		return getClass().getName() + "DataSource["+dataSourceName+"] Provider ";
	}
	
}
