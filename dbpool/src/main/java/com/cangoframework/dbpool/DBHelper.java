package com.cangoframework.dbpool;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.dbutils.BasicRowProcessor;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.RowProcessor;
import org.apache.commons.dbutils.handlers.ArrayHandler;
import org.apache.commons.dbutils.handlers.ArrayListHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ColumnListHandler;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import com.cangoframework.dbpool.annotation.Colunm;
import com.cangoframework.dbpool.annotation.Primary;
import com.cangoframework.dbpool.annotation.Table;

public class DBHelper {
	
	private static DataSourceFactory dataSourceFactory = new DataSourceFactory(System.getProperty("dbconfig", "classpath:dbconfig.xml"));
	private static final RowProcessor rowProcessor = new BasicRowProcessor();
	public static Connection getConnection() throws SQLException{
		Connection connection = dataSourceFactory.getConnection();
		return connection;
	}
	public static Connection getConnection(String dataSourceName) throws SQLException{
		Connection connection = dataSourceFactory.getConnection(dataSourceName);
		return connection;
	}
	public static DataSource getDataSource(){
		return dataSourceFactory.getDataSource();
	}
	public static DataSource getDataSource(String dataSourceName){
		return dataSourceFactory.getDataSource(dataSourceName);
	}
	private static boolean singleton = true;
	public static void setSingleton(boolean isSingleton) {
		singleton = isSingleton;
	}
	private static QueryRunner queryRunner;
	public static QueryRunner getQueryRunner() {
		if(singleton || queryRunner == null){
			queryRunner = new QueryRunner();
		}
		return connectionQueryRunner;
	}
	private static QueryRunner connectionQueryRunner;
	public static QueryRunner getConnectionQueryRunner(String dataSourceName) {
		if(singleton || connectionQueryRunner == null){
			connectionQueryRunner = new QueryRunner(getDataSource(dataSourceName));
		}
		return connectionQueryRunner;
	}
	private static QueryRunner defaultConnectionQueryRunner;
	public static QueryRunner getConnectionQueryRunner() {
		if(singleton || defaultConnectionQueryRunner == null){
			defaultConnectionQueryRunner = new QueryRunner(getDataSource());
		}
		return defaultConnectionQueryRunner;
	}
	
    /**
     * 实现ResultSetHandler 查询
     * @param conn
     * @param sql
     * @param rsh
     * @throws SQLException 
     * 备注：资源自动关闭
     */
    public static <T> T query(Connection conn,String sql, ResultSetHandler<T> rsh) throws SQLException {
    	QueryRunner runner = getQueryRunner();
    	return runner.query(conn, sql, rsh);
    }
    /**
     * 实现ResultSetHandler 查询
     * @param conn
     * @param sql
     * @param rsh
     * @param params
     * @throws SQLException 
     */
    public static <T> T query(Connection conn,String sql, ResultSetHandler<T> rsh,Object ... params) throws SQLException {
    	QueryRunner runner = getQueryRunner();
		return runner.query(conn, sql, rsh, params);
    }
    public static <T> T query(String dataSourceName,String sql, ResultSetHandler<T> rsh,Object ... params) throws SQLException {
    	QueryRunner runner = getConnectionQueryRunner(dataSourceName);
    	return runner.query(sql, rsh, params);
    }
    public static <T> T query(String sql, ResultSetHandler<T> rsh,Object ... params) throws SQLException {
    	QueryRunner runner = getConnectionQueryRunner();
    	return runner.query(sql, rsh, params);
    }
    
    /** 
     * 得到查询记录的条数 
     * @param <T>
     *  
     * @param sql 
     *            必须为select count(*) from t_user的格式 
     * @return 
     * @throws SQLException 
     */  
    public static <T> int getCount(Connection connection, String sql) throws SQLException {
    	QueryRunner runner = getQueryRunner();
        Object value = runner.query(connection, sql, new ScalarHandler<T>());
        return objectToInteger(value);
    }
  
