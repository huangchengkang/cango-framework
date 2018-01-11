package com.cangoframework.dbpool;

import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.sql.DataSource;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import com.cangoframework.dbpool.bonecp.BoneCPDataSourceProvider;
import com.cangoframework.dbpool.c3p0.C3p0DataSourceProvider;
import com.cangoframework.dbpool.dbcp.DBCPDataSourceProvider;
import com.cangoframework.dbpool.druid.DruidDataSourceProvider;
import com.cangoframework.dbpool.hikaricp.HikariCPDataSourceProvider;
import com.cangoframework.dbpool.proxool.ProxoolDataSourceProvider;

public class DataSourceFactory {
	private static Map<String, DataSource> dataSources = new HashMap<String, DataSource>();
	private String resourceFile;
	private String defaultDataSource;
	
	public DataSourceFactory(String resourceFile){
		try {
			this.resourceFile = resourceFile;
			initConnectionFactory();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public Connection getConnection() throws SQLException {
		return getConnection(defaultDataSource);
	}
	public Connection getConnection(String dataSourceName) throws SQLException {
		if (isEmpty(dataSourceName))
			throw new SQLException("Database name is null!");
		DataSource dataSource = (DataSource) dataSources.get(dataSourceName);
		if(dataSource==null)
			throw new SQLException("The specified connection datasource["+dataSourceName+"] could not be found!");
		return dataSource.getConnection();
	}
	public DataSource getDataSource(){
		return getDataSource(defaultDataSource);
	}
	public DataSource getDataSource(String dataSourceName){
		return (DataSource) dataSources.get(dataSourceName);
	}
	public Set<String> getDataSourceNames(){
		return dataSources.keySet();
	}

	public void shutdown() {
		Iterator<DataSource> localIterator = dataSources.values().iterator();
		while (localIterator.hasNext()) {
			DataSource dataSource = localIterator.next();
			if(dataSource == null){
				continue;
			}
			Class<? extends DataSource> dataSourceClass = dataSource.getClass();
			try {
				Method closeMethod = dataSourceClass.getMethod("close");
				closeMethod.invoke(dataSource);
			} catch (NoSuchMethodException e) {
				//no do something ...
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		dataSources.clear();
	}
	
	public static void main(String[] args) throws SQLException {
		DataSourceFactory factory = new DataSourceFactory("classpath:dbconfig.xml");
		System.out.println(factory.getConnection());
		factory.shutdown();
	}
	
	private void initConnectionFactory() throws Exception {
		
		SAXBuilder builder = new SAXBuilder();
		InputStream in = null;
		if(resourceFile.startsWith("classpath:")){
			in = DataSourceFactory.class.getClassLoader().getResourceAsStream(
					resourceFile.substring(10));
		}else{
			in = new FileInputStream(resourceFile);
		}
		Document doc = builder.build(in);
		
		//Set defaultDataSource 
		defaultDataSource = doc.getRootElement().getChildText("defaultDataSource");
		
		Element resources = doc.getRootElement().getChild("resources");
		List<?> resourceList = resources.getChildren("resource");
		for (int i = 0; i < resourceList.size(); i++) {
			Element resource = (Element) resourceList.get(i);
			
			//Load data source properties
			if(!"true".equals(resource.getAttributeValue("available", "false").trim().toLowerCase()))
				continue;
			
			if(!"jdbc".equals(resource.getAttributeValue("type", "").trim().toLowerCase())) 
				throw new Exception("DataSource type only support jdbc ... ");

			String dataSourceName = resource.getAttributeValue("name","");
			if(isEmpty(dataSourceName)) 
				throw new Exception("DataSource name is Empty ... ");
			
			//Load connection parameters
			List<?> connParam = resource.getChildren();
			Properties dataSourceProperties = new Properties();
			Element element = null;
			for (int j = 0; j < connParam.size(); j++) {
				
				element = (Element) connParam.get(j);
				String elementName = element.getName();
				String elementValue = element.getTextTrim();
				dataSourceProperties.put(elementName, elementValue);
			}
			//Instantiated data source
			addDataSource(dataSourceName, dataSourceProperties);
		}
		
	}
	
	/**
	 * @param dataSourceName
	 * @param dataSourceProperties
	 * @throws Exception
	 */
	private void addDataSource(String dataSourceName, Properties dataSourceProperties)throws Exception {
		Map<String, Class> providerMap = new LinkedHashMap<String, Class>();
		providerMap.put("org.apache.commons.dbcp.BasicDataSource", DBCPDataSourceProvider.class);
		providerMap.put("com.mchange.v2.c3p0.ComboPooledDataSource", C3p0DataSourceProvider.class);
		providerMap.put("com.alibaba.druid.pool.DruidDataSource", DruidDataSourceProvider.class);
		providerMap.put("com.jolbox.bonecp.BoneCPDataSource", BoneCPDataSourceProvider.class);
		providerMap.put("com.zaxxer.hikari.HikariDataSource", HikariCPDataSourceProvider.class);
		providerMap.put("org.logicalcobwebs.proxool.ProxoolDataSource", ProxoolDataSourceProvider.class);
		Set<String> dataSourceClassNames = providerMap.keySet();
		
		for (String dataSourceClassName : dataSourceClassNames) {
			if(existDataSourceClass(dataSourceClassName)){
				Class dataSourceProviderClass = providerMap.get(dataSourceClassName);
				DataSourceProvider dataSourceProvider = (DataSourceProvider) dataSourceProviderClass.newInstance();
				DataSource dataSource = dataSourceProvider.getDataSource(dataSourceName, dataSourceProperties);
				Connection connection = dataSource.getConnection();
				if(connection!=null){
					connection.close();
					dataSources.put(dataSourceName, dataSource);
				}
				break;
			}else{
				if("org.logicalcobwebs.proxool.ProxoolDataSource".equals(dataSourceClassName)){
					throw new Exception("There is no available connection pool!");
				}
			}
		}
	}
	
	private boolean existDataSourceClass(String dataSourceClassName) {
		try {
			Class.forName(dataSourceClassName);
			return true;
		} catch (ClassNotFoundException e) {
			return false;
		}
	}

	private static boolean isEmpty(String value){
		return value==null||"".equals(value.trim());
	}
}
