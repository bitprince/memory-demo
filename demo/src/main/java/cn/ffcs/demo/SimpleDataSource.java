package cn.ffcs.demo;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

/**
 * 
 * 
 * @Description: 简单的数据源，仅用来脱离容器测试数据访问的代码。
 * @Copyright: Copyright (c) 2013 FFCS All Rights Reserved
 * @Company: 北京福富软件有限公司
 * @author 黄君毅 2013-4-11
 * @version 1.00.00
 * @history:
 * 
 */
public class SimpleDataSource implements DataSource {

	@Override
	public PrintWriter getLogWriter() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setLogWriter(PrintWriter out) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setLoginTimeout(int seconds) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public int getLoginTimeout() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Connection getConnection() throws SQLException {
		return getConnection(username, password);
	}

	@Override
	public Connection getConnection(String username, String password)
			throws SQLException {
		try {
			Class.forName(driver);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return DriverManager.getConnection(url, username, password);
	}

	public static String driver = "com.mysql.jdbc.Driver";
	public static String url = "jdbc:mysql://localhost:3306/test?autoReconnect=true&useUnicode=true&characterEncoding=utf-8&mysqlEncoding=utf8";
	public static String username = "root";
	public static String password = "root";
	@Override
	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		// TODO Auto-generated method stub
		return null;
	}


}