    /** 
     * 得到查询记录的条数 
     * @param <T>
     *  
     * @param sql 
     *            必须为select count(*) from t_user的格式 
     * @param params 
     * @return 
     * @throws SQLException 
     */  
    public static <T> int getCount(Connection connection, String sql, Object... params) throws SQLException {
    	 QueryRunner runner = getQueryRunner();
         Object value = runner.query(connection, sql, new ScalarHandler<T>(), params);
         return objectToInteger(value);
    }
    public static <T> int getCount(String dataSourceName,String sql, Object... params) throws SQLException {
    	QueryRunner runner = getConnectionQueryRunner(dataSourceName);
    	Object value = runner.query(sql, new ScalarHandler<T>(), params);
    	return objectToInteger(value);
    }
    public static <T> int getCount(String sql, Object... params) throws SQLException {
    	QueryRunner runner = getConnectionQueryRunner();
    	Object value = runner.query(sql, new ScalarHandler<T>(), params);
    	return objectToInteger(value);
    }
  
    /** 
     * 根据传入的sql，查询记录，以数组形式返回第一行记录。 注意：如果有多行记录，只会返回第一行，所以适用场景需要注意，可以使用根据主键来查询的场景 
     *  
     * @param sql 
     * @return 
     * @throws SQLException 
     */  
    public static Object[] getFirstRowArray(Connection connection, String sql) throws SQLException {
    	QueryRunner runner = getQueryRunner();
        return runner.query(connection, sql, new ArrayHandler());
    }
  
    /** 
     * 根据传入的sql，查询记录，以数组形式返回第一行记录。 注意：如果有多行记录，只会返回第一行，所以适用场景需要注意，可以使用根据主键来查询的场景 
     *  
     * @param sql 
     * @param params 
     * @return 
     * @throws SQLException 
     */  
    public static Object[] getFirstRowArray(Connection connection, String sql, Object... params) throws SQLException {
    	QueryRunner runner = getQueryRunner();
        return runner.query(connection, sql, new ArrayHandler(), params);
    }
    public static Object[] getFirstRowArray(String dataSourceName, String sql, Object... params) throws SQLException {
    	QueryRunner runner = getConnectionQueryRunner(dataSourceName);
    	return runner.query(sql, new ArrayHandler(), params);
    }
    public static Object[] getFirstRowArray(String sql, Object... params) throws SQLException {
    	QueryRunner runner = getConnectionQueryRunner();
    	return runner.query(sql, new ArrayHandler(), params);
    }
    
    /**
     * 在结果集中抽出某个字段的结果集
     * @param conn
     * @param sql
     * @param columnName
     * @param type
     * @return
     * @throws SQLException 
     */
    public static <T> List<T> getColumnList(Connection conn,String sql,String columnName,Class<T> type) throws SQLException{
    	QueryRunner runner = getQueryRunner();
    	return runner.query(conn,sql, new ColumnListHandler<T>(columnName));
    }
    public static <T> List<T> getColumnList(Connection conn,String sql,String columnName,Class<T> type, Object... params) throws SQLException{
    	QueryRunner runner = getQueryRunner();
    	return runner.query(conn,sql, new ColumnListHandler<T>(columnName),params);
    }
    public static <T> List<T> getColumnList(String dataSourceName, String sql,String columnName,Class<T> type, Object... params) throws SQLException{
    	QueryRunner runner = getConnectionQueryRunner(dataSourceName);
    	return runner.query(sql, new ColumnListHandler<T>(columnName),params);
    }
    public static <T> List<T> getColumnList(String sql,String columnName,Class<T> type, Object... params) throws SQLException{
    	QueryRunner runner = getConnectionQueryRunner();
    	return runner.query(sql, new ColumnListHandler<T>(columnName),params);
    }
  
    /** 
     * 根据sql查询返回所有记录，以List数组形式返回 
     *  
     * @param sql 
     * @return 
     * @return 
     * @throws SQLException 
     */  
    public static List<Object[]> getListArray(Connection connection, String sql) throws SQLException {
    	QueryRunner runner = getQueryRunner();
        return runner.query(connection, sql, new ArrayListHandler());
    }
  
