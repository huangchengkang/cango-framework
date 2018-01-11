package com.cangoframework.base.commons;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 流水号生成器
 * 数据库需要设置大小写不敏感
 */
public class DBKeyHelper {

	private String snTableName = "object_maxsn";

	private DataSource dataSource;

	private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");

	private long iCount = 100000L;

	protected int cachesize;
	
	private String sLockSql = "update "+snTableName+" set MaxSerialNo=MaxSerialNo where TableName=? and ColumnName=? and DateFmt=? and NoFmt=?";
	private String sQuerySql = "select MaxSerialNo from "+snTableName+" where TableName=? and ColumnName=? and DateFmt=? and NoFmt=?";
	private String sUpdateSql = "update "+snTableName+" set MaxSerialNo=? where TableName=? and ColumnName=? and DateFmt=? and NoFmt=?";
	private String sInsertSql = "insert into "+snTableName+" (MaxSerialNo,TableName,ColumnName,DateFmt,NoFmt) values (?,?,?,?,?)";

	private String SCHAR = "@";
	// 固定大小为50的LRU cache；存放50个常用的流水对象
	private Map<String, SerialNoArray> keyCache = new ConcurrentHashMap<String, SerialNoArray>(300);

	public DBKeyHelper() {

	}

	private void initSql(){
		sLockSql = "update "+snTableName+" set MaxSerialNo=MaxSerialNo where TableName=? and ColumnName=? and DateFmt=? and NoFmt=?";
		sQuerySql = "select MaxSerialNo from "+snTableName+" where TableName=? and ColumnName=? and DateFmt=? and NoFmt=?";
		sUpdateSql = "update "+snTableName+" set MaxSerialNo=? where TableName=? and ColumnName=? and DateFmt=? and NoFmt=?";
		sInsertSql = "insert into "+snTableName+" (MaxSerialNo,TableName,ColumnName,DateFmt,NoFmt) values (?,?,?,?,?)";
	}

	/**
	 * 将流水对象放入cache中
	 * 
	 * @param key
	 * @param sa
	 */
	private synchronized void putCache(String key, SerialNoArray sa) {
		keyCache.put(key, sa);
	}

	/**
	 * 如果不是批量区流水的话，就可以设置缓存大小，默认为50
	 * 
	 * @param cachesize
	 */
	public void setCachesize(int cachesize) {
		this.cachesize = cachesize;
	}
	
	public int getCachesize() {
		return cachesize;
	}

	public String getSerialNo() {
		return getSerialNo("T");
	}

	public String getSerialNo(String sPrefix) {
		if (iCount > 999998L)
			iCount = 100000L;
		return sPrefix + sdf.format(new Date()) + iCount++;
	}

	public String getUUID() {
		return UUID.randomUUID().toString().replace("_", "");
	}

	public String getSerialNo(String sTable, String sColumn)
			throws Exception {
		return getSerialNo(sTable, sColumn, "yyyyMMdd", "00000000", new Date());
	}

	public String[] getSerialNoArray(String sTable, String sColumn)
			throws Exception {
		return getSerialNoArray(sTable, sColumn, "yyyyMMdd", "00000000",
				new Date(), 10);
	}

	public String[] getSerialNoArray(String sTable, String sColumn,
									 int iLen) throws Exception {
		return getSerialNoArray(sTable, sColumn, "yyyyMMdd", "00000000",
				new Date(), iLen);
	}

	public String getSerialNo(String sTable, String sColumn,
							  String sPrefix) throws Exception {
		if ((sPrefix == null) || (sPrefix.equals("")))
			sPrefix = "";
		else {
			sPrefix = "'" + sPrefix + "'";
		}
		return getSerialNo(sTable, sColumn, sPrefix + "yyyyMMdd", "00000000",
				new Date());
	}

	public String[] getSerialNoArray(String sTable, String sColumn,
									 String sPrefix, int iLen) throws Exception {
		if ((sPrefix == null) || (sPrefix.equals("")))
			sPrefix = "";
		else {
			sPrefix = "'" + sPrefix + "'";
		}
		return getSerialNoArray(sTable, sColumn, sPrefix + "yyyyMMdd",
				"00000000", new Date(), iLen);
	}

