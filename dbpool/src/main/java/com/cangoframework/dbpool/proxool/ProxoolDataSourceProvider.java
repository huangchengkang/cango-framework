package com.cangoframework.dbpool.proxool;

import javax.sql.DataSource;

import org.logicalcobwebs.proxool.ProxoolDataSource;

import com.cangoframework.dbpool.DataSourceProvider;
import com.cangoframework.dbpool.PoolConfig;

public class ProxoolDataSourceProvider extends DataSourceProvider{

	@Override
	public DataSource getDataSource(PoolConfig poolConfig) {
		ProxoolDataSource dataSource = new ProxoolDataSource();
		dataSource.setAlias(poolConfig.getPoolName());
		dataSource.setDriver(poolConfig.getDriverClass());
		dataSource.setPassword(poolConfig.getPassword());
		dataSource.setUser(poolConfig.getUserName());
		
		String jdbcUrl = poolConfig.getJdbcUrl();
		if(jdbcUrl!=null){
			jdbcUrl = jdbcUrl.trim();
			if(jdbcUrl.endsWith("?")||jdbcUrl.endsWith("&")||jdbcUrl.endsWith("&amp;")){
				jdbcUrl += "user="+poolConfig.getUserName()
				+"&password="+poolConfig.getPassword();
			}else if(jdbcUrl.contains("?")){
				jdbcUrl += "&user="+poolConfig.getUserName()
				+"&password="+poolConfig.getPassword();
			}else if(!jdbcUrl.contains("?")){
				jdbcUrl += "?user="+poolConfig.getUserName()
				+"&password="+poolConfig.getPassword();
			}
		}
		dataSource.setDriverUrl(jdbcUrl);
		dataSource.setMaximumConnectionCount(poolConfig.getMaxActive()+5);
		dataSource.setMinimumConnectionCount(poolConfig.getMinIdle());
		return dataSource;
	}

}