    /** 
     * 根据sql查询返回所有记录，以List数组形式返回 
     *  
     * @param sql 
     * @param params 
     * @return 
     * @return 
     * @throws SQLException 
     */  
    public static List<Object[]> getListArray(Connection connection, String sql, Object... params) throws SQLException {
    	QueryRunner runner = getQueryRunner();
        return runner.query(connection, sql, new ArrayListHandler(), params);
    }
    public static List<Object[]> getListArray(String dataSourceName, String sql, Object... params) throws SQLException {
    	QueryRunner runner = getConnectionQueryRunner(dataSourceName);
    	return runner.query(sql, new ArrayListHandler(), params);
    }
    public static List<Object[]> getListArray(String sql, Object... params) throws SQLException {
    	QueryRunner runner = getConnectionQueryRunner();
    	return runner.query(sql, new ArrayListHandler(), params);
    }
  
    /** 
     * 根据传入的sql，查询记录，以Map形式返回第一行记录。
     *  注意：如果有多行记录，只会返回第一行，所以适用场景需要注意，可以使用根据主键来查询的场景 
     *  
     * @param sql 
     * @return 
     * @return 
     * @throws SQLException 
     */  
    public static Map<String, Object> getFirstRowMap(Connection connection, String sql) throws SQLException {
    	QueryRunner runner = getQueryRunner();
        return runner.query(connection, sql, new MapHandler());
    }
  
	/** 
     * 根据传入的sql，查询记录，以Map形式返回第一行记录。 
     * 注意：如果有多行记录，只会返回第一行，所以适用场景需要注意，可以使用根据主键来查询的场景 
     *  
     * @param sql 
     * @param params 
     * @return 
     * @return 
     * @throws SQLException 
     */  
    public static Map<String, Object> getFirstRowMap(Connection connection, String sql, Object... params) throws SQLException {
    	QueryRunner runner = getQueryRunner();
        return runner.query(connection, sql, new MapHandler(), params);
    }
    public static Map<String, Object> getFirstRowMap(String dataSourceName, String sql, Object... params) throws SQLException {
    	QueryRunner runner = getConnectionQueryRunner();
    	return runner.query(sql, new MapHandler(), params);
    }
    public static Map<String, Object> getFirstRowMap(String sql, Object... params) throws SQLException {
    	QueryRunner runner = getConnectionQueryRunner();
    	return runner.query(sql, new MapHandler(), params);
    }
  
    /** 
     * 根据传入的sql查询所有记录，以List Map形式返回 
     *  
     * @param sql 
     * @return 
     * @return 
     * @throws SQLException 
     */  
    public static List<Map<String, Object>> getListMap(Connection connection, String sql) throws SQLException {
    	QueryRunner runner = getQueryRunner();
        return runner.query(connection, sql, new MapListHandler());
    }
  
    /** 
     * 根据传入的sql查询所有记录，以List Map形式返回 
     *  
     * @param sql 
     * @param params 
     * @return 
     * @return 
     * @throws SQLException 
     */  
    public static List<Map<String, Object>> getListMap(Connection connection, String sql, Object... params) throws SQLException {
    	QueryRunner runner = getQueryRunner();
        return runner.query(connection, sql, new MapListHandler(), params);
    }
    public static List<Map<String, Object>> getListMap(String dataSourceName, String sql, Object... params) throws SQLException {
    	QueryRunner runner = getConnectionQueryRunner(dataSourceName);
    	return runner.query(sql, new MapListHandler(), params);
    }
    public static List<Map<String, Object>> getListMap(String sql, Object... params) throws SQLException {
    	QueryRunner runner = getConnectionQueryRunner();
    	return runner.query(sql, new MapListHandler(), params);
    }
  
    /** 
     * 根据sql和对象，查询结果并以对象形式返回 
     * @param <T>
     *  
     * @param sql 
     * @param type 
     * @return 
     * @throws SQLException 
     */  
    public static  <T> T getBean(Connection connection, String sql, Class<T> type) throws SQLException {
    	QueryRunner runner = getQueryRunner();
        return runner.query(connection, sql, new BeanHandler<T>(type,rowProcessor));
    }
  
    /** 
     * 根据sql和对象，查询结果并以对象形式返回 
     *  
     * @param sql 
     * @param type 
     * @param params 
     * @return 
     * @throws SQLException 
     */  
    public static <T> T getBean(Connection connection, String sql, Class<T> type, Object... params) throws SQLException {
    	QueryRunner runner = getQueryRunner();
        return runner.query(connection, sql, new BeanHandler<T>(type,rowProcessor), params);
    }
    public static <T> T getBean(String dataSourceName, String sql, Class<T> type, Object... params) throws SQLException {
    	QueryRunner runner = getConnectionQueryRunner(dataSourceName);
    	return runner.query(sql, new BeanHandler<T>(type,rowProcessor), params);
    }
    public static <T> T getBean(String sql, Class<T> type, Object... params) throws SQLException {
    	QueryRunner runner = getConnectionQueryRunner();
    	return runner.query(sql, new BeanHandler<T>(type,rowProcessor), params);
    }
  