	public String getSerialNo(String sTable, String sColumn,
							  String sDateFmt, String sNoFmt, Date today) throws Exception {
		String sNewSerialNo = "DBERROR";
		// 以sTable@sColumn@sDateFmt@sNoFmt 为缓存的key值
		StringBuffer sb = new StringBuffer(sTable).append(SCHAR);
		sb.append(sColumn).append(SCHAR).append(sDateFmt).append(SCHAR)
				.append(sNoFmt);

		// 从缓存中取流水号
		SerialNoArray sa = keyCache.get(sb.toString());
		if (sa == null) {// 为空则新建
			sa = new SerialNoArray(this,sTable, sColumn, sDateFmt, sNoFmt);
			putCache(sb.toString(), sa);
		}
		try {
			sNewSerialNo = sa.getNextSerilNo();
		} catch (Exception e) {
			keyCache.remove(sb.toString());
			error("===get SerialNo from cache ERROR ====" + sTable, e);
			throw e;
		}
		return sNewSerialNo;
	}

	public String[] getSerialNoArray(String sTable, String sColumn,
									 String sDateFmt, String sNoFmt, Date today, int iLen)
			throws Exception {
		Connection conn = getConnection();
		String[] sReturnSerialNo;
		try {
			if (conn == null)
				throw new Exception("数据库连接无法成功获取...");
			// 锁定，同一表取流水号按照顺序来
			String tLock = String.valueOf(sTable);
			synchronized (tLock) {
				sReturnSerialNo = getSerialNoByCon(sTable, sColumn, sDateFmt,
						sNoFmt, today, iLen, conn);
			}

		} catch (Exception e) {
			throw new Exception("getSerialNo...失败！" + e.getMessage());
		} finally {
			if (conn != null)
				conn.close();
		}
		return sReturnSerialNo;
	}

	private String[] getSerialNoByCon(String sTable, String sColumn,
									  String sDateFmt, String sNoFmt, Date today, int iLen,
									  Connection Sqlca) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat(sDateFmt);
		DecimalFormat df = new DecimalFormat(sNoFmt);
		String sPrefixDate = sdf.format(today);
		int iPrefixLen = sPrefixDate.length();
		String sNewSerialNo = "DBERROR";

		//使用转换
		sTable = sTable.toLowerCase();
		sColumn = sColumn.toLowerCase();

		long iMaxNo = 0L;
		int iStep = iLen;
		if (iStep > 500) {
			iStep = 500;
			log("要求getSerialNo一次取[" + iLen + "]个,系统限制一次最多取[" + iStep + "]个!");
		}

		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = Sqlca.prepareStatement(sLockSql);
			ps.setString(1, sTable);
			ps.setString(2, sColumn);
			ps.setString(3, sDateFmt);
			ps.setString(4, sNoFmt);
			ps.execute();
			ps.close();