    /** 
     * 根据sql查询list对象 
     * @param <T>
     *  
     * @param sql 
     * @param type 
     * @return 
     * @throws SQLException 
     */  
    public static  <T> List<T> getListBean(Connection connection, String sql, Class<T> type) throws SQLException {
    	QueryRunner runner = getQueryRunner();
        return runner.query(connection, sql, new BeanListHandler<T>(type,rowProcessor));
    }
  
    /** 
     * 根据sql查询list对象 
     * @param <T>
     *  
     * @param sql 
     * @param type 
     * @param params 
     * @return 
     * @throws SQLException 
     */  
    public static  <T> List<T> getListBean(Connection connection, String sql, Class<T> type, Object... params) throws SQLException {
    	QueryRunner runner = getQueryRunner();
        return runner.query(connection, sql, new BeanListHandler<T>(type,rowProcessor), params);
    }
    public static  <T> List<T> getListBean(String dataSourceName, String sql, Class<T> type, Object... params) throws SQLException {
    	QueryRunner runner = getConnectionQueryRunner();
    	return runner.query(sql, new BeanListHandler<T>(type,rowProcessor), params);
    }
    
    public static  <T> List<T> getListBean(String sql, Class<T> type, Object... params) throws SQLException {
    	QueryRunner runner = getConnectionQueryRunner();
    	return runner.query(sql, new BeanListHandler<T>(type,rowProcessor), params);
    }
  
    /** 
     * 保存操作 
     *  
     * @param sql 
     * @param params 
     * @return 
     * @throws SQLException 
     */  
    public static int save(Connection connection, String sql, Object... params) throws SQLException {
        return update(connection, sql, params);
    }
    public static int save(String dataSourceName, String sql, Object... params) throws SQLException {
    	return update(dataSourceName, sql, params);
    }
    public static int save(String sql, Object... params) throws SQLException {
    	return update(sql, params);
    }
    
    //ORM
    public static int save(Connection connection, Object obj) throws SQLException, Exception {
    	List<Object> paramList = new ArrayList<>();
		String sql = getObjectSQL(obj, paramList );
    	return save(connection, sql, paramList.toArray());
    }
    public static int save(Object obj) throws SQLException, Exception {
    	List<Object> paramList = new ArrayList<>();
    	String sql = getObjectSQL(obj, paramList );
    	return save(sql, paramList.toArray());
    }
    public static void save(Connection connection, Object ... obj) throws SQLException, Exception {
    	if(obj==null||obj.length<=0) return;
    	String sql = null;
    	List<Object[]> paramLists = new ArrayList<>();
    	for (Object object : obj) {
    		List<Object> paramList = new ArrayList<>();
        	sql = getObjectSQL(object, paramList);
        	paramLists.add(paramList.toArray());
		}
    	batch(connection, sql, paramLists.toArray(new Object[][]{}));
    }
    public static void save(Object ... obj) throws SQLException, Exception {
    	if(obj==null||obj.length<=0) return;
    	String sql = null;
    	List<Object[]> paramLists = new ArrayList<>();
    	for (Object object : obj) {
    		List<Object> paramList = new ArrayList<>();
        	sql = getObjectSQL(object, paramList);
        	paramLists.add(paramList.toArray());
		}
    	batch(sql, paramLists.toArray(new Object[][]{}));
    }
   //ORM
    private static String getObjectSQL(Object obj,List<Object> paramList) throws SQLException, Exception {
    	Class<? extends Object> clazz = obj.getClass();
    	StringBuffer sqlBuffer = new StringBuffer();
    	String tableName = null;
    	Table table = clazz.getAnnotation(Table.class);
    	if(table!=null){
    		tableName = table.value();
    	}
    	if(tableName==null||"".equals(tableName)){
    		tableName = clazz.getSimpleName();
    	}
    	Field[] fields = clazz.getDeclaredFields();
    	sqlBuffer.append("insert into ");
    	sqlBuffer.append(tableName);
    	sqlBuffer.append(" (");
    	for (Field field : fields) {
    		Primary primary = field.getAnnotation(Primary.class);
    		if(primary!=null){
    			if(!primary.insert()){
    				continue;
    			}
    		}
    		Colunm colunm = field.getAnnotation(Colunm.class);
    		boolean all = table.all();
    		if(colunm==null && all){
    			String fieldName = field.getName();
    			sqlBuffer.append(fieldName);
				sqlBuffer.append(",");
				field.setAccessible(true);
				Object fieldValue = field.get(obj);
				paramList.add(fieldValue);
    		}
    		if(colunm!=null){
    			if(colunm.is()){
    				String fieldName = colunm.value();
    				if(fieldName==null||"".equals(fieldName)){
    					fieldName = field.getName();
    				}
    				sqlBuffer.append(fieldName);
    				sqlBuffer.append(",");
    				field.setAccessible(true);
    				Object fieldValue = field.get(obj);
    				paramList.add(fieldValue);
    			}
    		}
		}
    	sqlBuffer.delete(sqlBuffer.lastIndexOf(","), sqlBuffer.length());
    	sqlBuffer.append(") values (");
    	for (int i = 0; i < paramList.size(); i++) {
    		sqlBuffer.append("?,");
		}
    	sqlBuffer.delete(sqlBuffer.lastIndexOf(","), sqlBuffer.length());
    	sqlBuffer.append(")");
    	System.out.println(sqlBuffer.toString());
    	
    	return sqlBuffer.toString();
    }

	/** 
     * 更新操作 
     *  
     * @param sql 
     * @param params 
     * @return 
     * @throws SQLException 
     */  
    public static int update(Connection connection, String sql, Object... params) throws SQLException {
    	QueryRunner runner = getQueryRunner();
        return runner.update(connection, sql, params);
    }
    public static int update(String dataSourceName, String sql, Object... params) throws SQLException {
    	QueryRunner runner = getConnectionQueryRunner(dataSourceName);
    	return runner.update(sql, params);
    }
    public static int update(String sql, Object... params) throws SQLException {
    	QueryRunner runner = getConnectionQueryRunner();
    	return runner.update(sql, params);
    }
  
    /** 
     * 删除操作 
     *  
     * @param sql 
     * @param params 
     * @return 
     * @throws SQLException 
     */  
    public static int delete(Connection connection, String sql, Object... params) throws SQLException {
        return update(connection, sql, params);
    }
    public static int delete(String dataSourceName, String sql, Object... params) throws SQLException {
    	return update(dataSourceName, sql, params);
    }
    public static int delete(String sql, Object... params) throws SQLException {
    	return update(sql, params);
    }
  
    /** 
     * 批量操作，包括批量保存、修改、删除 
     *  
     * @param sql 
     * @param params 
     * @return 
     * @throws SQLException 
     */  
    public static int[] batch(Connection connection, String sql, Object[][] params) throws SQLException {
    	QueryRunner runner = getQueryRunner();
        return runner.batch(connection, sql, params);
    }
    public static int[] batch(String dataSourceName, String sql, Object[][] params) throws SQLException {
    	QueryRunner runner = getConnectionQueryRunner();
    	return runner.batch(sql, params);
    }
    public static int[] batch(String sql, Object[][] params) throws SQLException {
    	QueryRunner runner = getConnectionQueryRunner();
    	return runner.batch(sql, params);
    }
    
    /** 
     * 开启事务 
     */  
    public static void beginTransaction(Connection conn) {
        try {
            // 开启事务  
            conn.setAutoCommit(false);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
  
    /** 
     * 回滚事务 
     */  
    public static void rollback(Connection conn) {
        try {
            conn.rollback();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
  
    /** 
     * 提交事务 
     */  
    public static void commit(Connection conn) {
        try {
            conn.commit();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public static void close(Connection connection) {
        try {
            if (connection != null)  
                connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 关闭连接
     * @param obj
     * @return
     */
    private static int objectToInteger(Object obj) {
        try {
            if (obj != null && !obj.toString().trim().equals(""))  
                return Integer.parseInt(obj.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
            return 0;
        }
        return 0;
    }

}