			ps = Sqlca.prepareStatement(sQuerySql);
			ps.setString(1, sTable);
			ps.setString(2, sColumn);
			ps.setString(3, sDateFmt);
			ps.setString(4, sNoFmt);
			rs = ps.executeQuery();
			if (rs.next()) {
				String sMaxSerialNo = rs.getString(1);
				rs.close();
				iMaxNo = 0L;

				if ((sMaxSerialNo != null)
						&& (sMaxSerialNo.indexOf(sPrefixDate, 0) != -1))
					iMaxNo = Integer
							.valueOf(sMaxSerialNo.substring(iPrefixLen))
							.intValue();
				else {
					iMaxNo = getMaxlNoFromTable(sTable, sColumn, sPrefixDate,
							Sqlca);
				}

				sNewSerialNo = sPrefixDate + df.format(iMaxNo + iStep);
				
				ps = Sqlca.prepareStatement(sUpdateSql);
				ps.setString(1, sNewSerialNo);
				ps.setString(2, sTable);
				ps.setString(3, sColumn);
				ps.setString(4, sDateFmt);
				ps.setString(5, sNoFmt);
				ps.executeUpdate();
				ps.close();

			} else {
				rs.close();
				ps.close();
				iMaxNo = getMaxlNoFromTable(sTable, sColumn, sPrefixDate, Sqlca);
				sNewSerialNo = sPrefixDate + df.format(iMaxNo + iStep);
				
				ps = Sqlca.prepareStatement(sInsertSql);
				ps.setString(1, sNewSerialNo);
				ps.setString(2, sTable);
				ps.setString(3, sColumn);
				ps.setString(4, sDateFmt);
				ps.setString(5, sNoFmt);
				ps.execute();
				ps.close();
			}
			Sqlca.commit();
		} catch (Exception e) {
			Sqlca.rollback();
			error("getSerialNo...失败[" + e.getMessage() + "]!", e);
		} finally {
			if (rs != null)
				rs.close();
			if (ps != null)
				ps.close();
		}

		String[] sReturnSerialNo = new String[iStep];
		if (iStep == 1) {
			sReturnSerialNo[0] = sNewSerialNo;
		} else {
			for (int i = 0; i < sReturnSerialNo.length; i++) {
				sReturnSerialNo[i] = (sPrefixDate + df.format(iMaxNo + i + 1));
			}
		}
		return sReturnSerialNo;
	}

	private long getMaxlNoFromTable(String sTable, String sColumn,
									String sPrefix, Connection Sqlca) {
		int iPrefixLen = sPrefix.length();
		log("InitTableSerialNo[" + sTable + "][" + sColumn + "][" + sPrefix
				+ "]");
		String sSql = "select max(" + sColumn + ") from " + sTable + " where "
				+ sColumn + " like " + "?" ;
		ResultSet rsTemp = null;
		PreparedStatement ps = null;
		String sMaxSerialNo = "";
		long iMaxNo = 0L;
		try {
			ps = Sqlca.prepareStatement(sSql);
			ps.setString(1, sPrefix + "%");
			rsTemp = ps.executeQuery();
			if (rsTemp.next()) {
				sMaxSerialNo = rsTemp.getString(1);
				if (sMaxSerialNo != null)
					iMaxNo = Integer
							.valueOf(sMaxSerialNo.substring(iPrefixLen))
							.intValue();
				else
					sMaxSerialNo = "NoMaxSerialNo";
			}
		} catch (SQLException e) {
			iMaxNo = 10000L;
			error("getMaxlNoFromTable[" + sTable + "][" + sColumn + "]["
					+ sPrefix + "]=[" + sMaxSerialNo + "][" + iMaxNo + "]", e);
		} catch (NumberFormatException e1) {
			iMaxNo = 50000L;
			error("getMaxlNoFromTable[" + sTable + "][" + sColumn + "]["
					+ sPrefix + "]=[" + sMaxSerialNo + "][" + iMaxNo + "]", e1);
		} finally {
			try {
				if (rsTemp != null)
					rsTemp.close();
				if (ps != null)
					ps.close();
			} catch (SQLException e) {
			}
		}
		log("getMaxlNoFromTable[" + sTable + "][" + sColumn + "][" + sPrefix
				+ "]=[" + sMaxSerialNo + "][" + iMaxNo + "]");
		return iMaxNo;
	}

	private Logger logger = LoggerFactory.getLogger(DBKeyHelper.class);

	private void error(String str, Exception e) {
		this.logger.error(str, e);
	}
	private void log(String str) {
		logger.info(str);
	}

	private Connection getConnection() {
		Connection connection = null;
		try {
			connection = dataSource.getConnection();
			connection.setAutoCommit(false);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return connection;
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public  void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public String getSnTableName() {
		return snTableName;
	}

	public void setSnTableName(String snTableName) {
		this.snTableName = snTableName;
		//动态改变SQL语句的表名称
		initSql();
	}
}

/**
 * 流水号对象
 * 
 * @author kancy
 * 
 */
class SerialNoArray {
	int cachesize;
	int icurrent;
	String[] sArray = null;
	String sTable, sColumn, sDateFmt, sNoFmt;
	private DBKeyHelper keyHelper;

	SerialNoArray(DBKeyHelper keyHelper, String sTable, String sColumn, String sDateFmt, String sNoFmt) {
		this.sTable = sTable;
		this.sColumn = sColumn;
		this.sDateFmt = sDateFmt;
		this.sNoFmt = sNoFmt;
		this.keyHelper = keyHelper;
		this.cachesize = keyHelper.getCachesize();
		icurrent = cachesize;
	}

	/**
	 * 获取流水号
	 * 
	 * @return
	 * @throws Exception
	 */
	synchronized String getNextSerilNo() throws Exception {
		Date today = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat(sDateFmt);
		String sPrefixDate = sdf.format(today);
		// icurrent大于等于最大值时，重新获取流水号
		if (icurrent >= cachesize
				|| (sArray != null && icurrent >= sArray.length)) {
			sArray = keyHelper.getSerialNoArray(sTable, sColumn, sDateFmt,
					sNoFmt, today, cachesize);
			icurrent = 1;
			return sArray[0];
		}
		String sMaxSerialNo = sArray[icurrent++];
		// 判断是否切日
		if ((sMaxSerialNo != null)
				&& (sMaxSerialNo.indexOf(sPrefixDate, 0) != -1)) {
			return sMaxSerialNo;
		} else {
			sArray =  keyHelper.getSerialNoArray(sTable, sColumn, sDateFmt,
					sNoFmt, today, cachesize);
			icurrent = 1;
			return sArray[0];
		}
	}
	
}
